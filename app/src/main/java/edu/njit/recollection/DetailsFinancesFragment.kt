package edu.njit.recollection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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
        val entry = activity?.intent?.extras?.getSerializable("entry") as FinanceEntry
        val id = entry.id

        return view
    }

    companion object {
        fun newInstance() : DetailsFinancesFragment {
            return DetailsFinancesFragment()
        }
    }
}