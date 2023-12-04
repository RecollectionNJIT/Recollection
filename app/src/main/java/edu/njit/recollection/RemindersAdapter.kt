package edu.njit.recollection

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
        val listItem = items[position]
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
            intent.putExtra("reminder", listItem)
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
        // Delete the reminder from the Firebase Realtime Database
        val auth = FirebaseAuth.getInstance()
        val databaseRef = FirebaseDatabase.getInstance().reference
        val reminderRef = databaseRef.child("users").child(auth.uid!!)
            .child("reminders").child(reminder.id ?: "")

        reminderRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Reminder deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete reminder", Toast.LENGTH_SHORT).show()
            }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}
//
