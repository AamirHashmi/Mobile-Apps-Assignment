package com.example.mobile_apps_assignment

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile_screen.*
import kotlinx.android.synthetic.main.nav_header.view.*
import okhttp3.OkHttpClient
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var auth: FirebaseAuth
    private val client = OkHttpClient();
    private lateinit var noti: NotificationChannel;
    private lateinit var database: DatabaseReference
    private val storage = FirebaseStorage.getInstance();
    private lateinit var storageRef: StorageReference;

    private lateinit var headerLayout: View;

    private lateinit var drawer:DrawerLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //setting user profile
        auth = Firebase.auth;
        val userId = auth.currentUser!!.uid;
        var userName:String = "";

        database = Firebase.database.reference
        storageRef =  storage.reference.child("images/" +userId+ ".jpg");


        val tb:Toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)

        drawer=findViewById(R.id.drawer_layout);

        val actionBarDrawToggle= ActionBarDrawerToggle(this, drawer, toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(actionBarDrawToggle);
        actionBarDrawToggle.syncState();

        val navView: NavigationView = findViewById(R.id.nav_view);
        //hamdling nav on click events

        navView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SearchFragment()).commit();
            navView.setCheckedItem(R.id.nav_search);
        }




        //setting nav bar header values
         headerLayout = navView.getHeaderView(0)
        headerLayout.navBarInfo.text = auth.currentUser!!.email.toString();

        headerLayout.setOnClickListener{
            val intent = Intent(this, ProfileScreen::class.java);
            intent.putExtra("username", userName);

            startActivity(intent);
        }

        if (intent.hasExtra("name")){
            userName = intent.getStringExtra("name");
            if(userName != null){
                database!!.child("users").child(userId).child("username").setValue(userName)
                    .addOnSuccessListener {  }
                    .addOnFailureListener{
                        Log.d("msg", "FAILED IN HEREEE");
                    }
            }
        }   else {
            val nameListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI

                    userName = dataSnapshot!!.getValue<String>()!!
                    headerLayout.navBarUserName.text = userName.toString();
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("msg", "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.child("users").child(userId).child("username").addValueEventListener(nameListener);
        };





       val navImage = storageRef.downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'
//            Toast.makeText(this, "succesuful", Toast.LENGTH_SHORT).show();
            Picasso.get().load(it).into(headerLayout.navBarImageView);
        }.addOnFailureListener {
            // Handle any errors
        }



        //notification stuff

        val calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 16);
        calendar.set(Calendar.SECOND,0)

         val alarmManager =
                this.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val intent = Intent(applicationContext, NotificationReciever::class.java);
        val pendingIntent = PendingIntent.getBroadcast(applicationContext,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        alarmManager!!.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent)



    }

    //example of overriding on resume to load in a profile image to the nav bar if it just been changed
    override fun onResume() {
        super.onResume()
        val navImage = storageRef.downloadUrl.addOnSuccessListener {

            Picasso.get().load(it).into(headerLayout.navBarImageView);
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    fun logOut(){
        auth.signOut();
        finish();
        val intent = Intent(this, LandingScreen::class.java);
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_search -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SearchFragment()).commit();
            R.id.nav_shopping_list  -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ShoppingListsFragment()).commit();
            R.id.nav_map  -> startActivity(Intent(this, MapsScreen::class.java));
            R.id.nav_food_analysis  -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FoodAnalysisFragment()).commit();
            R.id.nav_language  -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LanguageFragment()).commit();
            R.id.nav_logout  -> logOut();
        }
        drawer.closeDrawer(GravityCompat.START);
       return true;
    }

    override fun onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }

    }



}