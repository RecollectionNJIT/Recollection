package edu.njit.recollection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Declare MainActivity fragments
        val mainCalendarFragment: Fragment = MainCalendarFragment()
        val mainFinancesFragment: Fragment = MainFinancesFragment()
        val mainHomeFragment: Fragment = MainHomeFragment()
        val mainNotesFragment: Fragment = MainNotesFragment()
        val mainRemindersFragment: Fragment = MainRemindersFragment()

        // Declare nav bar
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.main_bottom_navigation)

        // Assign fragments to each nav menu choice
        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.nav_calendar -> fragment = mainCalendarFragment
                R.id.nav_finances -> fragment = mainFinancesFragment
                R.id.nav_home -> fragment = mainHomeFragment
                R.id.nav_notes -> fragment = mainNotesFragment
                R.id.nav_reminders -> fragment = mainRemindersFragment
            }
            replaceFragment(fragment)
            true
        }

        // Set default nav bar selection
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    // This is how fragments are swapped
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame_layout, fragment)
        fragmentTransaction.commit()
    }
}