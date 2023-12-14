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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class NotesAdapter (private val context: Context, private val notesList: MutableList<Note>): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
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
        val note = notesList[position]
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
            i.putExtra("note",note.key)
            context.startActivity(i)
        }
        holder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                val auth = FirebaseAuth.getInstance()
                if (note.addToCal == true)
                    Firebase.database.reference.child("users").child(auth.uid!!).child("calendar").child(note.key!!).child("addToNotes").setValue("false")
                if (note.addToReminders == true)
                    Firebase.database.reference.child("users").child(auth.uid!!).child("reminders").child(note.key!!).child("addToNotes").setValue("false")
                Firebase.database.reference.child("users").child(auth.uid!!).child("notes").child(note.key!!).removeValue()
                notesList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
                return true
            }
        })
    }

    override fun getItemCount() = notesList.size

}
