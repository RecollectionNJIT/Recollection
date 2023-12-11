package edu.njit.recollection

import android.app.PendingIntent.getActivity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class HomeCalendarAdapter(
    private val context: Context,
    private val calendarList: MutableList<CalendarEntry>,
    private val activity: FragmentActivity?) : RecyclerView.Adapter<HomeCalendarAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.calendarHomeTitle)
        val timeStartView: TextView = itemView.findViewById(R.id.calendarHomeTimeStart)
        val timeEndView: TextView = itemView.findViewById(R.id.calendarHomeTimeEnd)
        val removeEnd = itemView.findViewById<TextView>(R.id.calendarHomeEnd)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCalendarAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.calendar_simple_day_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = calendarList.get(position)
        //Log.v("On bind view",  calendarList.toString())
        holder.titleTextView.text = day.title
        holder.timeStartView.text = day.timeStart
        holder.timeEndView.text = day.timeEnd

        if(holder.timeEndView.text.toString() == ""){
            holder.removeEnd.visibility = View.INVISIBLE
            Log.v("Is empty", ""+day.timeEnd)
        }
        else{
            holder.removeEnd.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener(){
            replaceFragment(MainCalendarFragment())
            bottomNavigationView.selectedItemId = R.id.nav_calendar
        }
    }

    override fun getItemCount() = calendarList.size


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = activity?.getSupportFragmentManager()
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.main_frame_layout, fragment)
        fragmentTransaction?.commit()
    }
}