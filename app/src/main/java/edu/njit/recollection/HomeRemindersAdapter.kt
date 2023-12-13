package edu.njit.recollection

import android.app.PendingIntent.getActivity
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeRemindersAdapter(
    private val context: Context,
    private val remindersList: MutableList<Reminders>,
    private val activity: FragmentActivity?) : RecyclerView.Adapter<HomeRemindersAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.reminderHomeTitle)
        val timeDate: TextView = itemView.findViewById(R.id.reminderHomeDate)
        val timeStart: TextView = itemView.findViewById(R.id.reminderHomeTimeStart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRemindersAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_home_reminder_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy h:m a")
        val sortedReminders = remindersList.sortedByDescending {
                LocalDate.parse(it.date + " " + it.time, dateTimeFormatter)
            }.reversed()

        val reminder = sortedReminders.get(position)
        holder.titleTextView.text = reminder.title
        holder.timeDate.text = reminder.date
        holder.timeStart.text = reminder.time

        holder.itemView.setOnClickListener(){
            replaceFragment(MainRemindersFragment())
            bottomNavigationView.selectedItemId = R.id.nav_reminders
        }
    }

    override fun getItemCount() = remindersList.size

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = activity?.getSupportFragmentManager()
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.main_frame_layout, fragment)
        fragmentTransaction?.commit()
    }
}