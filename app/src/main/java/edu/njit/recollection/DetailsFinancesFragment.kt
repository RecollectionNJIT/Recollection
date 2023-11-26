package edu.njit.recollection

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DetailsFinancesFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details_finances, container, false)
        val type = activity?.intent?.extras?.getString("type")

        // Get all entries from database, sorted by date.
        val entries = arrayListOf<FinanceEntry>()
        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference
        databaseRef.child("users").child(auth.uid!!).child("finances")
            .orderByChild("date").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        // Depending on the button press leading us to DetailActivity, we need different entries.
                        if (type != "alltime" && child.child("monthDate").getValue().toString() != type)
                            continue
                        else {
                            val entry = FinanceEntry(
                                child.child("date").getValue().toString(),
                                child.child("monthDate").getValue().toString(),
                                child.child("type").getValue().toString(),
                                child.child("category").getValue().toString(),
                                child.child("amount").getValue().toString().toDouble(),
                                child.key
                            )
                            entries.add(entry)
                            // Create row for entry in table
                            addRow(view, entry)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebaseFinanceAdd", "error", Throwable(error.toString()))
                }
            })

        return view
    }

    fun addRow(view:View, entry: FinanceEntry) {
        val financeEntryTable = view.findViewById<TableLayout>(R.id.financeEntryTable)
        // Create new Row
        val newTableRow = TableRow(view.context)
        newTableRow.background = ContextCompat.getDrawable(view.context, R.color.white)

        // Create new TextViews
        val dateTV = TextView(view.context)
        val typeTV = TextView(view.context)
        val categoryTV = TextView(view.context)
        val amountTV = TextView(view.context)

        // Get info from Finance Entry and style the TextViews
        dateTV.text = entry.date
        dateTV.gravity = Gravity.CENTER_HORIZONTAL
        dateTV.setTextColor(ContextCompat.getColor(view.context, R.color.teal))
        dateTV.textSize = 15F
        dateTV.setPadding(10)
        newTableRow.addView(dateTV)

        typeTV.text = entry.type
        typeTV.gravity = Gravity.CENTER_HORIZONTAL
        typeTV.setTextColor(ContextCompat.getColor(view.context, R.color.teal))
        typeTV.textSize = 15F
        typeTV.setPadding(10)
        newTableRow.addView(typeTV)

        categoryTV.text = entry.category
        categoryTV.gravity = Gravity.CENTER_HORIZONTAL
        categoryTV.setTextColor(ContextCompat.getColor(view.context, R.color.teal))
        categoryTV.textSize = 15F
        categoryTV.setPadding(10)
        newTableRow.addView(categoryTV)

        amountTV.text = entry.amount.toString()
        amountTV.gravity = Gravity.CENTER_HORIZONTAL
        amountTV.setTextColor(ContextCompat.getColor(view.context, R.color.teal))
        amountTV.textSize = 15F
        amountTV.setPadding(10)
        newTableRow.addView(amountTV)

        financeEntryTable.addView(newTableRow)
    }
    companion object {
        fun newInstance() : DetailsFinancesFragment {
            return DetailsFinancesFragment()
        }
    }
}