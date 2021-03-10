package com.example.mobile_apps_assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ProfileScreen : AppCompatActivity() {

    private lateinit var favouritesListRecyclerView: RecyclerView;
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_screen)

        val profileImageView: ImageView = findViewById(R.id.profilePictureImageView);
        val profileNameTextView: TextView = findViewById(R.id.profileNameTextView);

        favouritesListRecyclerView = findViewById(R.id.profileFavouritesRecipeList);

        val favouritesList: MutableList<RecipeSearchItem> = mutableListOf();

        val currentUser = Firebase.auth.currentUser;

        profileNameTextView.text = currentUser!!.email;
        //Picasso.get().load(currentUser.photoUrl).into(profileImageView);


        database = Firebase.database.reference.child("users").child(currentUser!!.uid).child("favourites");

        fun updateListUI(){
            runOnUiThread {
                val favouritesListDataAdapter = SearchAdapter(favouritesList);
                favouritesListRecyclerView.adapter = favouritesListDataAdapter;
                favouritesListRecyclerView.layoutManager = LinearLayoutManager(this);
            }
        }

        val favouritesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                favouritesList.clear()
                for (snap in dataSnapshot.getChildren()) {
                    val favourite = RecipeSearchItem(snap.getValue(RecipeSearchItem::class.java)!!.id, snap.getValue(RecipeSearchItem::class.java)!!.title, snap.getValue(RecipeSearchItem::class.java)!!.image);

                    favouritesList.add(favourite)
                    println("YO")

                }
                updateListUI();

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("msg", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(favouritesListener)
    }
}