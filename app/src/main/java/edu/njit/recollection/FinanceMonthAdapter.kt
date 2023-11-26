package edu.njit.recollection

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class FinanceMonthAdapter (private val context: Context, private val items: List<FinanceMonth>): RecyclerView.Adapter<FinanceMonthAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFinanceRVDate: TextView = itemView.findViewById(R.id.tvFinanceRVDate)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.rv_finance_item, parent, false)
        return ViewHolder(contactView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = items.get(position)
        holder.tvFinanceRVDate.text = listItem.monthDate
        Log.i("holderListItem", listItem.toString())

        holder.itemView.setOnClickListener {
            val i = Intent(context, DetailsActivity::class.java)
            i.putExtra("fragment", "finances")
            i.putExtra("type", listItem.monthDate)
            context.startActivity(i)
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}