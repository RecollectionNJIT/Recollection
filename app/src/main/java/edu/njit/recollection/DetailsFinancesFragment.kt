package edu.njit.recollection

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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


class DetailsFinancesFragment : Fragment() {
    private val entries = mutableListOf<FinanceEntry>()
    private lateinit var view: View
    private lateinit var financeEntryTable: TableLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_details_finances, container, false)
        val type = activity?.intent?.extras?.getString("type")
        financeEntryTable = view.findViewById<TableLayout>(R.id.financeEntryTable)

        // Change UI text to match the caller
        if (type != "alltime") {
            view.findViewById<TextView>(R.id.tvFinanceDetailsHeader).text = "Viewing: " + type
        }

        // Get all entries from database, sorted by date.
        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference
        databaseRef.child("users").child(auth.uid!!).child("finances")
            .orderByChild("date").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    entries.clear()
                    financeEntryTable.removeViews(1, Math.max(0, financeEntryTable.getChildCount() - 1));
                    for (child in snapshot.children) {
                        // Depending on the button press leading us to DetailActivity, we need different entries.
                        if (type != "alltime" && child.child("monthDate").getValue().toString() != type)
                            continue
                        else {
                            val entry = FinanceEntry(
                                child.child("date").getValue().toString(),
                                child.child("monthDate").getValue().toString(),
                                child.child("type").getValue().toString(),
                                child.child("category").getValue().toString(),
                                child.child("amount").getValue().toString().toDouble(),
                                child.key,
                                child.child("addToCalendar").getValue().toString().toBoolean()
                            )
                            entries.add(entry)
                            // Create row for entry in table
                            addRow(view, entry)
                        }
                    }
                    createPieChart(view)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebaseFinanceAdd", "error", Throwable(error.toString()))
                }
            })

        return view
    }
    fun addRow(view:View, entry: FinanceEntry) {
        // Create new Row
        val newTableRow = TableRow(view.context)
        newTableRow.background = ContextCompat.getDrawable(view.context, R.color.white)

        // Create new TextViews
        val dateTV = TextView(view.context)
        val typeTV = TextView(view.context)
        val categoryTV = TextView(view.context)
        val amountTV = TextView(view.context)

        // Get info from Finance Entry and style the TextViews
        dateTV.text = entry.date
        dateTV.gravity = Gravity.CENTER_HORIZONTAL
        dateTV.setTextColor(ContextCompat.getColor(view.context, R.color.teal))
        dateTV.textSize = 15F
        dateTV.setPadding(10)
        newTableRow.addView(dateTV)

        typeTV.text = entry.type
        typeTV.gravity = Gravity.CENTER_HORIZONTAL
        typeTV.setTextColor(ContextCompat.getColor(view.context, R.color.teal))
        typeTV.textSize = 15F
        typeTV.setPadding(10)
        newTableRow.addView(typeTV)

        categoryTV.text = entry.category
        categoryTV.gravity = Gravity.CENTER_HORIZONTAL
        categoryTV.setTextColor(ContextCompat.getColor(view.context, R.color.teal))
        categoryTV.textSize = 15F
        categoryTV.setPadding(10)
        newTableRow.addView(categoryTV)

        amountTV.text = "$" + String.format("%.2f", entry.amount)
        amountTV.gravity = Gravity.CENTER_HORIZONTAL
        amountTV.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
        amountTV.setTextColor(ContextCompat.getColor(view.context, R.color.teal))
        amountTV.textSize = 15F
        amountTV.setPadding(10)

        newTableRow.isClickable = true
        newTableRow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val i = Intent(view.context, AddActivity::class.java)
                i.putExtra("fragment", "finances")
                i.putExtra("edit", true)
                i.putExtra("editEntry", entry)
                startActivity(i)
            }
        })
        newTableRow.addView(amountTV)
        newTableRow.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                val auth = FirebaseAuth.getInstance()
                Firebase.database.reference.child("users").child(auth.uid!!).child("finances").child(entry.key!!).removeValue()
                financeEntryTable.removeView(newTableRow)
                Toast.makeText(view.context, "Entry Deleted!", Toast.LENGTH_SHORT).show()
                return true
            }
        })
        financeEntryTable.addView(newTableRow)
    }
    fun createPieChart(view: View) {
        val pieChart = view.findViewById<PieChart>(R.id.detailsPieChart)

        pieChart.setUsePercentValues(false)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(0f, 20f, 0f, 20f)
        pieChart.setMinAngleForSlices(15f)
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(ContextCompat.getColor(view.context, R.color.teal))
        pieChart.setTransparentCircleColor(ContextCompat.getColor(view.context, R.color.teal))
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
            override fun getFormattedValue(value: Float): String? {
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
    companion object {
        fun newInstance() : DetailsFinancesFragment {
            return DetailsFinancesFragment()
        }
    }
}