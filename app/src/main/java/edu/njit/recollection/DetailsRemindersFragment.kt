package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class DetailsRemindersFragment : Fragment() {

    private lateinit var editTitle: TextView
    private lateinit var editDescription: TextView
    private lateinit var editDate: TextView
    private lateinit var editTime: TextView
    private lateinit var detailReminder: Reminders


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details_reminders, container, false)
        requireActivity().window.statusBarColor = ContextCompat.getColor(view.context, R.color.reminders)

        // Initialize UI elements
        editTitle = view.findViewById<TextView>(R.id.editTitle)
        editDescription = view.findViewById<TextView>(R.id.editDescription)
        editDate = view.findViewById<TextView>(R.id.editDate)
        editTime = view.findViewById<TextView>(R.id.editTime)

        return view
    }

    override fun onStart() {
        super.onStart()
        val reminderKey = activity?.intent?.getSerializableExtra("reminder").toString()
        val auth = FirebaseAuth.getInstance()
        Log.v("KeyRem", reminderKey)
        val reminderRef = Firebase.database.reference.child("users").child(auth.uid!!).child("reminders").child(reminderKey)

        reminderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val title = snapshot.child("title").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val date = snapshot.child("date").value.toString()
                    val time = snapshot.child("time").value.toString()
                    val key = snapshot.child("key").value.toString()
                    val addToCal = snapshot.child("addToCal").value
                    val addToNotes = snapshot.child("addToNotes").value


                    // Do something with the retrieved data
                    detailReminder = Reminders(title,description,date,time,key,
                        addToCal as Boolean?,addToNotes as Boolean?
                    )
                    Log.v("Details", detailReminder.key.toString())

                    editTitle.text = detailReminder.title
                    editDescription.text = detailReminder.description
                    editDate.text = detailReminder.date
                    editTime.text = detailReminder.time

                    val updateButton = view!!.findViewById<ImageButton>(R.id.editButton)

                    updateButton.setOnClickListener {
                        //val newTitle = titleTextView.text.toString()
                        //val newBody = bodyTextView.text.toString()
                        Log.v("Details", detailReminder.key.toString())

                        val i = Intent(view!!.context, AddActivity::class.java)
                        i.putExtra("fragment", "reminders")
                        i.putExtra("edit", true)
                        i.putExtra("editEntry", detailReminder)
                        startActivity(i)
                    }

                } else {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occurred during the data retrieval
                Log.e("FirebaseError", "Error getting data: ${error.message}")
            }
        })
    }
    companion object {
        fun newInstance(): DetailsRemindersFragment {
            return DetailsRemindersFragment()
        }
    }
}
