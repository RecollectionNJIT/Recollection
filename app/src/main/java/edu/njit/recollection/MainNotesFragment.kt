package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class MainNotesFragment : Fragment() {
    private lateinit var rvNotes: RecyclerView
    private lateinit var adapter: NotesAdapter
    private val notes = mutableListOf<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_notes, container, false)
        // do stuff here
        rvNotes = view.findViewById<RecyclerView>(R.id.rvNotes)
        rvNotes.layoutManager = GridLayoutManager(view.context, 2)
        adapter = NotesAdapter(view.context, notes)
        rvNotes.adapter = adapter

        view.findViewById<Button>(R.id.btnDetailsNotes).setOnClickListener {
            val i = Intent(view.context, DetailsActivity::class.java)
            i.putExtra("fragment", "notes")
            startActivity(i)
        }
        view.findViewById<Button>(R.id.btnAddNotes).setOnClickListener {
            val i = Intent(view.context, AddActivity::class.java)
            i.putExtra("fragment", "notes")
            startActivity(i)
            adapter.notifyDataSetChanged()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Call the new method within onViewCreated
        updateRV()
    }


    companion object {
        fun newInstance() : MainNotesFragment {
            return MainNotesFragment()
        }
    }

    fun updateRV() {
        notes.clear()
        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference
        databaseRef.child("users").child(auth.uid!!).child("notes").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val note = mutableListOf<String>()
                for (child in snapshot.children) {
                    val title = child.child("title").getValue().toString()
                    val body = child.child("body").getValue().toString()
                    val imageLoc = child.child("imageLocation").getValue().toString()
                    val newNote = Note(title, body, imageLoc)
                    notes.add(newNote)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebaseFinanceMain", "error", Throwable(error.toString()))
            }
        })
    }
}