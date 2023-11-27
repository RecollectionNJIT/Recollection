package edu.njit.recollection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class DetailsRemindersFragment : Fragment() {

    private lateinit var etEditTitle: EditText
    private lateinit var etEditDescription: EditText
    private lateinit var etEditDate: EditText
    private lateinit var btnUpdateReminder: Button
    private var reminder: Reminders? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details_reminders, container, false)

        // Retrieve reminder data from intent
        reminder = activity?.intent?.getSerializableExtra("reminder") as Reminders?

        // Initialize UI elements
        etEditTitle = view.findViewById(R.id.etEditTitle)
        etEditDescription = view.findViewById(R.id.etEditDescription)
        etEditDate = view.findViewById(R.id.etEditDate)
        btnUpdateReminder = view.findViewById(R.id.btnUpdateReminder)

        // Update UI with reminder details
        if (reminder != null) {
            etEditTitle.setText(reminder!!.title)
            etEditDescription.setText(reminder!!.description)
            etEditDate.setText(reminder!!.date)
        }

        // Set up click listener for the Update button
        btnUpdateReminder.setOnClickListener {
            updateReminder()
        }

        return view
    }

    private fun updateReminder() {
        // Get updated values from EditText fields
        val updatedTitle = etEditTitle.text.toString()
        val updatedDescription = etEditDescription.text.toString()
        val updatedDate = etEditDate.text.toString()

        // Create a new instance of Reminders with updated values
        reminder = reminder?.copy(
            title = updatedTitle,
            description = updatedDescription,
            date = updatedDate
        )

        // Update UI with the edited values
        etEditTitle.setText(updatedTitle)
        etEditDescription.setText(updatedDescription)
        etEditDate.setText(updatedDate)
    }

    companion object {
        fun newInstance(): DetailsRemindersFragment {
            return DetailsRemindersFragment()
        }
    }
}
