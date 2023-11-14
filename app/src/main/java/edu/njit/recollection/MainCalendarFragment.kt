package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class MainCalendarFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_calendar, container, false)
        // do stuff here
        view.findViewById<Button>(R.id.btnDetailsCalendar).setOnClickListener {
            val i = Intent(view.context, DetailsActivity::class.java)
            i.putExtra("fragment", "calendar")
            startActivity(i)
        }
        view.findViewById<Button>(R.id.btnAddCalendar).setOnClickListener {
            val i = Intent(view.context, AddActivity::class.java)
            i.putExtra("fragment", "calendar")
            startActivity(i)
        }
        return view
    }

    companion object {
        fun newInstance() : MainCalendarFragment {
            return MainCalendarFragment()
        }
    }
}