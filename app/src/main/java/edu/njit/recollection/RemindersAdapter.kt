package edu.njit.recollection

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class RemindersAdapter(private val context: Context, private val items: List<Reminders>) : RecyclerView.Adapter<RemindersAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReminderTitle: TextView = itemView.findViewById(R.id.tvReminderTitle)
        val tvReminderDescription: TextView = itemView.findViewById(R.id.tvReminderDescription)
        val tvReminderDate: TextView = itemView.findViewById(R.id.tvReminderDate)
        val tvReminderTime: TextView = itemView.findViewById(R.id.tvReminderTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.rv_reminder_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy h:m a")
        val sortedReminders = items.sortedByDescending {
            LocalDate.parse(it.date + " " + it.time, dateTimeFormatter)
        }.reversed()

        Log.i("test", sortedReminders.toString())
        val listItem = sortedReminders[position]
        holder.tvReminderTitle.text = listItem.title
        holder.tvReminderDescription.text = listItem.description
        holder.tvReminderDate.text = listItem.date
        holder.tvReminderTime.text = listItem.time

        holder.itemView.setOnLongClickListener {
            showDeleteConfirmationDialog(listItem)  // Show a confirmation dialog
            true  // Indicate that the long click event is handled
        }


        holder.itemView.setOnClickListener {
            // Handle item click, e.g., show details
            //val i = Intent(context, DetailsActivity::class.java)
            //i.putExtra("fragment", "reminders")
            //i.putExtra("reminder", listItem)
            //context.startActivity(i)
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("fragment","reminders")
            intent.putExtra("reminder", listItem.key)
            context.startActivity(intent)
        }
    }
    private fun showDeleteConfirmationDialog(reminder: Reminders) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Reminder")
        builder.setMessage("Are you sure you want to delete this reminder?")

        builder.setPositiveButton("Delete") { _, _ ->
            deleteReminder(reminder)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteReminder(reminder: Reminders) {
        // Cancel the alarm associated with the reminder
        cancelAlarm(reminder)

        // Delete the reminder from the Firebase Realtime Database
        val auth = FirebaseAuth.getInstance()

        if (reminder.addToNotes == true)
            Firebase.database.reference.child("users").child(auth.uid!!).child("notes").child(reminder.key!!).child("addToReminders").setValue(false)
        if (reminder.addToCal == true)
            Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(reminder.key!!).child("addToReminders").setValue(false)
        val databaseRef = FirebaseDatabase.getInstance().reference
        val reminderRef = databaseRef.child("users").child(auth.uid!!)
            .child("reminders").child(reminder.key!!)

        reminderRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Reminder deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete reminder", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cancelAlarm(reminder: Reminders) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val requestCode = reminder.key?.hashCode() ?: 0

        Log.d("AlarmSetup", "key of adapter ${requestCode}")
        val pendingIntent =
            PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        // Check if the PendingIntent exists before canceling the alarm
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
//
