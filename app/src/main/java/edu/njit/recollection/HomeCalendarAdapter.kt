package edu.njit.recollection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeCalendarAdapter(
    private val context: Context,
    private val calendarList: MutableList<CalendarEntry>
) : RecyclerView.Adapter<HomeCalendarAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.calendarHomeTitle)
        val timeStartView: TextView = itemView.findViewById(R.id.calendarHomeTimeStart)
        val timeEndView: TextView = itemView.findViewById(R.id.calendarHomeTimeEnd)
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

    }

    override fun getItemCount() = calendarList.size

}