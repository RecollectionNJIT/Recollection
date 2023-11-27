package edu.njit.recollection

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

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

    override fun getItemCount(): Int {
        return items.size
    }
}
