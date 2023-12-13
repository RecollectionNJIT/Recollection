package edu.njit.recollection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.teal)

        // Initialize Firebase
        FirebaseApp.initializeApp(applicationContext)
        val auth = FirebaseAuth.getInstance()

        auth.addAuthStateListener {
            if (auth.currentUser != null) {
                val i = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(i)
                finish()
            } else {
                val i = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }
}