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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import java.util.Calendar
import java.util.Locale

class AddRemindersFragment : Fragment() {
    lateinit var myCalendar: Calendar
    lateinit var selectReminderTitle: EditText
    lateinit var selectReminderDescription: EditText
    lateinit var selectReminderDate: EditText
    lateinit var selectReminderTime: EditText
    lateinit var createReminderBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_reminders, container, false)

        // Retrieve Title, Description, Date, and Time from User
        selectReminderTitle = view.findViewById(R.id.selectReminderTitle)
        selectReminderDescription = view.findViewById(R.id.selectReminderDescription)
        selectReminderDate = view.findViewById(R.id.selectReminderDate)
        selectReminderTime = view.findViewById(R.id.selectReminderTime)
        createReminderBtn = view.findViewById(R.id.createReminderBtn)

        myCalendar = Calendar.getInstance()

        // Date Picker
        val dateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }

        selectReminderDate.setOnClickListener {
            val dpd = DatePickerDialog(
                view.context,
                dateListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        // Time Picker
        val timeListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                updateTimeLabel()
            }

        selectReminderTime.setOnClickListener {
            val tpd = TimePickerDialog(
                view.context,
                timeListener,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                false
            )
            tpd.show()
        }

        // Create the Reminder entry
        createReminderBtn.setOnClickListener {
            // Handle the creation of the reminder here
            val title = selectReminderTitle.text.toString()
            val description = selectReminderDescription.text.toString()
            val date = selectReminderDate.text.toString()
            val time = selectReminderTime.text.toString()

            // Add your logic to create a reminder with the entered details
            val newRem = Reminders(title, description, date, time, null) // Pass null for ID

            val auth = FirebaseAuth.getInstance()
            val newReminderEntryRef = Firebase.database.reference
                .child("users")
                .child(auth.uid!!)
                .child("reminders")
                .push() // Generate a unique key for the new reminder

            newReminderEntryRef.setValue(newRem.copy(id = newReminderEntryRef.key))
                .addOnSuccessListener {
                    activity?.finish()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to create reminder", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    private fun updateLabel() {
        val myFormat = "MMMM dd, yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        selectReminderDate.setText(dateFormat.format(myCalendar.time))
    }

    private fun updateTimeLabel() {
        val myFormat = "hh:mm a"
        val timeFormat = SimpleDateFormat(myFormat, Locale.US)
        selectReminderTime.setText(timeFormat.format(myCalendar.time))
    }

    companion object {
        fun newInstance(): AddRemindersFragment {
            return AddRemindersFragment()
        }
    }
}
