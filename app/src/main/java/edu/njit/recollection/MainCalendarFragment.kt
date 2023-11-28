package edu.njit.recollection

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.util.Objects


private lateinit var calendarRecycler: RecyclerView
private lateinit var adapter: CalendarAdapter
private var days = mutableListOf<CalendarEntry>()

private var initialCon = true

private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
private val today = LocalDate.now()
private var selectedDate = LocalDate.now()
private lateinit var prevContainer :  DayViewContainer
class MonthViewContainer(view: View) : ViewContainer(view) {
    // Alternatively, you can add an ID to the container layout and use findViewById()
    val titlesContainer = view as ViewGroup
}
class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)
}

class MainCalendarFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        days.clear()
    }

    override fun onStart() {
        super.onStart()
        days.clear()
        if(!initialCon) {
            updateRV(selectedDate)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_calendar, container, false)
        // do stuff here
        calendarRecycler = view.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        days.clear()
        adapter = CalendarAdapter(view.context, days)
        calendarRecycler.adapter = adapter

        val monthView = view.findViewById<TextView>(R.id.calendarMonth)
        val calendarText = view.findViewById<TextView>(R.id.calendarDay)
        calendarText.text = selectionFormatter.format(today)
        // Setting up calendar
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)
            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.view.visibility=View.VISIBLE
                    prevContainer = container
                    when(data.date){
                        today -> {
                            container.textView.setBackgroundResource(R.drawable.today_bg)
                        }
                        else ->{
                            container.textView.background = null
                        }
                    }
                    container.textView.setTextColor(Color.WHITE)
                    container.textView.setOnClickListener(){
                        if( prevContainer.textView.background != null  && data.date.month == today.month ){
                            if( container.textView.text.toString() == today.dayOfMonth.toString()){
                                Log.v("Month",""+ DayPosition.MonthDate)
                                prevContainer.textView.background=null
                                container.textView.setBackgroundResource(R.drawable.today_bg)
                            }
                            else{
                                Log.v("Not Equal Today","" +data.date.month )
                                Log.v("Today","" + today.month)
                                if(prevContainer.textView.text.toString() != today.dayOfMonth.toString()  ){
                                    prevContainer.textView.background = null
                                }
                                container.textView.setBackgroundResource(R.drawable.selected_bg)
                            }
                        }
                        else if (prevContainer.textView.background != null){
                                prevContainer.textView.background = null
                                container.textView.setBackgroundResource(R.drawable.selected_bg)
                        }
                        else{
                            if(data.date.month == today.month && container.textView.text.toString() == today.dayOfMonth.toString() ) {
                                container.textView.setBackgroundResource(R.drawable.today_bg)
                            }
                            else {
                                container.textView.setBackgroundResource(R.drawable.selected_bg)
                            }

                        }

                        prevContainer = container
                        selectDate(data.date, calendarText)
                        updateRV(selectedDate)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    container.view.visibility=View.INVISIBLE
                }
            }
        }
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(50)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(50)  // Adjust as needed
        val titlesContainer = view.findViewById<ViewGroup>(R.id.titlesContainer)
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
        //val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
        // Will produce => Thu | Fri | Sat | Sun | Mon | Tue | Wed
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                // Remember that the header is reused so this will be called for each month.
                // However, the first day of the week will not change so no need to bind
                // the same view every time it is reused.
                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    container.titlesContainer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = data.weekDays.first()[index].date.dayOfWeek
                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = title
                            //textView related to week
                        }
                }
            }
        }
        //when calendar is scrolled
        calendarView.monthScrollListener ={
            monthView.text = titleFormatter.format(it.yearMonth)
            if(it.yearMonth == today.yearMonth){
                selectDate(today, calendarText)
                //this one
                if(initialCon) {
                    initialCon = false
                }
                    updateRV(selectedDate)
            }else{
                selectDate(it.yearMonth.atDay(1), calendarText)
                updateRV(selectedDate)

            }
        }
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        view.findViewById<ImageButton>(R.id.btnAddCalendar).setOnClickListener {
            val i = Intent(view.context, AddActivity::class.java)
            i.putExtra("fragment", "calendar")
            i.putExtra("date",""+ selectedDate.toString() )
            startActivity(i)
            //updateRV(selectedDate)
            adapter.notifyDataSetChanged()
        }
        return view
    }

    companion object {
        fun newInstance() : MainCalendarFragment {
            return MainCalendarFragment()
        }
    }

    private fun selectDate(date: LocalDate, CalendarDate : TextView){
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date

            CalendarDate.text = selectionFormatter.format(date)

            Log.v("Selected", ""+date.toString())
            //oldDate?.let { binding.exThreeCalendar.notifyDateChanged(it) }
            //binding.exThreeCalendar.notifyDateChanged(date)
            //updateAdapterForDate(date)
        }
    }


    fun updateRV(selected:LocalDate = LocalDate.now()) {
        days.clear()
        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference
        databaseRef.child("users").child(auth.uid!!).child("calendar").orderByChild("date").equalTo(selected.toString()).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val date = child.child("date").getValue().toString()
                    val title = child.child("title").getValue().toString()
                    val description = child.child("description").getValue().toString()
                    val start = child.child("timeStart").getValue().toString()
                    val end = child.child("timeEnd").getValue().toString()
                    val key = child.key
                    val event = CalendarEntry(date, title, description, start, end, key)
                    days.add(event)
                    adapter.notifyDataSetChanged()
                }
                Log.v("days", ""+days.toString())

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebaseCalendarMain", "error", Throwable(error.toString()))
            }
        })
    }
}