package com.example.mobile_apps_assignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso


class IngredientDetailScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_detail_screen)

        val ingredientName = intent.getStringExtra("IngredientName").toString()
        val ingredientImage = intent.getStringExtra("IngredientImage").toString()

        val ingredientTitle: TextView = findViewById(R.id.IngredientDetailScreenTitle);
        val ingredientImageView: ImageView = findViewById(R.id.IngredientDetailScreenImageView);
        val addIngredientButton: Button = findViewById(R.id.IngredientDetailScreenAddIngredientButton);

        ingredientTitle.text = ingredientName;
        Picasso.get().load(ingredientImage).into(ingredientImageView);

        addIngredientButton.setOnClickListener{
            val returnIntent = Intent()
            returnIntent.putExtra("ingredient", ingredientName)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

    }
}