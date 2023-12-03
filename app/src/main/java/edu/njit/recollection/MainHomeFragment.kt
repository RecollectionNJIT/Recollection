package edu.njit.recollection

import android.content.Intent
import android.icu.lang.UProperty.INT_START

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.DecimalFormat

class MainHomeFragment : Fragment() {
    lateinit var calendarCV: CardView
    lateinit var financeCV: CardView
    lateinit var remindersCV: CardView
    lateinit var weatherCV: CardView

    private val financeEntries = mutableListOf<FinanceEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_home, container, false)

        view.findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            val i = Intent(view.context, SettingsActivity::class.java)
            startActivity(i)
        }

        calendarCV = view.findViewById(R.id.cvHomepageCalendar)
        financeCV = view.findViewById(R.id.cvHomepageFinance)
        remindersCV = view.findViewById(R.id.cvHomepageReminders)
        weatherCV = view.findViewById(R.id.cvHomepageWeather)

        createCalendarCV()
        createFinanceCV(view)
        createRemindersCV()
        createWeatherCV()

        return view
    }

    fun createCalendarCV() {
    }
    fun createFinanceCV(view: View) {
        financeEntries.clear()
        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference
        var filterMonth = ""
        databaseRef.child("users").child(auth.uid!!).child("finances")
            .orderByChild("date").limitToLast(1).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        filterMonth = child.child("monthDate").getValue().toString()
                    }
                    databaseRef.child("users").child(auth.uid!!).child("finances")
                        .orderByChild("date").addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (child in snapshot.children) {
                                    if (child.child("monthDate").getValue().toString() != filterMonth)
                                        continue
                                    val financeEntry = FinanceEntry(
                                        child.child("date").getValue().toString(),
                                        filterMonth,
                                        child.child("type").getValue().toString(),
                                        child.child("category").getValue().toString(),
                                        child.child("amount").getValue().toString().toDouble(),
                                        child.key
                                    )
                                    financeEntries.add(financeEntry)
                                }
                                createBarChart(view, filterMonth)
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Log.e("firebaseFinanceHomepage2", "error", Throwable(error.toString()))
                            }
                        })
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebaseFinanceHomepage1", "error", Throwable(error.toString()))
                }
            })
        financeCV.setOnClickListener{
            replaceFragment(MainFinancesFragment())
            bottomNavigationView.selectedItemId = R.id.nav_finances
        }
    }
    fun createBarChart(view: View, filterMonth: String) {
        val barChart = view.findViewById<HorizontalBarChart>(R.id.homepageBarChart)

        val description = Description()
        description.text = ""
        barChart.description = description

        view.findViewById<TextView>(R.id.tvFinanceChartHeader).text = filterMonth


        barChart.legend.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setScaleEnabled(false)
        barChart.setDrawValueAboveBar(false)
        barChart.setTouchEnabled(false)

        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.isEnabled = true
        barChart.xAxis.setDrawAxisLine(false)
        barChart.xAxis.textSize = 12f
        barChart.xAxis.textColor = ContextCompat.getColor(view.context, R.color.teal)

        barChart.axisLeft.isEnabled = false
        barChart.axisLeft.setDrawZeroLine(true)
        barChart.axisLeft.granularity = 1f
        barChart.axisLeft.axisMinimum = 0f

        barChart.axisRight.setDrawAxisLine(true)
        barChart.axisRight.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false

        barChart.xAxis.setLabelCount(3, false)
        val formatterX: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return when (value) {
                    0f -> "Expenses"
                    .5f -> "Income"
                    else -> ""
                }
            }
        }
        barChart.xAxis.valueFormatter = formatterX

        var expenseTotal = 0.0
        var incomeTotal = 0.0
        for (entry in financeEntries) {
            if (entry.type == "Expense")
                expenseTotal += entry.amount!!
            else
                incomeTotal += entry.amount!!
        }
        if (expenseTotal > incomeTotal)
            view.findViewById<TextView>(R.id.tvFinanceChartDesc).text = "Oh no! Your expenses are higher than your income this month!"
        else if (expenseTotal == incomeTotal)
            view.findViewById<TextView>(R.id.tvFinanceChartDesc).text = "Your income was exactly enough for your expenses this month, nice!"

        val barEntries: ArrayList<BarEntry> = ArrayList()
        barEntries.add(BarEntry(0f, expenseTotal.toFloat()))
        barEntries.add(BarEntry(.5f, incomeTotal.toFloat()))

        val dataset = BarDataSet(barEntries, "Monthly Income & Expenses")
        dataset.valueTextSize = 12f
        dataset.setColors(
            ContextCompat.getColor(view.context, R.color.burgandy),
            ContextCompat.getColor(view.context, R.color.forest_green)
        )
        dataset.setValueTextColors(mutableListOf(ContextCompat.getColor(view.context, R.color.white), ContextCompat.getColor(view.context, R.color.white)))
        val formatterDataSet: ValueFormatter = object : ValueFormatter() {
            val mFormat = DecimalFormat("###,###,##0.00")
            override fun getFormattedValue(value: Float): String? {
                return if (value == 0f) {
                    ""
                }
                else
                    return "$" + mFormat.format(value.toDouble())
            }
        }
        dataset.valueFormatter = formatterDataSet

        val data = BarData(dataset)
        data.barWidth = .4f
        barChart.data = data

        barChart.animateY(1400, Easing.EaseInOutQuad)

        barChart.invalidate()
    }
    fun createRemindersCV() {
    }
    fun createWeatherCV() {
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.main_frame_layout, fragment)
        fragmentTransaction?.commit()
    }
    companion object {
        fun newInstance() : MainHomeFragment {
            return MainHomeFragment()
        }
    }
}