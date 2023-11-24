package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class MainFinancesFragment : Fragment() {
    private lateinit var rvFinance: RecyclerView
    private lateinit var adapter: FinanceEntryAdapter
    private val items = mutableListOf<FinanceEntry>()
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
            i.putExtra("entry", FinanceEntry("January 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
            startActivity(i)
        }

        // Initialize the RecyclerView
        rvFinance = view.findViewById<RecyclerView>(R.id.rvFinance)
        rvFinance.layoutManager = LinearLayoutManager(view.context)
        adapter = FinanceEntryAdapter(view.context, items)
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
        // Call the new method within onViewCreated
        updateRV()
    }
    companion object {
        fun newInstance() : MainFinancesFragment {
            return MainFinancesFragment()
        }
    }

    fun updateRV() {
        items.clear()
        items.add(FinanceEntry("January 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceEntry("February 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceEntry("March 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceEntry("April 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceEntry("May 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceEntry("June 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        adapter.notifyDataSetChanged()
    }
}