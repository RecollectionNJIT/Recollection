package edu.njit.recollection

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("ReceiverTriggered", "ReminderBroadcastReceiver received broadcast")

        // Retrieve necessary data from intent
        val title = intent?.getStringExtra("title")

        // Show notification
        showNotification(context, title)
    }

    private fun showNotification(context: Context?, title: String?) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "REMINDER_CHANNEL_ID"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Reminder: $title")
            .setContentText("Your reminder is in 10 minutes!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Show the notification
        notificationManager.notify(1, builder.build())
    }
}
