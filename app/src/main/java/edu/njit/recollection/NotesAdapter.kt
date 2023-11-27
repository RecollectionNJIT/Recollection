package edu.njit.recollection
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class NotesAdapter (private val context: Context, private val notesList: List<Note>): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.noteRVTitle)
        val bodyTextView = itemView.findViewById<TextView>(R.id.noteRVBody)
        val imageView = itemView.findViewById<ImageView>(R.id.noteRVPicture)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_note_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList.get(position)
        holder.titleTextView.text = note.title
        holder.bodyTextView.text = note.body
        if (!note.imageLocation.isNullOrBlank()) {
            Glide.with(context)
                .load(note.imageLocation)
                .into(holder.imageView)
        } else {
            // If there is no image, you may want to set a placeholder or handle it in some way
            holder.imageView.setImageDrawable(null) // Set to null or provide a placeholder image
        }
        holder.itemView.setOnClickListener {
            val i = Intent(context, DetailsActivity::class.java)
            i.putExtra("fragment","notes")
            i.putExtra("note",note)
            context.startActivity(i)
        }
    }

    override fun getItemCount() = notesList.size







}
