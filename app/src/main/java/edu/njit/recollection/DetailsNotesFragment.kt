package edu.njit.recollection

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DetailsNotesFragment : Fragment() {

    private lateinit var titleTextView: EditText
    private lateinit var bodyTextView: EditText
    private var isTextChanged = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details_notes, container, false)
        val detailNote = activity?.intent?.getSerializableExtra("note") as Note

        // do stuff here
        titleTextView = view.findViewById<EditText>(R.id.detailsTitleTextView)
        bodyTextView = view.findViewById<EditText>(R.id.detailsBodyTextView)
        val imageView = view.findViewById<ImageView>(R.id.detailsImageView)



        // Set data to TextViews and ImageView
        titleTextView.setText(detailNote.title)
        bodyTextView.setText(detailNote.body)

        titleTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for your case
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Set the flag to true when text is changed
                isTextChanged = true
            }

            override fun afterTextChanged(editable: Editable?) {
                // Not needed for your case
            }
        })

        bodyTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for your case
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Set the flag to true when text is changed
                isTextChanged = true
            }

            override fun afterTextChanged(editable: Editable?) {
                // Not needed for your case
            }
        })


        if (!detailNote.imageLocation.isNullOrBlank()) {
            Glide.with(requireContext())
                .load(detailNote.imageLocation)
                .into(imageView)
        } else {
            // If there is no image, you may want to set a placeholder or handle it in some way
            imageView.setImageDrawable(null) // Set to null or provide a placeholder image
        }

        val updateButton = view.findViewById<Button>(R.id.btnUpdateDB)

        updateButton.setOnClickListener{
            val newTitle = titleTextView.text.toString()
            val newBody = bodyTextView.text.toString()
            val postValues = mapOf(
                "title" to newTitle,
                "body" to newBody,
                "imageLocation" to null,
                "key" to detailNote.key
            )
            val auth = FirebaseAuth.getInstance()
            val updateNoteRef = Firebase.database.reference.child("users").child(auth.uid!!).child("notes").child(detailNote.key!!)
            updateNoteRef.updateChildren(postValues)

        }





        return view
    }


    companion object {
        fun newInstance() : DetailsNotesFragment {
            return DetailsNotesFragment()
        }
    }
}