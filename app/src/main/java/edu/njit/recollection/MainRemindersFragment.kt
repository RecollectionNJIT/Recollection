package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainRemindersFragment : Fragment() {
    private lateinit var rvReminders: RecyclerView
    private lateinit var adapter: RemindersAdapter
    private val items = mutableListOf<Reminders>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_reminders, container, false)

        // Initialize the RecyclerView
        rvReminders = view.findViewById(R.id.rvReminders)
        rvReminders.layoutManager = GridLayoutManager(view.context, 2)
        adapter = RemindersAdapter(view.context, items)
        rvReminders.adapter = adapter

//        view.findViewById<Button>(R.id.btnDetailsReminders).setOnClickListener {
//            val i = Intent(view.context, DetailsActivity::class.java)
//            i.putExtra("fragment", "reminders")
//            startActivity(i)
//        }

        view.findViewById<ImageButton>(R.id.btnAddReminders).setOnClickListener {
            val i = Intent(view.context, AddActivity::class.java)
            i.putExtra("fragment", "reminders")
            startActivity(i)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Call the new method within onViewCreated
        updateRV()
    }

    companion object {
        fun newInstance() : MainRemindersFragment {
            return MainRemindersFragment()
        }
    }

    fun updateRV() {
        items.clear()
        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference
        val remindersRef = databaseRef.child("users").child(auth.uid!!).child("reminders")

        remindersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()

                for (child in snapshot.children) {
                    val title = child.child("title").getValue().toString()
                    val description = child.child("description").getValue().toString()
                    val date = child.child("date").getValue().toString()
                    val time = child.child("time").getValue().toString()
                    val id = child.key // Get the ID from the database
                    val newRem = Reminders(title, description, date, time, id)
                    items.add(newRem)
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebaseFinanceMain", "error", Throwable(error.toString()))
            }
        })
    }

}
