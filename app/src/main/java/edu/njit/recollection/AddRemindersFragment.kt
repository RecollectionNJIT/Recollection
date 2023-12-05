package edu.njit.recollection

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
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
    lateinit var sendToCal : CheckBox
    lateinit var sendToNotes : CheckBox


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

        sendToCal = view.findViewById(R.id.checkboxAddToCalendar)
        sendToNotes = view.findViewById(R.id.checkboxAddToNotes)

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
            //dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
            //dpd.show()
            dpd.datePicker.maxDate = Long.MAX_VALUE;
            dpd.show();
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

            val calDate = calendarDate(date)

            val calendar = sendToCal.isChecked
            val notes = sendToNotes.isChecked
            // Add your logic to create a reminder with the entered details
            val newRem = Reminders(title, description, date, time) // Pass null for ID

            val auth = FirebaseAuth.getInstance()
            val newReminderEntryRef = Firebase.database.reference
                .child("users")
                .child(auth.uid!!)
                .child("reminders")
                .push() // Generate a unique key for the new reminder

            newRem.key = newReminderEntryRef.key
            newRem.addToCal = sendToCal.isChecked
            newRem.addToNotes = sendToNotes.isChecked
            newReminderEntryRef.setValue(newRem)

            if(calendar){
                val calendarEnt = CalendarEntry(calDate, title, description, time,"N/A",newRem.key)
                val newCalendarEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(newRem.key!!)
                newCalendarEntryRef.setValue(calendarEnt)
            }

            if(notes){
                val notesEnt = Note(title, description,"N/A", newRem.key)
                val newNotesEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("notes").child(newRem.key!!)
                newNotesEntryRef.setValue(notesEnt)
            }
            activity?.finish()
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

    private fun calendarDate(oldDate: String) : String {
        val oldFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return newFormat.format(oldFormat.parse(oldDate))
    }

    companion object {
        fun newInstance(): AddRemindersFragment {
            return AddRemindersFragment()
        }
    }
}
