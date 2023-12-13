package edu.njit.recollection

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

    private lateinit var titleTextView: TextView
    private lateinit var bodyTextView: TextView
    private lateinit var detailNote: Note

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
        titleTextView = view.findViewById<TextView>(R.id.detailsTitleTextView)
        bodyTextView = view.findViewById<TextView>(R.id.detailsBodyTextView)
        val imageView = view.findViewById<ImageView>(R.id.detailsImageView)

        // Set data to TextViews and ImageView
        titleTextView.setText(detailNote.title)
        bodyTextView.setText(detailNote.body)

        if (!detailNote.imageLocation.isNullOrBlank()) {
            // Display the selected image in the ImageView using Glide

            // Clear any previous resources
            Glide.with(this)
                .clear(imageView)

            // Decode the base64 string into a Bitmap
            val decodedBytes = Base64.decode(detailNote.imageLocation, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            // Load the Bitmap into the ImageView
            Glide.with(this)
                .load(bitmap)
                .into(imageView)
        }


        /*if (!detailNote.imageLocation.isNullOrBlank()) {
            Log.d("MyApp", "Loading image from URI: ${detailNote.imageLocation}")
            Glide.with(requireContext())
                .load(detailNote.imageLocation)
                .into(imageView)
        } else {
            Log.d("MyApp", "No image location provided.")
            // If there is no image, you may want to set a placeholder or handle it in some way
            imageView.setImageDrawable(null) // Set to null or provide a placeholder image
        }*/

        val updateButton = view.findViewById<ImageButton>(R.id.btnUpdateDB)

        updateButton.setOnClickListener {
            //val newTitle = titleTextView.text.toString()
            //val newBody = bodyTextView.text.toString()


            val i = Intent(view.context, AddActivity::class.java)
            i.putExtra("fragment", "notes")
            i.putExtra("edit", true)
            i.putExtra("editEntry", detailNote)
            startActivity(i)


            /*val postValues = mapOf(
                "title" to detailNote.title,
                "body" to detailNote.body,
                "imageLocation" to detailNote.imageLocation, // Keep the existing image location
                "key" to detailNote.key
            )

            val auth = FirebaseAuth.getInstance()
            val updateNoteRef = Firebase.database.reference.child("users").child(auth.uid!!).child("notes").child(detailNote.key!!)
            updateNoteRef.updateChildren(postValues)
            activity?.finish()*/
        }





        return view
    }



    companion object {
        fun newInstance() : DetailsNotesFragment {
            return DetailsNotesFragment()
        }
    }
}