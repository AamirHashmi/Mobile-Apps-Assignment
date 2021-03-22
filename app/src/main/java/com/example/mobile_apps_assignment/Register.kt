package com.example.mobile_apps_assignment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        // Initialize Firebase Auth
        auth = Firebase.auth;

        val registerButton: Button = findViewById(R.id.registerButton);
        val emailTextInput: EditText = findViewById(R.id.emailTextInput);
        val nameTextInput: EditText = findViewById(R.id.registerNameTextInput);
        val passwordTextInput: EditText = findViewById(R.id.passwordTextInput);




        registerButton.setOnClickListener{
            val email = if(emailTextInput.text.isEmpty()) " " else emailTextInput.text.toString().trim();
            val password = if(passwordTextInput.text.isEmpty()) " " else passwordTextInput.text.toString().trim();
            val name = if(nameTextInput.text.isEmpty()) " " else nameTextInput.text.toString().trim();


            Log.d("msg", "This is the email: " + email);
            Log.d("msg", "This is the password: " + password);

            if(!isValidEmail(email)){
                Toast.makeText(baseContext, "Invalid Email Address", Toast.LENGTH_SHORT).show()
            } else if(!isValidPassword(password)){
                Toast.makeText(baseContext, "Invalid Password", Toast.LENGTH_SHORT).show()
            }
            else{
                signUp(email, password, name);
            }
        }

    }


    private fun signUp(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("msg", "createUserWithEmail:success")

                    //send verification email
                    sendSendVerificationEmail();
                    Toast.makeText(baseContext, "Registered Successfully", Toast.LENGTH_SHORT).show()
                    finish();
                    val intent = Intent(this, MainActivity::class.java);
                    intent.putExtra("name", name);
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("msg", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

    private fun sendSendVerificationEmail(){
        val user = auth.currentUser

        if(user != null){
            user!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("msg", "Email sent.")
                        Toast.makeText(baseContext, "Email Sent", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(baseContext, "Please Login", Toast.LENGTH_SHORT).show()
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