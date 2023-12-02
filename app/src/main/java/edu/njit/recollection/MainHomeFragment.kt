package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView

class MainHomeFragment : Fragment() {
    lateinit var calendarCV: CardView
    lateinit var financeCV: CardView
    lateinit var remindersCV: CardView
    lateinit var weatherCV: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_home, container, false)

        view.findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            val i = Intent(view.context, SettingsActivity::class.java)
            startActivity(i)
        }

        calendarCV = view.findViewById(R.id.cvHomepageCalendar)
        financeCV = view.findViewById(R.id.cvHomepageFinance)
        remindersCV = view.findViewById(R.id.cvHomepageReminders)
        weatherCV = view.findViewById(R.id.cvHomepageWeather)

        createCalendarCV()
        createFinanceCV()
        createRemindersCV()
        createWeatherCV()

        return view
    }

    fun createCalendarCV() {
    }
    fun createFinanceCV() {
    }
    fun createRemindersCV() {
    }
    fun createWeatherCV() {
    }

    companion object {
        fun newInstance() : MainHomeFragment {
            return MainHomeFragment()
        }
    }
}