package edu.njit.recollection
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class NotesAdapter (private val context: Context, private val notesList: List<Note>): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_note_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesAdapter.ViewHolder, position: Int) {
        val note = notesList[position]
        holder.bind(note)
    }

    override fun getItemCount() = notesList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
            private val titleTextView = itemView.findViewById<TextView>(R.id.noteRVTitle)
            private val bodyTextView = itemView.findViewById<TextView>(R.id.noteRVBody)
            private val imageView = itemView.findViewById<ImageView>(R.id.noteRVPicture)

            init {
                itemView.setOnClickListener(this)
            }

            fun bind(note: Note) {
                titleTextView.text = note.title
                bodyTextView.text = note.body

                if (!note.imageLocation.isNullOrBlank()) {
                    Glide.with(context)
                        .load(note.imageLocation)
                        .into(imageView)
                } else {
                    // If there is no image, you may want to set a placeholder or handle it in some way
                    imageView.setImageDrawable(null) // Set to null or provide a placeholder image
                }
            }

        override fun onClick(v: View?) {
            val note = notesList[adapterPosition]



            val intent = Intent(context, DetailsNotesFragment::class.java)
            intent.putExtra("fragment","notes")
            intent.putExtra("Notes_Item", note)
            context.startActivity(intent)


        }


        }


}
