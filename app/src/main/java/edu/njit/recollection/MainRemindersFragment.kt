package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainRemindersFragment : Fragment() {
    private lateinit var rvReminders: RecyclerView
    private lateinit var adapter: RemindersAdapter
    private val items = mutableListOf<Reminder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_reminders, container, false)

        // Initialize the RecyclerView
        rvReminders = view.findViewById(R.id.rvReminders)
        rvReminders.layoutManager = LinearLayoutManager(view.context)
        adapter = RemindersAdapter(view.context, items)
        rvReminders.adapter = adapter

        view.findViewById<Button>(R.id.btnDetailsReminders).setOnClickListener {
            val i = Intent(view.context, DetailsActivity::class.java)
            i.putExtra("fragment", "reminders")
            startActivity(i)
        }

        view.findViewById<Button>(R.id.btnAddReminders).setOnClickListener {
            val i = Intent(view.context, AddActivity::class.java)
            i.putExtra("fragment", "reminders")
            startActivity(i)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Call the new method within onViewCreated
        updateRV()
    }

    companion object {
        fun newInstance() : MainRemindersFragment {
            return MainRemindersFragment()
        }
    }

    fun updateRV() {
        items.clear()
        items.clear()
        // Add pairs of reminders to the items list
        items.add(Reminder("Title1", "Description1", "Date1", "0"))
        items.add(Reminder("Title2", "Description2", "Date2", "1"))
        items.add(Reminder("Title3", "Description3", "Date3", "2"))
        items.add(Reminder("Title4", "Description4", "Date4", "3"))
        items.add(Reminder("Title5", "Description5", "Date5", "4"))
        items.add(Reminder("Title6", "Description6", "Date6", "5"))
        items.add(Reminder("Title7", "Description7", "Date7", "6"))
        items.add(Reminder("Title8", "Description8", "Date8", "7"))
        adapter.notifyDataSetChanged()
    }
}
