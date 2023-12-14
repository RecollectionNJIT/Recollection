package edu.njit.recollection

import WeatherValue
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
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
import okhttp3.Headers
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat
import java.time.LocalDate
import java.util.Locale


class MainHomeFragment : Fragment() {
    lateinit var calendarCV: CardView
    lateinit var financeCV: CardView
    lateinit var remindersCV: CardView
    lateinit var weatherCV: CardView
    lateinit var titleView: TextView

    private lateinit var rvWeather: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter
    private val weatherVals = mutableListOf<WeatherValue>()

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

        requireActivity().window.statusBarColor = ContextCompat.getColor(view.context, R.color.teal)
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(view.context, R.color.dark_teal))
        bottomNavigationView.itemActiveIndicatorColor = ContextCompat.getColorStateList(view.context, R.color.teal)

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
        createWeatherCV(view)

        return view
    }

    fun createCalendarCV(view: View) {
        val recycler = calendarCV.findViewById<RecyclerView>(R.id.homeCalendarRecyclerView)
        val text = calendarCV.findViewById<TextView>(R.id.homeCalendarNoEvent)
        text.visibility=View.INVISIBLE
        // do stuff here
        var days = mutableListOf<CalendarEntry>()
        days.clear()
        val adapter = HomeCalendarAdapter(view.context, days, activity)
        recycler.adapter = adapter

        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference

        Log.v("Homepage Calendar","" + LocalDate.now().toString());
        databaseRef.child("users").child(auth.uid!!).child("calendar").orderByChild("date").equalTo(LocalDate.now().toString()).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                text.visibility=View.INVISIBLE
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
                if(days.size == 0){
                    recycler.visibility=View.INVISIBLE
                    text.visibility=View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebaseCalendarMain", "error", Throwable(error.toString()))
            }
        })

        recycler.setOnClickListener {
            replaceFragment(MainCalendarFragment())
            bottomNavigationView.selectedItemId = R.id.nav_calendar
        }

        calendarCV.setOnClickListener {
            replaceFragment(MainCalendarFragment())
            bottomNavigationView.selectedItemId = R.id.nav_calendar
        }
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
        val text = remindersCV.findViewById<TextView>(R.id.homeRemindersNoEvent)
        val reminders = mutableListOf<Reminders>()
        reminders.clear()

        val adapter = HomeRemindersAdapter(view.context, reminders, activity)
        recycler.adapter = adapter

        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference

        val newFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        val oldFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        databaseRef.child("users").child(auth.uid!!).child("reminders").orderByChild("date").equalTo(newFormat.format(oldFormat.parse(LocalDate.now().toString()))).addValueEventListener(object :
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
                    reminder.addToCal = child.child("addToCal").getValue().toString().toBoolean()
                    reminder.addToNotes = child.child("addToNotes").getValue().toString().toBoolean()
                    reminders.add(reminder)
                }
                // Check if reminders list is empty
                if (reminders.isEmpty()) {
                    // If empty, add a default reminder with the message
//                    val defaultReminder = Reminders("", "You have no reminders to show!", "", "", null)
//                    reminders.add(defaultReminder)
                    text.visibility = View.VISIBLE
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("firebaseRemindersMain", "error", Throwable(error.toString()))
            }
        })
        recycler.setOnClickListener {
            replaceFragment(MainRemindersFragment())
            bottomNavigationView.selectedItemId = R.id.nav_reminders
        }
        remindersCV.setOnClickListener {
            replaceFragment(MainRemindersFragment())
            bottomNavigationView.selectedItemId = R.id.nav_reminders
        }
        text.setOnClickListener {
            replaceFragment(MainRemindersFragment())
            bottomNavigationView.selectedItemId = R.id.nav_reminders
        }
    }
    fun createWeatherCV(view: View) {
        val apiLatLong = "http://ip-api.com/json/"
        val client = AsyncHttpClient()
        client.get(apiLatLong, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers?,
                json: JsonHttpResponseHandler.JSON
            ) {
                Log.v("WeatherAPI", "Info Sent in first API call")

                try {
                    val lat = json.jsonObject.getDouble("lat").toString()
                    val long = json.jsonObject.getDouble("lon").toString()
                    val town = json.jsonObject.getString("city")
                    val region = json.jsonObject.getString("regionName")
                    titleView = view!!.findViewById(R.id.tvWeatherCV)
                    titleView.text = "$town, $region"

                    getWeather(lat, long)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                if (headers != null) {
                    Log.e("APIError", "Failed to retrieve data from the first API call. Status code: $statusCode, Response: $response")
                } else {
                    Log.e("APIError", "Failed to retrieve data from the first API call. Headers are null. Status code: $statusCode, Response: $response")
                }
            }
        })
    }
    private fun getWeather(lat: String, long: String) {
        val firstAPIcall = "https://api.weather.gov/points/$lat,$long"
        val client = AsyncHttpClient()

        client.get(firstAPIcall, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                Log.v("WeatherAPI","Info Sent in first API call")

                try {
                    val properties = json.jsonObject.getJSONObject("properties")
                    val secondAPIcall = properties.getString("forecast")
                    makeSecondAPICall(secondAPIcall)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers,
                response: String,
                throwable: Throwable
            ) {
                Log.e("APIError", "Failed to retrieve data from the first API call")
            }
        })
    }
    private fun makeSecondAPICall(secondAPIcall: String) {
        val client = AsyncHttpClient()

        client.get(secondAPIcall, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                Log.v("WeatherAPI","Info Sent in second API call")

                try {
                    val weatherList = parseWeatherResponse(json.jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers,
                response: String,
                throwable: Throwable
            ) {
                Log.e("SecondAPIError", "Failed to retrieve data from the second API call")
            }
        })
    }
    private fun parseWeatherResponse(json: JSONObject): List<WeatherValue> {
        val periodsArray = json.getJSONObject("properties").getJSONArray("periods")
        weatherVals.clear()

        for (i in 0 until minOf(periodsArray.length(), 14)) {
            val periodObject = periodsArray.getJSONObject(i)

            val number = periodObject.getInt("number")
            val name = periodObject.getString("name")
            val isDaytime = periodObject.getBoolean("isDaytime")
            val temperature = periodObject.getInt("temperature")
            val shortForecast = periodObject.getString("shortForecast")

            val weatherValue = WeatherValue(number, name, isDaytime, temperature, shortForecast)
            weatherVals.add(weatherValue)
        }
        updateWeatherRecyclerView()

        return weatherVals
    }
    private fun updateWeatherRecyclerView() {
        rvWeather = view?.findViewById(R.id.homeWeatherRecyclerView)!!
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvWeather.layoutManager = layoutManager
        weatherAdapter = WeatherAdapter(requireView().context, weatherVals)
        rvWeather.adapter = weatherAdapter
        weatherAdapter.notifyDataSetChanged()
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