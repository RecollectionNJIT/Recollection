package edu.njit.recollection

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import java.util.Calendar
import java.util.Locale

class AddFinancesFragment : Fragment() {
    private lateinit var myCalendar: Calendar
    private lateinit var selectDateET: EditText
    private lateinit var monthDate: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_finances, container, false)
        // Instantiate all variables
        selectDateET = view.findViewById(R.id.selectDateET)
        val typeSpinner = view.findViewById<Spinner>(R.id.typeSpinner)
        val categorySpinner = view.findViewById<Spinner>(R.id.categorySpinner)
        val itemPriceET = view.findViewById<EditText>(R.id.itemPriceET)
        val editBool = activity?.intent?.extras?.getBoolean("edit", false)

        // If editing, change UI text to match
        if (editBool == true) {
            view.findViewById<Button>(R.id.addEntryBtn).text = "Save Edits"
            view.findViewById<TextView>(R.id.tvAddFinanceEntryHeader).text = "Editing Entry"
        }

        // Retrieve selections from Spinners
        var typeChoice = ""
        var category = ""
        val typeOptions = arrayOf("Income", "Expense")
        val typeSpinnerAdapter = ArrayAdapter(view.context, R.layout.spinner_finance_item, typeOptions)
        typeSpinner.adapter = typeSpinnerAdapter
        typeSpinner.onItemSelectedListener =
            object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    typeChoice = typeOptions[p2]
                    // Determine categories based on type
                    var categoryOptions = emptyArray<String>()
                    if (typeChoice == "Income") {
                        categoryOptions = arrayOf("Work/Paycheck", "Other")
                    }
                    else if (typeChoice == "Expense") {
                        categoryOptions = arrayOf("Rent", "Groceries", "Dining", "Bills", "Entertainment", "Other")
                    }
                    val categorySpinnerAdapter = ArrayAdapter(view.context, R.layout.spinner_finance_item, categoryOptions)
                    categorySpinner.adapter = categorySpinnerAdapter
                    categorySpinner.onItemSelectedListener =
                        object: AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                category = categoryOptions[p2]
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    if (editBool == true) {
                        val editEntry = activity?.intent?.extras?.getSerializable("editEntry") as FinanceEntry
                        categorySpinner.setSelection((categorySpinner.adapter as ArrayAdapter<String>).getPosition(editEntry.category),true)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // Determine if it is a new entry or an edit to an existing entry
        if (editBool == true) {
            val editEntry = activity?.intent?.extras?.getSerializable("editEntry") as FinanceEntry
            selectDateET.setText(editEntry.date)
            typeSpinner.setSelection((typeSpinner.adapter as ArrayAdapter<String>).getPosition(editEntry.type),true)
            itemPriceET.setText(String.format("%.2f", editEntry.amount))
        }

        // Retrieve Date from User
        myCalendar = Calendar.getInstance()
        val date =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        selectDateET.setOnClickListener {
            val dpd = DatePickerDialog(
                view.context,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        // This makes it so the input for price is locked to 2 places after the decimal.
        itemPriceET.setFilters(
            arrayOf<InputFilter>(
                DigitsInputFilter(
                    Integer.MAX_VALUE,
                    2,
                    Double.POSITIVE_INFINITY
                )
            )
        )

        // Create the Finance entry and send it to db on add button press
        view.findViewById<Button>(R.id.addEntryBtn).setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            if (editBool == false) {
                val newFinanceEntry = FinanceEntry(selectDateET.text.toString(), monthDate, typeChoice, category, itemPriceET.text.toString().toDouble())
                val auth = FirebaseAuth.getInstance()
                val newFinancesEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("finances").push()
                newFinancesEntryRef.setValue(newFinanceEntry)
                activity?.finish()
            }
            else {
                val editEntry = activity?.intent?.extras?.getSerializable("editEntry") as FinanceEntry
                val editedFinanceEntry = FinanceEntry(selectDateET.text.toString(), monthDate, typeChoice, category, itemPriceET.text.toString().toDouble())
                val editFinancesEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("finances").child(editEntry.key!!)
                editFinancesEntryRef.setValue(editedFinanceEntry)
                activity?.finish()
            }
        }
        return view
    }
    private fun updateLabel() {
        var myFormat = "MMMM yyyy"
        var dateFormat = SimpleDateFormat(myFormat, Locale.US)
        //Get the Month and Year from input, store it in a separate variable for object creation
        monthDate = dateFormat.format(myCalendar.getTime())
        //Display a different format to user.
        myFormat = "MM/dd/yyyy"
        dateFormat = SimpleDateFormat(myFormat, Locale.US)
        selectDateET.setText(dateFormat.format(myCalendar.getTime()))
    }
    companion object {
        fun newInstance() : AddFinancesFragment {
            return AddFinancesFragment()
        }
    }
}