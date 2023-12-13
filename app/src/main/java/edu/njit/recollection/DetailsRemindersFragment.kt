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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.util.Calendar
import java.util.Locale

class DetailsRemindersFragment : Fragment() {

    private lateinit var etEditTitle: EditText
    private lateinit var etEditDescription: EditText
    private lateinit var etEditDate: EditText
    private lateinit var etEditTime: EditText
    private lateinit var btnUpdateReminder: Button
    private lateinit var btnDeleteReminder: Button
    private var reminder: Reminders? = null
    private lateinit var myCalendar: Calendar
    lateinit var sendToCal : CheckBox
    lateinit var sendToNotes : CheckBox

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
        btnDeleteReminder = view.findViewById(R.id.btnDeleteReminder)

        sendToCal = view.findViewById(R.id.checkboxAddToCalendar)
        sendToNotes = view.findViewById(R.id.checkboxAddToNotes)

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

        // Set up click listener for the Delete button
        btnDeleteReminder.setOnClickListener {
            deleteReminder()
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
        //dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
        //dpd.show()
        dpd.datePicker.maxDate = Long.MAX_VALUE;
        dpd.show();
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

        val calendar = sendToCal.isChecked
        val notes = sendToNotes.isChecked

        // Update the reminder if it is not null
        reminder?.let { reminder ->
            // Create a map with updated values
            val updatedValues = mapOf(
                "title" to updatedTitle,
                "description" to updatedDescription,
                "date" to updatedDate,
                "time" to updatedTime
            )

            // Update the specific reminder in the Firebase Realtime Database
            val auth = FirebaseAuth.getInstance()
            val databaseRef = FirebaseDatabase.getInstance().reference
            val reminderRef = databaseRef.child("users").child(auth.uid!!)
                .child("reminders").child(reminder.key ?: "")

            val newKey = reminder.key
            val calDate = calendarDate(updatedDate)

            if(calendar){
                val calendarEnt = CalendarEntry(calDate, updatedTitle, updatedDescription, updatedTime,"N/A",newKey)
                val newCalendarEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(newKey!!)
                newCalendarEntryRef.setValue(calendarEnt)
            }

            if(notes){
                val notesEnt = Note(updatedTitle, updatedDescription,"N/A", newKey)
                val newNotesEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("notes").child(newKey!!)
                newNotesEntryRef.setValue(notesEnt)
            }

            reminderRef.updateChildren(updatedValues)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Reminder updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update reminder", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun deleteReminder() {
        // Delete the reminder if it is not null
        reminder?.let { reminder ->
            // Delete the specific reminder from the Firebase Realtime Database
            val auth = FirebaseAuth.getInstance()
            val databaseRef = FirebaseDatabase.getInstance().reference
            val reminderRef = databaseRef.child("users").child(auth.uid!!)
                .child("reminders").child(reminder.key ?: "")

            reminderRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Reminder deleted successfully", Toast.LENGTH_SHORT).show()
                    // You can finish the activity or navigate back after deleting
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to delete reminder", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun calendarDate(oldDate: String) : String {
        val oldFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return newFormat.format(oldFormat.parse(oldDate))
    }

    companion object {
        fun newInstance(): DetailsRemindersFragment {
            return DetailsRemindersFragment()
        }
    }
}
//