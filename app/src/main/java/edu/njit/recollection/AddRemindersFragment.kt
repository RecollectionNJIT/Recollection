package edu.njit.recollection

import android.app.DatePickerDialog
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

class AddRemindersFragment : Fragment() {
    lateinit var myCalendar: Calendar
    lateinit var selectReminderDescription: EditText
    lateinit var selectReminderDate: EditText
    lateinit var createReminderBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_reminders, container, false)

        // Retrieve Description and Date from User
        selectReminderDescription = view.findViewById(R.id.selectReminderDescription)
        selectReminderDate = view.findViewById(R.id.selectReminderDate)
        createReminderBtn = view.findViewById(R.id.createReminderBtn)

        myCalendar = Calendar.getInstance()
        val date =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        selectReminderDate.setOnClickListener {
            val dpd = DatePickerDialog(
                view.context,
                android.R.style.Theme_Holo_Light_Dialog,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
            dpd.datePicker.findViewById<View>(resources.getIdentifier("day", "id", "android")).visibility =
                View.GONE
            dpd.show()
        }

        // Create the Reminder entry
        createReminderBtn.setOnClickListener {
            // Handle the creation of the reminder here
            val description = selectReminderDescription.text.toString()
            val date = selectReminderDate.text.toString()
            // Add your logic to create a reminder with the entered description and date
        }

        return view
    }

    private fun updateLabel() {
        val myFormat = "MMMM dd, yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        selectReminderDate.setText(dateFormat.format(myCalendar.time))
    }

    companion object {
        fun newInstance(): AddRemindersFragment {
            return AddRemindersFragment()
        }
    }
}
