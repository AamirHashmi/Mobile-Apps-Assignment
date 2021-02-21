package com.example.mobile_apps_assignment

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class RecipeDetailScreen : AppCompatActivity() {
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

        //setting data to view components
        recipeTitleTextView.text=recipeName;
        Picasso.get().load(recipeImage).into(recipeImageView);
    }
}