package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainFinancesFragment : Fragment() {
    private lateinit var rvFinance: RecyclerView
    private lateinit var adapter: FinanceMonthAdapter
    private val items = mutableListOf<FinanceMonth>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_finances, container, false)

        // Set up Alltime Finances Button
        view.findViewById<Button>(R.id.btnSeeAlltimeFinances).setOnClickListener {
            val i = Intent(view.context, DetailsActivity::class.java)
            i.putExtra("fragment", "finances")
            i.putExtra("type", "alltime")
            startActivity(i)
        }

        // Initialize the RecyclerView
        rvFinance = view.findViewById<RecyclerView>(R.id.rvFinance)
        rvFinance.layoutManager = LinearLayoutManager(view.context)
        adapter = FinanceMonthAdapter(view.context, items)
        rvFinance.adapter = adapter

        // Set up Add Button
        view.findViewById<ImageButton>(R.id.btnAddFinances).setOnClickListener {
            val i = Intent(view.context, AddActivity::class.java)
            i.putExtra("fragment", "finances")
            startActivity(i)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Update RecyclerView
        updateRV()
    }
    companion object {
        fun newInstance() : MainFinancesFragment {
            return MainFinancesFragment()
        }
    }
    fun updateRV() {
        items.clear()
        val auth = FirebaseAuth.getInstance()
        val databaseRef = Firebase.database.reference
        databaseRef.child("users").child(auth.uid!!).child("finances")
            .orderByChild("date").addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val months = mutableListOf<String>()
                    for (child in snapshot.children) {
                        val month = child.child("monthDate").getValue().toString()
                        if (month in months) {
                            continue
                        }
                        months.add(month)
                        items.add(FinanceMonth(month))
                        adapter.notifyDataSetChanged()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebaseFinanceMain", "error", Throwable(error.toString()))
                }
            })
    }
}