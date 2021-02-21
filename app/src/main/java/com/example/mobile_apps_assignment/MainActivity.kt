package com.example.mobile_apps_assignment

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private val client = OkHttpClient();

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

        val searchButton: Button = findViewById(R.id.NavigateToSearchPageButton);
        searchButton.setOnClickListener{
            val intent = Intent(this, SearchScreen::class.java);
            startActivity(intent);
        }

        val testTextView: TextView = findViewById(R.id.apiTestTextView);



        val request = Request.Builder()
                .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=burger&number=10")
                .get()
                .addHeader("x-rapidapi-key", "4c61ca64ffmshb84f7b0b1ac2333p1bfd52jsn9b60ed97c505")
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .build()

        var h: String = "";
        client.newCall(request).enqueue(object : Callback {


            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    h = response.body!!.string();
                   // Log.d("msg", h);

                }
                //Log.d("msg", h);
            }


        })
        Log.d("msg", h);
        val testButton: Button = findViewById(R.id.testButton);
        testButton.setOnClickListener{
            Log.d("msg", h);
            testTextView.text = h;
        }



    }


}