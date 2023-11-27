package edu.njit.recollection

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import com.bumptech.glide.Glide
import java.io.IOException
import java.io.OutputStream


class AddNotesFragment : Fragment() {
    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var createButton: Button
    private lateinit var btnChoosePhoto: Button
    private lateinit var imagePreview: ImageView
    private lateinit var btnTakePhoto: Button
    private val CAMERA_PERMISSION_REQUEST = 1001


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
        btnChoosePhoto = view.findViewById(R.id.btnChoosePhoto)
        imagePreview = view.findViewById(R.id.imagePreview)
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto)


        fun startCamera() {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST)
        }

        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            when (requestCode) {
                CAMERA_PERMISSION_REQUEST -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Camera permission granted, proceed with camera-related operations
                        startCamera()
                    } else {
                        // Camera permission denied, handle accordingly (show a message, disable camera functionality, etc.)
                        Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
                // Add other permission request cases if needed
            }
        }

        btnChoosePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, Companion.PICK_PHOTO_REQUEST)
        }
        btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
            } else {
                // Permission is already granted, proceed with camera-related operations
                startCamera()
            }
        }

        fun loadImage(imageUri: Uri?) {
            if (imageUri != null) {
                // Display the selected image in the ImageView using Glide
                imagePreview.visibility = View.VISIBLE
                Glide.with(this)
                    .load(imageUri)
                    .into(imagePreview)
            }
        }

        /* alt load image also doesn't work
        fun loadImage(imageUri: Uri?) {
            if (imageUri != null) {
                // Display the selected image in the ImageView
                imagePreview.visibility = View.VISIBLE

                // Use setImageURI to set the image without Glide
                imagePreview.setImageURI(imageUri)
            }
        }



         */

        fun savePhotoAndGetUri(bitmap: Bitmap?): Uri? {
            if (bitmap == null) {
                return null
            }

            // Get the content resolver
            val contentResolver: ContentResolver = requireActivity().contentResolver

            // Define the image details
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "MyImage") // Display name of the image
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bitmap.width)
                put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            }

            // Define the external content URI to save the image to the Pictures directory
            val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            // Insert the image details into the MediaStore
            val imageUri = contentResolver.insert(imageCollection, contentValues)

            // Open an output stream to the content URI
            imageUri?.let { uri ->
                try {
                    val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
                    outputStream?.use { os ->
                        // Compress and write the bitmap to the output stream
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            return imageUri
        }



        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            when (requestCode) {
                PICK_PHOTO_REQUEST -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val selectedImageUri: Uri? = data.data
                        loadImage(selectedImageUri)
                    }
                }
                TAKE_PHOTO_REQUEST -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val photo: Bitmap? = data.extras?.get("data") as? Bitmap
                        // For taking photos, you might want to save the photo to a file and get its URI
                        // then load the image using Glide
                        val photoUri = savePhotoAndGetUri(photo)
                        loadImage(photoUri)
                    }
                }
            }
        }

        // do stuff here
        // this would be where we store the data to the database
        createButton.setOnClickListener{
            val title = titleEditText.text.toString()
            val body = bodyEditText.text.toString()

            val newNote = Note(title,body,null)
            val auth = FirebaseAuth.getInstance()
            val newNoteEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("notes").push()
            newNoteEntryRef.setValue(newNote)
            activity?.finish()
        }

        return view
    }

    companion object {
        fun newInstance() : AddNotesFragment {
            return AddNotesFragment()
        }

        const val TAKE_PHOTO_REQUEST = 2
        const val PICK_PHOTO_REQUEST = 1
    }
}