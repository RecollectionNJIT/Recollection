package edu.njit.recollection

import android.content.Intent

import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
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
import java.time.LocalDate

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

        createCalendarCV(view)
        createFinanceCV(view)
        createRemindersCV(view)
        createWeatherCV()

        return view
    }

    fun createCalendarCV(view: View) {
        val recycler = calendarCV.findViewById<RecyclerView>(R.id.homeCalendarRecyclerView)
        // do stuff here
        var days = mutableListOf<CalendarEntry>()
        days.clear()
        val adapter = HomeCalendarAdapter(view.context, days)
        recycler.adapter = adapter

        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference

        Log.v("Homepage Calendar","" + LocalDate.now().toString());
        databaseRef.child("users").child(auth.uid!!).child("calendar").orderByChild("date").equalTo(LocalDate.now().toString()).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                days.clear()
                for (child in snapshot.children) {
                    val date = child.child("date").getValue().toString()
                    val title = child.child("title").getValue().toString()
                    val description = child.child("description").getValue().toString()
                    val start = child.child("timeStart").getValue().toString()
                    val end = child.child("timeEnd").getValue().toString()
                    val key = child.key
                    val event = CalendarEntry(date, title, description, start, end, key)
                    event.addToReminders = child.child("addToReminders").getValue().toString().toBoolean()
                    event.addToNotes = child.child("addToNotes").getValue().toString().toBoolean()
                    event.addToFinances = child.child("addToFinances").getValue().toString().toBoolean()
                    days.add(event)
                }
                Log.v("days", ""+days.toString())
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebaseCalendarMain", "error", Throwable(error.toString()))
            }
        })

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

    fun createRemindersCV(view: View) {
        val recycler = remindersCV.findViewById<RecyclerView>(R.id.homeRemindersRecyclerView)
        val reminders = mutableListOf<Reminders>()
        reminders.clear()

        val adapter = RemindersAdapter(view.context, reminders)
        recycler.adapter = adapter

        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference

        databaseRef.child("users").child(auth.uid!!).child("reminders").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reminders.clear()
                for (child in snapshot.children) {
                    val title = child.child("title").getValue().toString()
                    val description = child.child("description").getValue().toString()
                    val date = child.child("date").getValue().toString()
                    val time = child.child("time").getValue().toString()
                    val id = child.key
                    val reminder = Reminders(title, description, date, time, id)
                    reminders.add(reminder)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebaseRemindersMain", "error", Throwable(error.toString()))
            }
        })
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