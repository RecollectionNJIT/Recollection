package edu.njit.recollection

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.Calendar
import java.util.Locale

class DetailsRemindersFragment : Fragment() {

    private lateinit var etEditTitle: EditText
    private lateinit var etEditDescription: EditText
    private lateinit var etEditDate: EditText
    private lateinit var etEditTime: EditText
    private lateinit var btnUpdateReminder: Button
    private var reminder: Reminders? = null
    private lateinit var myCalendar: Calendar

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
        etEditTime = view.findViewById(R.id.etEditTime)
        btnUpdateReminder = view.findViewById(R.id.btnUpdateReminder)

        // Set up the Calendar instance
        myCalendar = Calendar.getInstance()

        // Update UI with reminder details
        if (reminder != null) {
            etEditTitle.setText(reminder!!.title)
            etEditDescription.setText(reminder!!.description)
            etEditDate.setText(reminder!!.date)
            etEditTime.setText(reminder!!.time)
        }

        // Set up click listener for the Date EditText
        etEditDate.setOnClickListener {
            showDatePicker()
        }

        // Set up click listener for the Time EditText
        etEditTime.setOnClickListener {
            showTimePicker()
        }

        // Set up click listener for the Update button
        btnUpdateReminder.setOnClickListener {
            updateReminder()
        }

        return view
    }

    private fun showDatePicker() {
        val dateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateDateLabel()
            }

        val dpd = DatePickerDialog(
            requireContext(),
            dateListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun showTimePicker() {
        val timeListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                updateTimeLabel()
            }

        val tpd = TimePickerDialog(
            requireContext(),
            timeListener,
            myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE),
            false
        )
        tpd.show()
    }

    private fun updateDateLabel() {
        val myFormat = "MMMM dd, yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        etEditDate.setText(dateFormat.format(myCalendar.time))
    }

    private fun updateTimeLabel() {
        val myFormat = "hh:mm a"
        val timeFormat = SimpleDateFormat(myFormat, Locale.US)
        etEditTime.setText(timeFormat.format(myCalendar.time))
    }

    private fun updateReminder() {
        // Get updated values from EditText fields
        val updatedTitle = etEditTitle.text.toString()
        val updatedDescription = etEditDescription.text.toString()
        val updatedDate = etEditDate.text.toString()
        val updatedTime = etEditTime.text.toString()

        // Create a new instance of Reminders with updated values
        reminder = reminder?.copy(
            title = updatedTitle,
            description = updatedDescription,
            date = updatedDate,
            time = updatedTime
        )

        // Update UI with the edited values
        etEditTitle.setText(updatedTitle)
        etEditDescription.setText(updatedDescription)
        etEditDate.setText(updatedDate)
        etEditTime.setText(updatedTime)
    }

    companion object {
        fun newInstance(): DetailsRemindersFragment {
            return DetailsRemindersFragment()
        }
    }
}
