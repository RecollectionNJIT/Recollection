package edu.njit.recollection

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CalendarAdapter (private val context: Context, private val calendarList: List<CalendarEntry>): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.calendarTitle)
        val bodyTextView = itemView.findViewById<TextView>(R.id.calendarDescription)
        val timeStartView = itemView.findViewById<TextView>(R.id.calendarTimeStart)
        val timeEndView = itemView.findViewById<TextView>(R.id.calendarTimeEnd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.calendar_day_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = calendarList.get(position)
        holder.titleTextView.text = day.title
        holder.bodyTextView.text = day.description
        holder.timeStartView.text = day.timeStart
        holder.timeEndView.text = day.timeEnd

        holder.itemView.setOnClickListener {
            val i = Intent(context, DetailsActivity::class.java)
            i.putExtra("fragment", "calendar")
            i.putExtra("calendar", day)
            context.startActivity(i)
        }
    }

    override fun getItemCount() = calendarList.size

}