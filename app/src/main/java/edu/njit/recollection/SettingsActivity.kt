package edu.njit.recollection

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        window.statusBarColor = ContextCompat.getColor(this, R.color.teal)

        findViewById<ImageButton>(R.id.backFromSettingsBtn).setOnClickListener {
            finish()
        }

        val auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btnGoogleSignOut).setOnClickListener {
            val googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
            signOut(googleSignInClient, auth)
            revokeAccess(googleSignInClient, auth)
        }
    }

    private fun signOut(googleSignInClient: GoogleSignInClient, auth: FirebaseAuth) {
        auth.signOut()
        googleSignInClient.signOut()
//            .addOnCompleteListener(this) {
//                // Do Stuff On Complete
//            }
    }
    private fun revokeAccess(googleSignInClient: GoogleSignInClient, auth: FirebaseAuth) {
        auth.signOut()
        googleSignInClient.revokeAccess()
//            .addOnCompleteListener(this) {
//                // Do Stuff On Complete
//            }
    }
}