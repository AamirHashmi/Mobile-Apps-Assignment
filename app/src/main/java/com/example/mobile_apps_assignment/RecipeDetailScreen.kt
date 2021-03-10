package com.example.mobile_apps_assignment

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

class RecipeDetailScreen : AppCompatActivity() {

    private val client = OkHttpClient();
    private lateinit var instructionsTextView: TextView;
    private lateinit var database: DatabaseReference
    private var favourited: Boolean = false;
    private lateinit var recipeId: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail_screen)

        //getting recipe parameters from clicked item in the recycler view
        recipeId = intent.getIntExtra("RecipeId", -1).toString();
        var recipeName:String = "";
        var recipeImage:String = "";

        if(intent.hasExtra("RecipeName")){
             recipeName = intent.getStringExtra("RecipeName").toString()
             recipeImage = intent.getStringExtra("RecipeImage").toString()
        }

        //instantiating view components
        val recipeTitleTextView: TextView = findViewById(R.id.recipeDetailScreenTitleTextView);
        val recipeImageView: ImageView = findViewById(R.id.recipeDetailScreenImageView);
        instructionsTextView = findViewById(R.id.instructionsScrollViewText);

        val shareDataButton: Button = findViewById(R.id.shareDataButton);
        val favouriteButton: FloatingActionButton = findViewById(R.id.recipeDetailScreenFavouriteButton);

        val currentUser = Firebase.auth.currentUser;
        database = Firebase.database.reference;



        val favListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val recipe = dataSnapshot.key; //<RecipeSearchItem>()

                if(recipe != null){
                    favourited = true;
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child(currentUser!!.uid!!).child("favourites").child(recipeId).addValueEventListener(favListener)


        favouriteButton.setOnClickListener{

            val recipeObj = RecipeSearchItem(recipeId.toInt(), recipeName, recipeImage);

            if(favourited){
                database!!.child("users").child(currentUser!!.uid).child("favourites").child(recipeId).removeValue()
                    .addOnSuccessListener { favourited = false }
                    .addOnFailureListener{
                        Log.d("msg", "FAILED IN HEREEE");
                    }
            }else{
                database!!.child("users").child(currentUser!!.uid).child("favourites").child(recipeId).setValue(recipeObj)
                    .addOnSuccessListener { favourited = true }
                    .addOnFailureListener{
                        Log.d("msg", "FAILED IN HEREEE");
                    }
            }

        }

        //setting data to view components
        recipeTitleTextView.text=recipeName;

        if(recipeImage !=""){
            Picasso.get().load(recipeImage).into(recipeImageView);
        }


        getRecipeInformation(recipeId);

        //Sharing recipes on social media using implicit intents
        shareDataButton.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "I just made " + recipeName + " using the recipe app.")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share with: ")
            startActivity(shareIntent)
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




}