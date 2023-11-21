package edu.njit.recollection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText



class AddNotesFragment : Fragment() {
    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var createButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_notes, container, false)

        titleEditText = view.findViewById(R.id.addTitle)
        bodyEditText = view.findViewById(R.id.addBody)
        createButton = view.findViewById(R.id.createNotesButton)

        // do stuff here
        // this would be where we store the data to the database
        createButton.setOnClickListener{
            val title = titleEditText.text.toString()
            val body = bodyEditText.text.toString()

            val newNote = Note(title,body,null)
                    }

        return view
    }

    companion object {
        fun newInstance() : AddNotesFragment {
            return AddNotesFragment()
        }
    }
}