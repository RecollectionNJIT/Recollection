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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import java.util.Calendar
import java.util.Locale
import android.util.Base64
import android.widget.ImageButton
import java.io.ByteArrayOutputStream


class AddNotesFragment : Fragment() {
    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var createButton: Button
    private lateinit var btnChoosePhoto: Button
    private lateinit var imagePreview: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var pageTitle: TextView
    private val CAMERA_PERMISSION_REQUEST = 1001
    lateinit var sendToCal : CheckBox
    lateinit var sendToReminders : CheckBox
    lateinit var selectNoteDate: EditText
    lateinit var selectNoteTime: EditText
    lateinit var myCalendar: Calendar
    lateinit var editNote: Note
    var photoBase64: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    fun bitmapToBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) {
            return null
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun savePhotoAndGetBase64(bitmap: Bitmap?): String? {
        return bitmapToBase64(bitmap)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_notes, container, false)
        requireActivity().window.statusBarColor = ContextCompat.getColor(view.context, R.color.notes)

        myCalendar = Calendar.getInstance()


        fun loadImage(base64String: String?) {
            Log.d("MyApp", "Loading image from Base64 string")

            if (!base64String.isNullOrBlank()) {
                // Display the selected image in the ImageView using Glide

                imagePreview.visibility = View.VISIBLE

                // Clear any previous resources
                Glide.with(this)
                    .clear(imagePreview)

                // Decode the base64 string into a Bitmap
                val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                // Load the Bitmap into the ImageView
                Glide.with(this)
                    .load(bitmap)
                    .into(imagePreview)
            }
        }

        fun handleCameraResult(data: Intent?) {
            val photo: Bitmap? = data?.getParcelableExtra("data")
            photoBase64 = savePhotoAndGetBase64(photo).toString()
            loadImage(photoBase64)
            // Use photoBase64 as needed
        }

        fun handleGalleryResult(data: Intent?) {
            val selectedImageUri: Uri? = data?.data
            photoBase64 = savePhotoAndGetBase64(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)).toString()
            loadImage(photoBase64)
            // Use photoBase64 as needed
        }

        fun updateLabel() {
            val myFormat = "MMMM dd, yyyy"
            val dateFormat = SimpleDateFormat(myFormat, Locale.US)
            selectNoteDate.setText(dateFormat.format(myCalendar.time))
        }

        fun updateTimeLabel() {
            val myFormat = "hh:mm a"
            val timeFormat = SimpleDateFormat(myFormat, Locale.US)
            selectNoteTime.setText(timeFormat.format(myCalendar.time))
        }

        fun calendarDate(oldDate: String) : String {
            val oldFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
            val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return newFormat.format(oldFormat.parse(oldDate))
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
        pageTitle = view.findViewById(R.id.tvAddNoteHeader)
        sendToCal = view.findViewById(R.id.checkboxAddToCalendar)
        sendToReminders = view.findViewById(R.id.checkboxAddToReminders)
        selectNoteTime = view.findViewById(R.id.selectNoteTime)
        selectNoteDate = view.findViewById(R.id.selectNoteDate)
        val dateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }

        selectNoteDate.setOnClickListener {
            val dpd = DatePickerDialog(
                view.context,
                dateListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            //dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
            //dpd.show()
            dpd.datePicker.maxDate = Long.MAX_VALUE;
            dpd.show();
        }

        val timeListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                updateTimeLabel()
            }

        selectNoteTime.setOnClickListener {
            val tpd = TimePickerDialog(
                view.context,
                timeListener,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                false
            )
            tpd.show()
        }

        val checkBoxListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            // Check if at least one checkbox is checked
            val isVisible = sendToCal.isChecked || sendToReminders.isChecked

            // Set the visibility based on the condition
            selectNoteTime.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
            selectNoteDate.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        }

        sendToCal.setOnCheckedChangeListener(checkBoxListener)
        sendToReminders.setOnCheckedChangeListener(checkBoxListener)

        val editBool = activity?.intent?.extras?.getBoolean("edit",false)

        if (editBool == true) {
            editNote = activity?.intent?.extras?.getSerializable("editEntry") as Note
            pageTitle.text = "Editing Note"
            createButton.text = "Save Edits"
            titleEditText.setText(editNote.title)
            bodyEditText.setText(editNote.body)
            sendToReminders.visibility = View.INVISIBLE
            sendToCal.visibility = View.INVISIBLE

            if (!editNote.imageLocation.isNullOrBlank()) {
                // Display the selected image in the ImageView using Glide

                imagePreview.visibility = View.VISIBLE

                // Clear any previous resources
                Glide.with(this)
                    .clear(imagePreview)

                // Decode the base64 string into a Bitmap
                val decodedBytes = Base64.decode(editNote.imageLocation, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                // Load the Bitmap into the ImageView
                Glide.with(this)
                    .load(bitmap)
                    .into(imagePreview)
            }

        }


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

        // this is where we store the data to the database
        createButton.setOnClickListener{

            val title = titleEditText.text.toString()
            val body = bodyEditText.text.toString()
            //if it's being edited, won't allow checkboxes or add to calendar
            if(editBool == true) {
                //check if they swapped the image
                if(photoBase64 == null) {
                    val postValues = mapOf(
                        "title" to title,
                        "body" to body,
                        "imageLocation" to editNote.imageLocation, // Keep the existing image location
                        "key" to editNote.key
                    )

                    val auth = FirebaseAuth.getInstance()
                    val updateNoteRef = Firebase.database.reference.child("users").child(auth.uid!!).child("notes").child(editNote.key!!)
                    updateNoteRef.updateChildren(postValues)
                    val toCal = editNote.addToCal
                    val toRem = editNote.addToReminders
                    if(toCal == true) {
                        val auth = FirebaseAuth.getInstance()
                        val updateMap = HashMap<String, Any>()
                        updateMap["title"] = title
                        updateMap["description"] = body
                        val calendarEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(editNote.key!!)
                        calendarEntryRef.updateChildren(updateMap)
                    }
                    if(toRem == true) {
                        val auth = FirebaseAuth.getInstance()
                        val updateMap = HashMap<String, Any>()
                        updateMap["title"] = title
                        updateMap["description"] = body
                        val reminderEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("reminders").child(editNote.key!!)
                        reminderEntryRef.updateChildren(updateMap)
                    }

                    activity?.finish()
                }
                // else get the new path that has been edited
                else {
                    val postValues = mapOf(
                        "title" to title,
                        "body" to body,
                        "imageLocation" to photoBase64, // Keep the existing image location
                        "key" to editNote.key
                    )

                    val auth = FirebaseAuth.getInstance()
                    val updateNoteRef = Firebase.database.reference.child("users").child(auth.uid!!).child("notes").child(editNote.key!!)
                    updateNoteRef.updateChildren(postValues)
                    val toCal = editNote.addToCal
                    val toRem = editNote.addToReminders
                    if(toCal == true) {
                        val auth = FirebaseAuth.getInstance()
                        val updateMap = HashMap<String, Any>()
                        updateMap["title"] = title
                        updateMap["description"] = body
                        val calendarEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(editNote.key!!)
                        calendarEntryRef.updateChildren(updateMap)
                    }
                    if(toRem == true) {
                        val auth = FirebaseAuth.getInstance()
                        val updateMap = HashMap<String, Any>()
                        updateMap["title"] = title
                        updateMap["description"] = body
                        val reminderEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("reminders").child(editNote.key!!)
                        reminderEntryRef.updateChildren(updateMap)
                    }
                    activity?.finish()
                }
            }
            else {
                var newNote = Note(title,body,photoBase64)
                val auth = FirebaseAuth.getInstance()
                val toCal = sendToCal.isChecked
                val toRem = sendToReminders.isChecked
                val newNoteEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("notes").push()
                newNoteEntryRef.setValue(newNote)
                newNote.key = newNoteEntryRef.key
                newNote.addToCal = sendToCal.isChecked
                newNote.addToReminders = sendToReminders.isChecked
                newNoteEntryRef.setValue(newNote)

                if (toCal || toRem) {
                    val date = selectNoteDate.text.toString()
                    val time = selectNoteTime.text.toString()
                    val calDate = calendarDate(date)
                    if(toCal) {
                        val calendarEnt = CalendarEntry(calDate, title, body, time, "N/A", newNote.key,toRem ,true)
                        val auth = FirebaseAuth.getInstance()
                        val newCalendarEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(newNote.key!!)
                        newCalendarEntryRef.setValue(calendarEnt)
                    }
                    if(toRem) {
                        val newRem = Reminders(title,body,date,time,newNote.key,toCal,true)
                        val auth = FirebaseAuth.getInstance()
                        val newReminderEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("reminders").child(newNote.key!!)
                        newReminderEntryRef.setValue(newRem)
                    }

                }
                activity?.finish()
            }
            activity?.finish()
        }
        view.findViewById<ImageButton>(R.id.backFromAddNoteBtn).setOnClickListener {
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