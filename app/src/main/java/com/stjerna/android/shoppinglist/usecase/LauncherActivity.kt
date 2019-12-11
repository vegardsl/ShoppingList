package com.stjerna.android.shoppinglist.usecase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.stjerna.android.shoppinglist.R
import com.stjerna.android.shoppinglist.usecase.createlist.DashboardActivity
import com.stjerna.android.shoppinglist.usecase.login.LoginActivity

class LauncherActivity : Activity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        Thread.sleep(1000L) // To prevent instantaneous launch of next activity.
        finish()
        if (currentUser != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}