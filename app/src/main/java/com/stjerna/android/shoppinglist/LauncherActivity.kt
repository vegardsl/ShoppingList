package com.stjerna.android.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.stjerna.android.shoppinglist.usecase.signup.SignUpActivity

class LauncherActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, SignUpActivity::class.java))
        } else {
            Toast.makeText(this, "Welcome back, ${currentUser.displayName}", Toast.LENGTH_LONG).show()
        }
    }
}