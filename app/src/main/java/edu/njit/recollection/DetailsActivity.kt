package edu.njit.recollection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Declare DetailsActivity fragments
        val detailsCalendarFragment: Fragment = DetailsCalendarFragment()
        val detailsFinancesFragment: Fragment = DetailsFinancesFragment()
        val detailsNotesFragment: Fragment = DetailsNotesFragment()
        val detailsRemindersFragment: Fragment = DetailsRemindersFragment()

        // This gets the name of the fragment that called the DetailsActivity
        val fragmentName = getIntent().getExtras()?.getString("fragment")

        // Figure out what the fragment should be
        var fragment: Fragment
        if (fragmentName == "calendar")
            fragment = detailsCalendarFragment
        else if (fragmentName == "finances")
            fragment = detailsFinancesFragment
        else if (fragmentName == "notes")
            fragment = detailsNotesFragment
        else
            fragment = detailsRemindersFragment

        // Load the fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.details_frame_layout, fragment)
        fragmentTransaction.commit()
    }
}