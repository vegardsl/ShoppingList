package com.stjerna.android.shoppinglist.usecase.signup

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.stjerna.android.shoppinglist.CloudUserGateway
import com.stjerna.android.shoppinglist.Failure
import com.stjerna.android.shoppinglist.R
import com.stjerna.android.shoppinglist.Success
import com.stjerna.android.shoppinglist.entity.User
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    companion object {
        val TAG: String = SignUpActivity::class.java.simpleName
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.sign_up_button -> signUp()
            else -> error("No action.")
        }
    }

    private fun signUp() {
        val email = email_editText.text.toString()
        val password = password_editText.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    auth.currentUser?.let { firebaseUser ->
                        val user = User(
                            firebaseUser.uid,
                            "Nick Name",
                            firebaseUser.email ?: error("Must have email."),
                            mutableListOf()
                        )

                        CloudUserGateway().put(user) {
                            when (it) {
                                is Success -> Toast.makeText(
                                    baseContext, "Authentication success.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                is Failure -> Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // ...
            }
    }

}