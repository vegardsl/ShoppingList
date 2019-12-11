package com.stjerna.android.shoppinglist.usecase.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.stjerna.android.shoppinglist.R
import com.stjerna.android.shoppinglist.usecase.createlist.DashboardActivity
import com.stjerna.android.shoppinglist.usecase.signup.SignUpActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener {
            login(
                email_editText.text.toString(),
                password_editText.text.toString()
            )
        }

        sign_up_button.setOnClickListener {
            finish()
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    finish()
                    startActivity(Intent(this, DashboardActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    companion object {
        val TAG = LoginActivity::class.java.simpleName
    }
}