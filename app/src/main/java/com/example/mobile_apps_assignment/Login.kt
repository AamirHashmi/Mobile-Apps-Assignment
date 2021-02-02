package com.example.mobile_apps_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth;

        val emailTextInput:EditText = findViewById(R.id.loginEmailTextInput);
        val passwordTextInput:EditText = findViewById(R.id.loginPasswordTextInput);
        val loginButton: Button = findViewById(R.id.loginButton);

        loginButton.setOnClickListener{

            val email = if(emailTextInput.text.isEmpty()) " " else emailTextInput.text.toString().trim();
            val password = if(passwordTextInput.text.isEmpty()) " " else passwordTextInput.text.toString().trim();

            if(!isValidEmail(email)){
                Toast.makeText(baseContext, "Invalid Email Address", Toast.LENGTH_SHORT).show()
            } else if(!isValidPassword(password)){
                Toast.makeText(baseContext, "Invalid Password", Toast.LENGTH_SHORT).show()
            } else{
                signIn(email, password);
            }
        }

    }

    private fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("msg", "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Logged In Successfully", Toast.LENGTH_SHORT).show()
                    finish();
                    val intent = Intent(this, MainActivity::class.java);
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("msg", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }

                // ...
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val PASSWORD_PATTERN: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}"
        )
        return !TextUtils.isEmpty(password) && PASSWORD_PATTERN.matcher(password).matches()

    }
}