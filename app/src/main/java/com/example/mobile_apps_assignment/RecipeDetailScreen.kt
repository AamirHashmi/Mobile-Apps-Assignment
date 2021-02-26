package com.example.mobile_apps_assignment

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

class RecipeDetailScreen : AppCompatActivity() {

    private val client = OkHttpClient();
    private lateinit var instructionsTextView: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail_screen)

        //getting recipe parameters from clicked item in the recycler view
        val recipeId:String = intent.getIntExtra("RecipeId", -1).toString();
        val recipeName:String = intent.getStringExtra("RecipeName").toString()
        val recipeImage:String = intent.getStringExtra("RecipeImage").toString()

        //instantiating view components
        val recipeTitleTextView: TextView = findViewById(R.id.recipeDetailScreenTitleTextView);
        val recipeImageView: ImageView = findViewById(R.id.recipeDetailScreenImageView);
        instructionsTextView = findViewById(R.id.instructionsScrollViewText);

        //setting data to view components
        recipeTitleTextView.text=recipeName;
        Picasso.get().load(recipeImage).into(recipeImageView);

        getRecipeInformation(recipeId);

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