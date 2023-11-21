package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

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
        rvNotes.layoutManager = LinearLayoutManager(view.context)
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
        }
        return view
    }

    companion object {
        fun newInstance() : MainNotesFragment {
            return MainNotesFragment()
        }
    }
}