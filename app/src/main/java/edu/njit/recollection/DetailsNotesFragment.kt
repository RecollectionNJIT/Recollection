package edu.njit.recollection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class DetailsNotesFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details_notes, container, false)
        // do stuff here
        val titleTextView = view.findViewById<TextView>(R.id.detailsTitleTextView)
        val bodyTextView = view.findViewById<TextView>(R.id.detailsBodyTextView)
        val imageView = view.findViewById<ImageView>(R.id.detailsImageView)
        val bundle = arguments
        if (bundle != null && bundle.containsKey("Notes_Item")) {
            val note = bundle.getSerializable("Notes_Item") as Note
            // Set data to TextViews and ImageView
            titleTextView.text = note.title
            bodyTextView.text = note.body

            if (!note.imageLocation.isNullOrBlank()) {
                Glide.with(requireContext())
                    .load(note.imageLocation)
                    .into(imageView)
            } else {
                // If there is no image, you may want to set a placeholder or handle it in some way
                imageView.setImageDrawable(null) // Set to null or provide a placeholder image
            }
        }


        return view
    }

    companion object {
        fun newInstance() : DetailsNotesFragment {
            return DetailsNotesFragment()
        }
    }
}