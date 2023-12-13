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
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    fun normalizeImageUri(uri: String?): String? {
        if (uri?.startsWith("/external_primary/images") == true) {
            // Handle the relative path case, construct a content URI if possible
            // Example: content://media/external/images/media/1000000047
            return "content://media$uri"
        }
        if (uri?.startsWith("content://media/external/images/media/") == true) {
            return uri
        }
        val normalizedUri = uri?.substringAfter("/external/images/media/")
        return "content://media/external/images/media/$normalizedUri"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details_notes, container, false)
        titleTextView = view.findViewById<TextView>(R.id.detailsTitleTextView)
        bodyTextView = view.findViewById<TextView>(R.id.detailsBodyTextView)

        imageView = view.findViewById<ImageView>(R.id.detailsImageView)



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
    override fun onStart() {
        super.onStart()
        val noteKey = activity?.intent?.getSerializableExtra("note").toString()
        val auth = FirebaseAuth.getInstance()
        val notesRef = Firebase.database.reference.child("users").child(auth.uid!!).child("notes").child(noteKey)

        notesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val title = snapshot.child("title").value.toString()
                    val body = snapshot.child("body").value.toString()
                    val imageLocation = snapshot.child("imageLocation").value.toString()
                    val key = snapshot.child("key").value.toString()
                    val addToReminders = snapshot.child("addToReminders").value
                    val addToCal = snapshot.child("addToCal").value

                    // Do something with the retrieved data
                    detailNote = Note(title,body,imageLocation,key,
                        addToReminders as Boolean?, addToCal as Boolean?
                    )
                    Log.v("Details", detailNote.key.toString())

                    titleTextView.text = detailNote.title
                    bodyTextView.text = detailNote.body

                    if (!detailNote.imageLocation.isNullOrBlank()) {
                        // Display the selected image in the ImageView using Glide

                        // Clear any previous resources
                        Glide.with(requireContext())
                            .clear(imageView)

                        // Decode the base64 string into a Bitmap
                        val decodedBytes = Base64.decode(detailNote.imageLocation, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        // Load the Bitmap into the ImageView
                        Glide.with(requireContext())
                            .load(bitmap)
                            .into(imageView)
                    }
                    val updateButton = view!!.findViewById<ImageButton>(R.id.btnUpdateDB)

                    updateButton.setOnClickListener {
                        //val newTitle = titleTextView.text.toString()
                        //val newBody = bodyTextView.text.toString()
                        Log.v("Details", detailNote.key.toString())

                        val i = Intent(view!!.context, AddActivity::class.java)
                        i.putExtra("fragment", "notes")
                        i.putExtra("edit", true)
                        i.putExtra("editEntry", detailNote)
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
        fun newInstance() : DetailsNotesFragment {
            return DetailsNotesFragment()
        }
    }
}