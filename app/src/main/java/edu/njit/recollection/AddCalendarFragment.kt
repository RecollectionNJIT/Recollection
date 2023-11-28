package edu.njit.recollection

import android.os.Bundle
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

        titleEditText = view.findViewById(R.id.addCalendarTitle)
        descriptionEditText = view.findViewById(R.id.addCalendarDescription)
        startText = view.findViewById(R.id.addStartTime)
        endText = view.findViewById(R.id.addEndTime)
        saveButton = view.findViewById(R.id.calendarSaveBtn)
        // Inflate the layout for this fragment

        // do stuff here

        saveButton.setOnClickListener{
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val date = "2023-11-27"
            val start = startText.text.toString()
            val end =  endText.text.toString()
            val event = CalendarEntry(date, title, description, start, end)
            val auth = FirebaseAuth.getInstance()
            val newNoteEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").push()
            newNoteEntryRef.setValue(event)
            activity?.finish()
        }

        return view
    }

    companion object {
        fun newInstance() : AddCalendarFragment {
            return AddCalendarFragment()
        }
    }
}