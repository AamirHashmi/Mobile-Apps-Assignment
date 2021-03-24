package com.example.mobile_apps_assignment

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.MotionEventCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

class RecipeDetailScreen : AppCompatActivity(),  GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    private val client = OkHttpClient();
    private lateinit var instructionsTextView: TextView;
    private lateinit var favouriteButton: FloatingActionButton;
    private lateinit var database: DatabaseReference
    private lateinit var userId: String;
    private lateinit var recipeName: String;
    private lateinit var recipeImage: String;
    private var favourited: Boolean = false;
    private lateinit var recipeId: String;
    private lateinit var favSound: MediaPlayer;
    private lateinit var unfavSound: MediaPlayer;

    private lateinit var gd:GestureDetectorCompat;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail_screen)

        gd = GestureDetectorCompat(this, this);
        gd.setOnDoubleTapListener(this);

        //getting recipe parameters from clicked item in the recycler view
        recipeId = intent.getIntExtra("RecipeId", -1).toString();
        recipeName = "";
        recipeImage = "";

        if(intent.hasExtra("RecipeName")){
             recipeName = intent.getStringExtra("RecipeName").toString()
             recipeImage = intent.getStringExtra("RecipeImage").toString()
        }

        //instantiating view components
        val recipeTitleTextView: TextView = findViewById(R.id.recipeDetailScreenTitleTextView);
        val recipeImageView: ImageView = findViewById(R.id.recipeDetailScreenImageView);
        instructionsTextView = findViewById(R.id.instructionsScrollViewText);

        val shareDataButton: Button = findViewById(R.id.shareDataButton);
        favouriteButton = findViewById(R.id.recipeDetailScreenFavouriteButton);

        //instantiating media player for sound effect
        favSound = MediaPlayer.create(this, R.raw.favourite_sound_effect);
        unfavSound = MediaPlayer.create(this, R.raw.unfav_sound_effect);

        val currentUser = Firebase.auth.currentUser;
        userId = currentUser!!.uid;
        database = Firebase.database.reference;



        val favListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val recipe = dataSnapshot.value; //<RecipeSearchItem>()

                if(recipe != null){
                    favourited = true;
                    favouriteButton.setImageResource(R.drawable.ic_star_fill)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(userId).child("favourites").child(recipeId).addValueEventListener(favListener)
        println("yoyo")


        favouriteButton.setOnClickListener{
            val recipeObj = RecipeSearchItem(recipeId.toInt(), recipeName, recipeImage);
            favouriteRecipe(recipeObj);
        }

        //setting data to view components
        recipeTitleTextView.text=recipeName;

        if(recipeImage !=""){
            recipeImage = recipeImage.replace("https://spoonacular.com/recipeImages/", "")
            Picasso.get().load( "https://spoonacular.com/recipeImages/"+ recipeImage).into(recipeImageView);
        }


        getRecipeInformation(recipeId);

        //Sharing recipes on social media using implicit intents
        shareDataButton.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "I just made " + recipeName + " using the recipe app.")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, getString(R.string.recipe_share));
            startActivity(shareIntent);
        }

    }


    fun getRecipeInformation(recipeId: String) {

        val request = Request.Builder()
                .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/$recipeId/information")
                .get()
                .addHeader("x-rapidapi-key", "4c61ca64ffmshb84f7b0b1ac2333p1bfd52jsn9b60ed97c505")
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val body = response.body!!.string();
                val gson = GsonBuilder().create()
                val recipeInformation = gson.fromJson(body, RecipeInformation::class.java) //need to set this to the instructions
                println("nothing")
                runOnUiThread {
                    if(recipeInformation.instructions != null){
                        instructionsTextView.text = recipeInformation.instructions.toString()
                    }
                   }

                println(body);
            }
        })
    }


    fun favouriteRecipe(recipeObj:RecipeSearchItem){
        if(favourited){
            database!!.child("users").child(userId).child("favourites").child(recipeId).removeValue()
                .addOnSuccessListener {
                    favourited = false
                    favouriteButton.setImageResource(R.drawable.ic_baseline_star_outline_24)
                    unfavSound.start();
                    Toast.makeText(baseContext,R.string.toast_recipe_unfavourite, Toast.LENGTH_SHORT).show();
                }
                .addOnFailureListener{
                    Log.d("msg", "FAILED IN HEREEE");
                }
        }else{
            database!!.child("users").child(userId).child("favourites").child(recipeId).setValue(recipeObj)
                .addOnSuccessListener {
                    favourited = true
                    favouriteButton.setImageResource(R.drawable.ic_star_fill)
                    favSound.start();
                    Toast.makeText(baseContext,R.string.toast_recipe_favourite, Toast.LENGTH_SHORT).show();
                }
                .addOnFailureListener{
                    Log.d("msg", "FAILED IN HEREEE");
                }
        }
    }

    override fun onDoubleTap(p0: MotionEvent?): Boolean {
        return true;
    }


    override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
       //Toast.makeText(this,"DOUBLE 8 TAPPED", Toast.LENGTH_SHORT).show();

        return true;
    }

    override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
      //  Toast.makeText(this,"SINGLE TAPPED", Toast.LENGTH_SHORT).show();
        return true;
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gd.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onShowPress(p0: MotionEvent?) {
        //Toast.makeText(this,"PRESSED", Toast.LENGTH_SHORT).show();

    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        //Toast.makeText(this,"SINGLE TAP UP", Toast.LENGTH_SHORT).show();
        return true;
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        //Toast.makeText(this,"OONDOWN", Toast.LENGTH_SHORT).show();
        return true;
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        //Toast.makeText(this,"FLING", Toast.LENGTH_SHORT).show();
        val recipeObj = RecipeSearchItem(recipeId.toInt(), recipeName, recipeImage);
        favouriteRecipe(recipeObj)
        return true;
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
       // Toast.makeText(this,"SCROLL", Toast.LENGTH_SHORT).show();
        return true;
    }

    override fun onLongPress(p0: MotionEvent?) {
      //  Toast.makeText(this,"LONG TAPPED", Toast.LENGTH_SHORT).show();

    }


}