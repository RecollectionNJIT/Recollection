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
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import java.util.Calendar
import java.util.Locale

class AddFinancesFragment : Fragment() {
    lateinit var myCalendar: Calendar
    lateinit var selectDateET: EditText
    lateinit var monthDate: String
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
        selectDateET = view.findViewById(R.id.selectDateET)
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

        // Retrieve selections from Spinners
        var typeChoice = ""
        var category = ""
        val typeOptions = arrayOf("Income", "Expense")
        val typeSpinnerAdapter = ArrayAdapter(view.context, R.layout.spinner_finance_item, typeOptions)
        view.findViewById<Spinner>(R.id.typeSpinner).adapter = typeSpinnerAdapter
        view.findViewById<Spinner>(R.id.typeSpinner).onItemSelectedListener =
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
                        view.findViewById<Spinner>(R.id.categorySpinner).adapter = categorySpinnerAdapter
                        view.findViewById<Spinner>(R.id.categorySpinner).onItemSelectedListener =
                            object: AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                    category = categoryOptions[p2]
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

        // This makes it so the input for price is locked to 2 places after the decimal.
        val itemPriceET = view.findViewById<EditText>(R.id.itemPriceET)
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
            val newFinanceEntry = FinanceEntry(selectDateET.text.toString(), monthDate, typeChoice, category, itemPriceET.text.toString().toDouble())
            val auth = FirebaseAuth.getInstance()
            val newFinancesEntryRef = Firebase.database.reference.child("users").child(auth.uid!!).child("finances").push()
            newFinancesEntryRef.setValue(newFinanceEntry)
            activity?.finish()
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