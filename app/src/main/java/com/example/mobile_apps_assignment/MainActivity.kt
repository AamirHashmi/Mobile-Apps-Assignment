package com.example.mobile_apps_assignment

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import okhttp3.OkHttpClient
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var auth: FirebaseAuth
    private val client = OkHttpClient();
    private lateinit var noti: NotificationChannel;

    private lateinit var drawer:DrawerLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth;

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



        //setting nav bar header values
        val headerLayout: View = navView.getHeaderView(0)
        headerLayout.navBarName.text = "Profile"
        headerLayout.navBarInfo.text = auth.currentUser!!.email.toString();
        headerLayout.setOnClickListener{
            val intent = Intent(this, ProfileScreen::class.java);
            startActivity(intent);
        }



      /*  val logOutButton: Button = findViewById(R.id.logOutButton);
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
        }*/

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



      /*  val testButton: Button = findViewById(R.id.testButton);
        testButton.setOnClickListener{


        }
    */

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
            R.id.nav_map  -> startActivity(Intent(this, MapsScreen::class.java))//supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MapsFragment(), "map-fragment").commit();
            R.id.nav_food_analysis  -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FoodAnalysisFragment()).commit();
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