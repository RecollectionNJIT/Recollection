package edu.njit.recollection

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.common.primitives.Longs.max
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import java.util.Calendar
import java.util.Locale
import java.util.Date

class AddRemindersFragment : Fragment() {
    lateinit var myCalendar: Calendar
    lateinit var pageTitle: TextView
    lateinit var selectReminderTitle: EditText
    lateinit var selectReminderDescription: EditText
    lateinit var selectReminderDate: EditText
    lateinit var selectReminderTime: EditText
    lateinit var createReminderBtn: Button
    lateinit var sendToCal : CheckBox
    lateinit var sendToNotes : CheckBox
    lateinit var editReminder: Reminders

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_reminders, container, false)
        requireActivity().window.statusBarColor = ContextCompat.getColor(view.context, R.color.reminders)

        // Retrieve Title, Description, Date, and Time from User
        selectReminderTitle = view.findViewById(R.id.selectReminderTitle)
        selectReminderDescription = view.findViewById(R.id.selectReminderDescription)
        selectReminderDate = view.findViewById(R.id.selectReminderDate)
        selectReminderTime = view.findViewById(R.id.selectReminderTime)
        createReminderBtn = view.findViewById(R.id.createReminderBtn)
        pageTitle = view.findViewById(R.id.textViewRemindersAdd)

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

        val editBool = activity?.intent?.extras?.getBoolean("edit",false)

        if (editBool == true) {
            editReminder = activity?.intent?.extras?.getSerializable("editEntry") as Reminders
            pageTitle.text = "Editing Reminder"
            createReminderBtn.text = "Save Edits"
            selectReminderTitle.setText(editReminder.title)
            selectReminderDescription.setText(editReminder.description)
            selectReminderDate.setText(editReminder.date)
            selectReminderTime.setText(editReminder.time)
            sendToCal.isChecked = editReminder.addToCal!!
            sendToNotes.isChecked = editReminder.addToNotes!!
        }

        // Create the Reminder entry
        createReminderBtn.setOnClickListener {
            // Handle the creation of the reminder here
            val title = selectReminderTitle.text.toString()
            val description = selectReminderDescription.text.toString()
            val date = selectReminderDate.text.toString()
            val time = selectReminderTime.text.toString()
            val calDate = calendarDate(date)

            if (editBool == true) {
                val postValues = mapOf(
                    "title" to title,
                    "description" to description,
                    "date" to date,
                    "time" to time,
                    "key" to editReminder.key
                )
                val auth = FirebaseAuth.getInstance()
                val newReminderEntryRef =
                    Firebase.database.reference.child("users").child(auth.uid!!).child("reminders")
                        .child(editReminder.key!!)
                val calendar = sendToCal.isChecked
                val notes = sendToNotes.isChecked
                if (calendar) {
                    val calendarEnt =
                        CalendarEntry(calDate, title, description, time, "N/A", editReminder.key!!, true, notes)
                    val newCalendarEntryRef =
                        Firebase.database.reference.child("users").child(auth.uid!!).child("calendar")
                            .child(editReminder.key!!)
                    newCalendarEntryRef.setValue(calendarEnt)
                }

                if (notes) {
                    val notesEnt = Note(title, description, "N/A", editReminder.key!!, true, calendar)
                    val newNotesEntryRef =
                        Firebase.database.reference.child("users").child(auth.uid!!).child("notes")
                            .child(editReminder.key!!)
                    newNotesEntryRef.setValue(notesEnt)
                }
                newReminderEntryRef.updateChildren(postValues)
                newReminderEntryRef.child("addToCal").setValue(calendar)
                newReminderEntryRef.child("addToNotes").setValue(notes)
                activity?.finish()
            }

            else{
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

                // Inside createReminderBtn.setOnClickListener
                val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                // Get the current time in milliseconds
                val currentTime = System.currentTimeMillis()

                // Get the time difference between the current time and the reminder time
                val timeDifference = myCalendar.timeInMillis - currentTime

                // Schedule an alarm 10 minutes before the reminder time
                val tenMinutesInMillis = 10 * 60 * 1000
                val alarmTime = timeDifference - tenMinutesInMillis

                // Ensure alarmTime is at least 0 to avoid negative values
                val adjustedAlarmTime = max(alarmTime, 0)

                val intent = Intent(view.context, ReminderBroadcastReceiver::class.java)
                intent.putExtra("title", title)  // pass necessary data to the receiver

                // Use a unique request code for each notification (e.g., reminder ID)
                val requestCode = newRem.key.hashCode()  // assuming newRem.key is unique

                // Add FLAG_IMMUTABLE to the PendingIntent creation
                val pendingIntent = PendingIntent.getBroadcast(
                    view.context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE
                )

                Log.d("AlarmSetup", "key of add ${requestCode}")
                Log.d("AlarmSetup", "Setting up alarm for $title at ${myCalendar.time}")
                Log.d("AlarmSetup", "Setting up alarm for $title at ${currentTime + adjustedAlarmTime}")

                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    currentTime + adjustedAlarmTime,
                    pendingIntent
                )

                if (calendar) {
                    val calendarEnt =
                        CalendarEntry(calDate, title, description, time, "N/A", newRem.key, true , notes)
                    val newCalendarEntryRef =
                        Firebase.database.reference.child("users").child(auth.uid!!).child("calendar")
                            .child(newRem.key!!)
                    newCalendarEntryRef.setValue(calendarEnt)
                }

                if (notes) {
                    val notesEnt = Note(title, description, "N/A", newRem.key, true ,calendar)
                    val newNotesEntryRef =
                        Firebase.database.reference.child("users").child(auth.uid!!).child("notes")
                            .child(newRem.key!!)
                    newNotesEntryRef.setValue(notesEnt)
                }
                activity?.finish()
            }
            activity?.finish()
        }

        view.findViewById<ImageButton>(R.id.backFromAddRemBtn).setOnClickListener {
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
