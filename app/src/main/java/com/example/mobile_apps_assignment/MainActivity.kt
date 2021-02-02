package com.example.mobile_apps_assignment

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth;

        val logOutButton: Button = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener{
            auth.signOut();
            finish();
            val intent = Intent(this, LandingScreen::class.java);
            startActivity(intent)
        }


    }

}