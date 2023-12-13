package edu.njit.recollection

import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import java.util.Calendar
import java.util.Locale

class AddCalendarFragment : Fragment() {
    private lateinit var myCalendar: Calendar
    private lateinit var activityTitle : TextView
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var startTime: EditText
    private lateinit var endTime: EditText
    private lateinit var saveButton: Button
    private lateinit var sendToReminder : CheckBox
    private lateinit var sendToNotes : CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_calendar, container, false)

        requireActivity().window.statusBarColor = ContextCompat.getColor(view.context, R.color.calendar)

//        Log.v("Date received",""+activity?.intent?.extras?.getString("date"))
//        Log.v("date", "" + activity?.intent?.extras?.getString("date"))
//        Log.v("title", "" + activity?.intent?.extras?.getString("title"))
//        Log.v("description", "" + activity?.intent?.extras?.getString("description"))
//        Log.v("timeStart", "" + activity?.intent?.extras?.getString("timeStart"))
//        Log.v("timeEnd", "" + activity?.intent?.extras?.getString("timeEnd"))
//        Log.v("edit", "" + activity?.intent?.extras?.getBoolean("edit"))
//        Log.v("key", "" + activity?.intent?.extras?.getString("key"))
        val edit = activity?.intent?.extras?.getBoolean("edit", false)

        activityTitle = view.findViewById(R.id.textViewCalendarAdd)

        titleEditText = view.findViewById(R.id.addCalendarTitle)
        descriptionEditText = view.findViewById(R.id.addCalendarDescription)
        startTime = view.findViewById(R.id.addStartTime)
        endTime = view.findViewById(R.id.addEndTime)
        saveButton = view.findViewById(R.id.calendarSaveBtn)
        sendToReminder = view.findViewById(R.id.addToReminder)
        sendToNotes = view.findViewById(R.id.addToNotes)
        val itemPriceET = view.findViewById<EditText>(R.id.itemPriceET)

        // Inflate the layout for this fragment
        myCalendar = Calendar.getInstance()

        val timeListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                updateTimeLabel(startTime)
            }
        val timeListener2 =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                updateTimeLabel(endTime)
            }
        startTime.setOnClickListener {
            val tpd = TimePickerDialog(
                view.context,
                timeListener,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                false
            )
            tpd.show()
        }

        endTime.setOnClickListener {
            val tpd = TimePickerDialog(
                view.context,
                timeListener2,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                false
            )
            tpd.show()
        }

        // This handles the visibility of the time box as well as making the time N/A if it's a payday
        val sendToFinances = view.findViewById<CheckBox>(R.id.addToFinances)
        sendToFinances.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                itemPriceET.visibility = View.VISIBLE
            }
            else {
                itemPriceET.visibility = View.INVISIBLE
            }
        }



        // This makes it so the input for price is locked to 2 places after the decimal.
        itemPriceET.setFilters(
            arrayOf<InputFilter>(
                DigitsInputFilter(
                    Integer.MAX_VALUE,
                    2,
                    Double.POSITIVE_INFINITY
                )
            )
        )

        // do stuff here
        if(edit == true) {
            val auth = FirebaseAuth.getInstance()

            var title = activity?.intent?.extras?.getString("title")
            var description = activity?.intent?.extras?.getString("description")
            val date = activity?.intent?.extras?.getString("date")
            var start = activity?.intent?.extras?.getString("timeStart")
            var end =activity?.intent?.extras?.getString("timeEnd")
            val key = activity?.intent?.extras?.getString("key")
            val addToRemindersBool = activity?.intent?.extras?.getBoolean("addToReminders")
            val addToNotesBool = activity?.intent?.extras?.getBoolean("addToNotes")
            val addToFinancesBool = activity?.intent?.extras?.getBoolean("addToFinances")

            sendToReminder.isChecked = addToRemindersBool!!
            sendToNotes.isChecked = addToNotesBool!!

            activityTitle.setText("Edit Calendar Event")
            titleEditText.setText( activity?.intent?.extras?.getString("title"))
            descriptionEditText.setText(activity?.intent?.extras?.getString("description"))
            startTime.setText(activity?.intent?.extras?.getString("timeStart"))
            endTime.setText( activity?.intent?.extras?.getString("timeEnd"))
            val original = CalendarEntry(date, title, description, start, end)

            sendToFinances.isChecked = addToFinancesBool!!
            if (sendToFinances.isChecked) {
                Firebase.database.reference.child("users").child(auth.uid!!).child("finances").child(key!!).child("amount").get().addOnSuccessListener {
                    itemPriceET.setText((String.format("%.2f", it.value.toString().toDouble())))
                }
            }

            saveButton.setOnClickListener {
                title = titleEditText.text.toString()
                description = descriptionEditText.text.toString()
                start = startTime.text.toString()
                end = endTime.text.toString()
                var errMsg  = ""
                var error = false
                //send error messages
                if(title.toString().isEmpty()){
                   errMsg +="Error: A title is required!\n"
                    error = true
                }
                if(start.toString().isEmpty()){
                    errMsg += "Error: A start time is required!\n"
                    error = true
                }
                if(start.toString() > end.toString() && end.toString().length > 1){
                    errMsg += "Error: End time must be greater than start time!\n"
                    error = true
                }

                val editedCalEntry = CalendarEntry(date, title, description, start, end,key)
                val editCalEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(key!!)
                editedCalEntry.addToFinances = sendToFinances.isChecked
                editedCalEntry.addToReminders = sendToReminder.isChecked
                editedCalEntry.addToNotes = sendToNotes.isChecked
                editCalEntryRef.setValue(editedCalEntry)
                if(!error) {
                    if (sendToFinances.isChecked) {
                        val newFinanceEntry = FinanceEntry(
                            financeDate(date!!),
                            financeMonthDate(date),
                            "Income",
                            "Work/Paycheck",
                            itemPriceET.text.toString().toDouble(),
                            key!!,
                            true
                        )
                        val newFinanceEntryRef =
                            Firebase.database.reference.child("users").child(auth.uid!!)
                                .child("finances").child(key!!)
                        newFinanceEntryRef.setValue(newFinanceEntry)
                    }

                    if (sendToReminder.isChecked) {
                        val reminderDate = reminderDate(date!!)
                        //Log.v("Reminder Date", ""+reminderDate)
                        val reminderEnt = Reminders(title, description, reminderDate, start, "")
                        val newReminderEntryRef =
                            Firebase.database.reference.child("users").child(auth.uid!!)
                                .child("reminders").child(key!!)
                        newReminderEntryRef.setValue(reminderEnt)
                    }

                    if (sendToNotes.isChecked) {
                        val noteEnt = Note(title, description, "")
                        val newNoteEntryRef =
                            Firebase.database.reference.child("users").child(auth.uid!!)
                                .child("notes").child(key!!)
                        newNoteEntryRef.setValue(noteEnt)
                    }
                }
                if(!error){
                    activity?.finish()
                }
                else{
                    Toast.makeText(this.context,errMsg, Toast.LENGTH_SHORT).show()
                }

            }
        }
        else {
            saveButton.setOnClickListener {
                val title = titleEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val date = activity?.intent?.extras?.getString("date")
                val start = startTime.text.toString()
                val end = endTime.text.toString()
                val reminder = sendToReminder.isChecked
                val event = CalendarEntry(date, title, description, start, end)
                val auth = FirebaseAuth.getInstance()


                var errMsg  = ""
                var error = false
                //send error messages
                if(title.toString().isEmpty()){
                    errMsg +="Error: A title is required!\n"
                    error = true
                }
                if(start.toString().isEmpty()){
                    errMsg += "Error: A start time is required!\n"
                    error = true
                }
                if(start.toString() > end.toString() && end.toString().length > 1){
                    errMsg += "Error: End time must be greater than start time!\n"
                    error = true
                }
                if(!error) {
                    val newCalEntryRef =
                        Firebase.database.reference.child("users").child(auth.uid!!)
                            .child("calendar")
                            .push()
                    event.key = newCalEntryRef.key
                    event.addToReminders = sendToReminder.isChecked
                    event.addToFinances = sendToFinances.isChecked
                    event.addToNotes = sendToNotes.isChecked
                    newCalEntryRef.setValue(event)

                    if (reminder) {
                        val reminderDate = reminderDate(date!!)
                        //Log.v("Reminder Date", ""+reminderDate)
                        val reminderEnt = Reminders(
                            title,
                            description,
                            reminderDate,
                            start,
                            "",
                            addToCal = true,
                            addToNotes = sendToNotes.isChecked
                        )
                        val newReminderEntryRef =
                            Firebase.database.reference.child("users").child(auth.uid!!)
                                .child("reminders").child(event.key!!)
                        newReminderEntryRef.setValue(reminderEnt)
                    }

                    if (sendToFinances.isChecked) {
                        val newFinanceEntry = FinanceEntry(
                            financeDate(event.date!!),
                            financeMonthDate(event.date!!),
                            "Income",
                            "Work/Paycheck",
                            itemPriceET.text.toString().toDouble(),
                            event.key!!,
                            true
                        )
                        val newFinanceEntryRef =
                            Firebase.database.reference.child("users").child(auth.uid!!)
                                .child("finances").child(event.key!!)
                        newFinanceEntryRef.setValue(newFinanceEntry)
                    }

                    if (sendToNotes.isChecked) {
                        val noteEnt = Note(title, description, "")
                        val newNoteEntryRef =
                            Firebase.database.reference.child("users").child(auth.uid!!)
                                .child("notes").child(event.key!!)
                        newNoteEntryRef.setValue(noteEnt)
                    }
                }

                if(!error){
                    activity?.finish()
                }
                else{
                    Toast.makeText(this.context,errMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }
    private fun financeDate(oldDate: String) : String {
        val newFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val oldFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return newFormat.format(oldFormat.parse(oldDate))
    }
    private fun financeMonthDate(oldDate: String) : String {
        val newFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        val oldFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return newFormat.format(oldFormat.parse(oldDate))
    }

    companion object {
        fun newInstance() : AddCalendarFragment {
            return AddCalendarFragment()
        }
    }

    private fun reminderDate(oldDate: String) : String {
        val newFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val oldFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return newFormat.format(oldFormat.parse(oldDate))
    }

    private fun updateTimeLabel(text : EditText) {
        val myFormat = "hh:mm a"
        val timeFormat = SimpleDateFormat(myFormat, Locale.US)
        text.setText(timeFormat.format(myCalendar.time))
    }
}