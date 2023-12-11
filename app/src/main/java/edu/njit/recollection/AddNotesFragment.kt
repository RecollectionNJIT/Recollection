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
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
    private var imagePathForNote: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun savePhotoAndGetUri(bitmap: Bitmap?): Uri? {
        if (bitmap == null) {
            return null
        }

        // Get the content resolver
        val contentResolver: ContentResolver = requireActivity().contentResolver

        val timestamp = System.currentTimeMillis()

        val imageName = "Image_$timestamp.jpg"

        // Define the image details
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageName) // Display name of the image
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        // Define the external content URI to save the image to the Pictures directory
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        // Insert the image details into the MediaStore
        val imageUri = contentResolver.insert(imageCollection, contentValues)
        imagePathForNote = imageUri?.path.toString()

        // Log the URI
        Log.d("MyApp", "Image URI: $imageUri")

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




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_notes, container, false)

        fun loadImage(imageUri: Uri?) {
            Log.d("MyApp", "Loading image from URI: $imageUri")

            if (imageUri != null) {
                // Display the selected image in the ImageView using Glide
                imagePreview.visibility = View.VISIBLE

                // Clear any previous resources
                Glide.with(this)
                    .clear(imagePreview)

                Glide.with(this)
                    .load(imageUri)
                    .into(imagePreview)
            }
        }

        fun handleCameraResult(data: Intent?) {
            val photo: Bitmap? = data?.getParcelableExtra("data")
            val photoUri = savePhotoAndGetUri(photo)
            loadImage(photoUri)
        }

        fun handleGalleryResult(data: Intent?) {
            val selectedImageUri: Uri? = data?.data
            imagePathForNote = selectedImageUri?.path.toString()
            loadImage(selectedImageUri)
        }

        val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                when {
                    data?.hasExtra("data") == true -> handleCameraResult(data)
                    data?.data != null -> handleGalleryResult(data)
                    else -> Log.e("MyApp", "No image data found in the result")
                }
            }
        }

        titleEditText = view.findViewById(R.id.addTitle)
        bodyEditText = view.findViewById(R.id.addBody)
        createButton = view.findViewById(R.id.createNotesButton)
        btnChoosePhoto = view.findViewById(R.id.btnChoosePhoto)
        imagePreview = view.findViewById(R.id.imagePreview)
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto)


        fun startCamera(takePhotoLauncher: ActivityResultLauncher<Intent>) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoLauncher.launch(cameraIntent)
        }





        btnChoosePhoto.setOnClickListener {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            takePhotoLauncher.launch(pickPhotoIntent)
        }

        btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
            } else {
                // Permission is already granted, proceed with camera-related operations
                startCamera(takePhotoLauncher)
            }
        }


        // do stuff here
        // this would be where we store the data to the database
        createButton.setOnClickListener{
            val title = titleEditText.text.toString()
            val body = bodyEditText.text.toString()

            val newNote = Note(title,body,imagePathForNote)
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

    }


}