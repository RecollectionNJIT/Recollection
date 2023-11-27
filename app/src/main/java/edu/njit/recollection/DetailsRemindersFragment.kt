package edu.njit.recollection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DetailsRemindersFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details_reminders, container, false)

        // Retrieve reminder data from intent
        val reminder = activity?.intent?.getSerializableExtra("reminder") as Reminders?

        // Update UI with reminder details
        val tvTitle = view.findViewById<TextView>(R.id.tvReminderTitleDetails)
        val tvDescription = view.findViewById<TextView>(R.id.tvReminderDescriptionDetails)
        val tvDate = view.findViewById<TextView>(R.id.tvReminderDateDetails)

        if (reminder != null) {
            tvTitle.text = reminder.title
            tvDescription.text = reminder.description
            tvDate.text = reminder.date
        }

        return view
    }

    companion object {
        fun newInstance(): DetailsRemindersFragment {
            return DetailsRemindersFragment()
        }
    }
}
