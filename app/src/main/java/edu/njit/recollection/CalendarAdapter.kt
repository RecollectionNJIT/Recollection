package edu.njit.recollection

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class CalendarAdapter (private val context: Context, private val calendarList: MutableList<CalendarEntry>): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
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
        //Log.v("On bind view",  calendarList.toString())
        holder.titleTextView.text = day.title
        holder.bodyTextView.text = day.description
        holder.timeStartView.text = day.timeStart
        holder.timeEndView.text = day.timeEnd

        holder.itemView.setOnClickListener {
            val i = Intent(context, AddActivity::class.java)
            i.putExtra("fragment", "calendar")
            i.putExtra("date", day.date)
            i.putExtra("title", day.title)
            i.putExtra("description", day.description)
            i.putExtra("timeStart", day.timeStart)
            i.putExtra("timeEnd", day.timeEnd)
            i.putExtra("key", day.key)
            i.putExtra("edit", true)
            context.startActivity(i)
        }

        holder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    val auth = FirebaseAuth.getInstance()
                    Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(day.key!!).removeValue()
                    Log.v("removed item and checking list", ""+calendarList)
                    Toast.makeText(holder.itemView.context, "Entry Deleted!", Toast.LENGTH_SHORT).show()
                    calendarList.removeAt(position)
                    notifyItemRemoved(position)
                    //notifyItemRangeChanged(position, itemCount)
                    Log.v("removed item and checking list before return", ""+calendarList)
                    return true
                }
        })
    }

    override fun getItemCount() = calendarList.size

}