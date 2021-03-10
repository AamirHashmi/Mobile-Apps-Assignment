package com.example.mobile_apps_assignment

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private val client = OkHttpClient();
    private lateinit var noti: NotificationChannel;

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

        val shoppingListButton: Button = findViewById(R.id.NavigateToShoppingListPageButton);
        shoppingListButton.setOnClickListener{
            val intent = Intent(this, ShoppingListsScreen::class.java);
            startActivity(intent);
        }

        val foodAnalysisButton: Button = findViewById(R.id.openFoodAnalysisScreen);
        foodAnalysisButton.setOnClickListener{
            val intent = Intent(this, FoodAnalysisScreen::class.java);
            startActivity(intent);
        }

        val profileButton: Button = findViewById(R.id.navigateToProfileButton);
        profileButton.setOnClickListener{
            val intent = Intent(this, ProfileScreen::class.java);
            startActivity(intent);
        }

        val mapButton: Button = findViewById(R.id.navigateToMapScreenButton);
        mapButton.setOnClickListener{
            val intent = Intent(this, MapsScreen::class.java);
            startActivity(intent);
        }

        //notification stuff

        val calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND,0)

         val alarmManager =
                this.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val intent = Intent(applicationContext, NotificationReciever::class.java);
        val pendingIntent = PendingIntent.getBroadcast(applicationContext,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager!!.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)



        val testButton: Button = findViewById(R.id.testButton);
        testButton.setOnClickListener{


        }


    }

}