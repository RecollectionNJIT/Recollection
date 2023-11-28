package edu.njit.recollection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class AddCalendarFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var startText: EditText
    private lateinit var endText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_calendar, container, false)
        Log.v("Date received",""+activity?.intent?.extras?.getString("date"))
        Log.v("date", "" + activity?.intent?.extras?.getString("date"))
        Log.v("title", "" + activity?.intent?.extras?.getString("title"))
        Log.v("description", "" + activity?.intent?.extras?.getString("description"))
        Log.v("timeStart", "" + activity?.intent?.extras?.getString("timeStart"))
        Log.v("timeEnd", "" + activity?.intent?.extras?.getString("timeEnd"))
        Log.v("edit", "" + activity?.intent?.extras?.getBoolean("edit"))
        Log.v("key", "" + activity?.intent?.extras?.getString("key"))
        val edit = activity?.intent?.extras?.getBoolean("edit", false)

        titleEditText = view.findViewById(R.id.addCalendarTitle)
        descriptionEditText = view.findViewById(R.id.addCalendarDescription)
        startText = view.findViewById(R.id.addStartTime)
        endText = view.findViewById(R.id.addEndTime)
        saveButton = view.findViewById(R.id.calendarSaveBtn)
        // Inflate the layout for this fragment

        // do stuff here
        if(edit == true) {
            var title = activity?.intent?.extras?.getString("title")
            var description = activity?.intent?.extras?.getString("description")
            val date = activity?.intent?.extras?.getString("date")
            var start = activity?.intent?.extras?.getString("timeStart")
            var end =activity?.intent?.extras?.getString("timeEnd")
            val key = activity?.intent?.extras?.getString("key")

            titleEditText.setText( activity?.intent?.extras?.getString("title"))
            descriptionEditText.setText(activity?.intent?.extras?.getString("description"))
            startText.setText(activity?.intent?.extras?.getString("timeStart"))
            endText.setText( activity?.intent?.extras?.getString("timeEnd"))
            val original = CalendarEntry(date, title, description, start, end)

            saveButton.setOnClickListener {
                title = titleEditText.text.toString()
                description = descriptionEditText.text.toString()
                start = startText.text.toString()
                end = endText.text.toString()

                val auth = FirebaseAuth.getInstance()
                val editedCalEntry = CalendarEntry(date, title, description, start, end,key)
                val editCalEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(key!!)
                editCalEntryRef.setValue(editedCalEntry)
                activity?.finish()
            }
        }
        else {
            saveButton.setOnClickListener {
                val title = titleEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val date = activity?.intent?.extras?.getString("date")
                val start = startText.text.toString()
                val end = endText.text.toString()
                val event = CalendarEntry(date, title, description, start, end)
                val auth = FirebaseAuth.getInstance()
                val newCalEntryRef =
                    Firebase.database.reference.child("users").child(auth.uid!!).child("calendar")
                        .push()
                newCalEntryRef.setValue(event)
                activity?.finish()
            }
        }
        return view
    }

    companion object {
        fun newInstance() : AddCalendarFragment {
            return AddCalendarFragment()
        }
    }
}