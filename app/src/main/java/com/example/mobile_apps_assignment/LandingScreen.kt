package com.example.mobile_apps_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LandingScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_screen)

        auth = Firebase.auth;

        val loginButton: Button = findViewById(R.id.menuLoginButton);
        val registerButton: Button = findViewById(R.id.menuRegisterButton);

        loginButton.setOnClickListener{
            val intent = Intent(this, Login::class.java);
            startActivity(intent)
        }

        registerButton.setOnClickListener{
            val intent = Intent(this, Register::class.java);
            startActivity(intent)
        }

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser;
        //checks if the user is signed in
        if(currentUser!=null){
            //refresh the page
            finish();
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent)
        }
    }
}