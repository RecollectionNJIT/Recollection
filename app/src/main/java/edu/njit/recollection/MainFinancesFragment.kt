package edu.njit.recollection

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.DecimalFormat

class MainFinancesFragment : Fragment() {
    private lateinit var view: View
    private lateinit var rvFinance: RecyclerView
    private lateinit var adapter: FinanceMonthAdapter
    private val items = mutableListOf<FinanceMonth>()
    private val entries = mutableListOf<FinanceEntry>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_finances, container, false)

        // Theme for Fragment
        requireActivity().window.statusBarColor = ContextCompat.getColor(view.context, R.color.finance)
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(view.context, R.color.finance_dark))
        bottomNavigationView.itemActiveIndicatorColor = ContextCompat.getColorStateList(view.context, R.color.finance)


        // Set up Alltime Finances Button
        view.findViewById<Button>(R.id.btnSeeAlltimeFinances).setOnClickListener {
            val options : ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), androidx.core.util.Pair(view.findViewById<TextView>(R.id.tvFinanceHeader), "header"))
            val i = Intent(view.context, DetailsActivity::class.java)
            i.putExtra("fragment", "finances")
            i.putExtra("type", "alltime")
            startActivity(i, options.toBundle())
        }

        // Initialize the RecyclerView
        rvFinance = view.findViewById<RecyclerView>(R.id.rvFinance)
        rvFinance.layoutManager = LinearLayoutManager(view.context)
        adapter = FinanceMonthAdapter(view.context, items)
        rvFinance.adapter = adapter

        // Set up Add Button
        view.findViewById<ImageButton>(R.id.btnAddFinances).setOnClickListener {
            val options : ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), androidx.core.util.Pair(view.findViewById<TextView>(R.id.tvFinanceHeader), "header"))
            val i = Intent(view.context, AddActivity::class.java)
            i.putExtra("fragment", "finances")
            startActivity(i, options.toBundle())
        }
        return view
    }
    override fun onStart() {
        super.onStart()
        // Update RecyclerView & Create Pie Chart
        updateUI()
    }
    companion object {
        fun newInstance() : MainFinancesFragment {
            return MainFinancesFragment()
        }
    }
    fun updateUI() {
        items.clear()
        entries.clear()
        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference
        databaseRef.child("users").child(auth.uid!!).child("finances")
            .orderByChild("date").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val months = mutableListOf<String>()
                    for (child in snapshot.children) {
                        val month = child.child("monthDate").getValue().toString()
                        // Add Entry to Entries List for PieChart
                        val entry = FinanceEntry(
                            child.child("date").getValue().toString(),
                            month,
                            child.child("type").getValue().toString(),
                            child.child("category").getValue().toString(),
                            child.child("amount").getValue().toString().toDouble(),
                            child.key
                        )
                        entries.add(entry)

                        // Add unique months to months list for RecyclerView
                        if (month in months) {
                            continue
                        }
                        months.add(month)
                        items.add(FinanceMonth(month))
                        adapter.notifyDataSetChanged()
                    }
                    createPieChart(view)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebaseFinanceMain", "error", Throwable(error.toString()))
                }
            })
    }
    fun createPieChart(view: View) {
        val pieChart = view.findViewById<PieChart>(R.id.mainPieChart)

        pieChart.setUsePercentValues(false)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(0f, 20f, 0f, 20f)
        pieChart.setMinAngleForSlices(15f)
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(ContextCompat.getColor(view.context, R.color.finance_bg))
        pieChart.setTransparentCircleColor(ContextCompat.getColor(view.context, R.color.finance_bg))
        pieChart.setTransparentCircleAlpha(110)
        pieChart.setHoleRadius(55f)
        pieChart.setTransparentCircleRadius(58f)

        pieChart.setDrawCenterText(false)
        pieChart.setHighlightPerTapEnabled(true)
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(ContextCompat.getColor(view.context, R.color.white))
        pieChart.setEntryLabelTextSize(12f)

        // Calculate Pie Slice values, and enter them.
        var rentTotal = 0.00
        var groceriesTotal = 0.00
        var diningTotal = 0.00
        var billsTotal = 0.00
        var entertainmentTotal = 0.00
        var otherTotal = 0.00
        for (entry in entries) {
            if (entry.type == "Income")
                continue
            if (entry.category == "Rent")
                rentTotal += entry.amount!!
            else if (entry.category == "Groceries")
                groceriesTotal += entry.amount!!
            else if (entry.category == "Dining")
                diningTotal += entry.amount!!
            else if (entry.category == "Bills")
                billsTotal += entry.amount!!
            else if (entry.category == "Entertainment")
                entertainmentTotal += entry.amount!!
            else
                otherTotal += entry.amount!!
        }
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()
        if (rentTotal != 0.0) {
            pieEntries.add(PieEntry(rentTotal.toFloat()))
            colors.add(ContextCompat.getColor(view.context, R.color.space_cadet))
        }
        if (groceriesTotal != 0.0) {
            pieEntries.add(PieEntry(groceriesTotal.toFloat()))
            colors.add(ContextCompat.getColor(view.context, R.color.mantis))
        }
        if (diningTotal != 0.0) {
            pieEntries.add(PieEntry(diningTotal.toFloat()))
            colors.add(ContextCompat.getColor(view.context, R.color.metallic_gold))
        }
        if (billsTotal != 0.0) {
            pieEntries.add(PieEntry(billsTotal.toFloat()))
            colors.add(ContextCompat.getColor(view.context, R.color.magenta_haze))
        }
        if (entertainmentTotal != 0.0) {
            pieEntries.add(PieEntry(entertainmentTotal.toFloat()))
            colors.add(ContextCompat.getColor(view.context, R.color.cerise))
        }
        if (otherTotal != 0.0) {
            pieEntries.add(PieEntry(otherTotal.toFloat()))
            colors.add(ContextCompat.getColor(view.context, R.color.apricot))
        }

        val dataSet = PieDataSet(pieEntries, "Expenses")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.colors = colors

        // Value lines
        dataSet.valueLinePart1Length = 0.6f
        dataSet.valueLinePart2Length = 0.3f
        dataSet.valueLineWidth = 2f
        dataSet.valueLinePart1OffsetPercentage = 10f  // Line starts outside of chart
        dataSet.isUsingSliceColorAsValueLineColor = true

        // Value text appearance
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.valueTextSize = 16f
        dataSet.valueTypeface = Typeface.DEFAULT_BOLD

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(ContextCompat.getColor(view.context, R.color.white))
        val formatter: ValueFormatter = object : ValueFormatter() {
            val mFormat = DecimalFormat("###,###,##0.00")
            override fun getFormattedValue(value:Float, entry: Entry, dataSetIndex:Int, viewPortHandler: ViewPortHandler): String {
                return if (value == 0f) {
                    ""
                }
                else
                    return "$" + mFormat.format(value.toDouble())
            }
            override fun getFormattedValue(value: Float): String {
                return if (value == 0f) {
                    ""
                }
                else
                    return "$" + mFormat.format(value.toDouble())
            }
        }
        data.setValueFormatter(formatter)
        pieChart.setData(data)
        pieChart.data.setValueFormatter(formatter)

        pieChart.highlightValues(null)
        pieChart.invalidate()
    }
}