package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainFinancesFragment : Fragment() {
    private lateinit var rvFinance: RecyclerView
    private lateinit var adapter: FinanceSpreadsheetAdapter
    private val items = mutableListOf<FinanceSpreadsheet>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_finances, container, false)

        // Initialize the RecyclerView
        rvFinance = view.findViewById<RecyclerView>(R.id.rvFinance)
        rvFinance.layoutManager = LinearLayoutManager(view.context)
        adapter = FinanceSpreadsheetAdapter(view.context, items)
        rvFinance.adapter = adapter

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
        items.add(FinanceSpreadsheet("January 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceSpreadsheet("February 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceSpreadsheet("March 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceSpreadsheet("April 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceSpreadsheet("May 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        items.add(FinanceSpreadsheet("June 2023", "1Bm0rRYIoD_jHncar78Z98Fi4HDiHCSp_xanuxYtVQdg"))
        adapter.notifyDataSetChanged()
    }
}