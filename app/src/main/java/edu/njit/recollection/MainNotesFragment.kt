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
import androidx.recyclerview.widget.GridLayoutManager

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
        notes.add(Note("Hello","I'm bored",null))
        notes.add(Note("It's me","I'm sick",null))
        notes.add(Note("I don't remember","I'm tired",null))
        notes.add(Note("The words to Adele's songs","I'm dying",null))
        notes.add(Note("So goodnight","I'm ded",null))
        adapter.notifyDataSetChanged()
    }
}