package edu.njit.recollection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        // Declare AddActivity fragments
        val addCalendarFragment: Fragment = AddCalendarFragment()
        val addFinancesFragment: Fragment = AddFinancesFragment()
        val addNotesFragment: Fragment = AddNotesFragment()
        val addRemindersFragment: Fragment = AddRemindersFragment()

        // This gets the name of the fragment that called the AddActivity
        val fragmentName = getIntent().getExtras()?.getString("fragment")

        // Figure out what the fragment should be
        var fragment: Fragment
        if (fragmentName == "calendar")
            fragment = addCalendarFragment
        else if (fragmentName == "finances")
            fragment = addFinancesFragment
        else if (fragmentName == "notes")
            fragment = addNotesFragment
        else
            fragment = addRemindersFragment

        // Load the fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.add_frame_layout, fragment)
        fragmentTransaction.commit()
    }
}