package edu.njit.recollection

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import java.util.Calendar
import java.util.Locale

class AddFinancesFragment : Fragment() {
    lateinit var myCalendar: Calendar
    lateinit var selectMonthET: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_finances, container, false)
        // Retrieve Date from User
        selectMonthET = view.findViewById(R.id.selectMonthET)
        myCalendar = Calendar.getInstance()
        val date =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        selectMonthET.setOnClickListener {
            val dpd = DatePickerDialog(
                view.context,
                android.R.style.Theme_Holo_Light_Dialog,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
            dpd.datePicker.findViewById<View>(resources.getIdentifier("day","id","android")).visibility = View.GONE
            dpd.show()
        }

        // Create the Spreadsheet entry
        view.findViewById<Button>(R.id.createSpreadsheetBtn).setOnClickListener {
//            activity?.finish()
        }

        return view
    }
    private fun updateLabel() {
        val myFormat = "MMMM yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        selectMonthET.setText(dateFormat.format(myCalendar.getTime()))
    }
    companion object {
        fun newInstance() : AddFinancesFragment {
            return AddFinancesFragment()
        }
    }
}