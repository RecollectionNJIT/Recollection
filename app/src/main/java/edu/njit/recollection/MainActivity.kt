package edu.njit.recollection

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

public lateinit var bottomNavigationView: BottomNavigationView
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Create the notification channel
        createNotificationChannel()

        // Declare MainActivity fragments
        val mainCalendarFragment: Fragment = MainCalendarFragment()
        val mainFinancesFragment: Fragment = MainFinancesFragment()
        val mainHomeFragment: Fragment = MainHomeFragment()
        val mainNotesFragment: Fragment = MainNotesFragment()
        val mainRemindersFragment: Fragment = MainRemindersFragment()

        // Declare nav bar
        bottomNavigationView = findViewById(R.id.main_bottom_navigation)

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
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ReminderChannel"
            val descriptionText = "Channel for reminder notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channelId = "REMINDER_CHANNEL_ID"

            // Log messages to track the execution flow
            Log.d("NotificationChannel", "Creating Notification Channel")

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Log messages to track the execution flow
            Log.d("NotificationChannel", "Notification Manager retrieved")

            notificationManager.createNotificationChannel(channel)

            // Log messages to track the execution flow
            Log.d("NotificationChannel", "Notification Channel created")


        }
    }
}