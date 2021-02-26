package com.example.mobile_apps_assignment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class IngredientSearchAdapter(private val dataSet: List<IngredientSearchItem>) :
    RecyclerView.Adapter<IngredientSearchAdapter.ViewHolder>()  {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val searchItemNameTextView: TextView = view.findViewById(R.id.IngredientSearchItemTextView)
        val searchItemImageView: ImageView = view.findViewById(R.id.IngredientSearchItemImageView)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.ingredient_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val currentItem = dataSet[position];
        // Get element from your dataset at this position and replace the
        // contents of the view with that element


        viewHolder.searchItemNameTextView.text = currentItem.name;

        //using picasso to load image url
        Picasso.get().load("https://spoonacular.com/cdn/ingredients_250x250/" + currentItem.image).into(viewHolder.searchItemImageView);


        viewHolder.itemView.setOnClickListener{
            Toast.makeText(viewHolder.itemView.context, viewHolder.searchItemNameTextView.text.toString() + " clicked" , Toast.LENGTH_SHORT).show()

            val ingredientName = viewHolder.searchItemNameTextView.text.toString().trim();
           // val recipeId = currentItem.id;
            val ingredientImage = "https://spoonacular.com/cdn/ingredients_250x250/" + currentItem.image;

            val intent = Intent(viewHolder.itemView.context, IngredientDetailScreen::class.java)

            // passing parameters to recipe detail page

            intent.putExtra("IngredientName", ingredientName);
            intent.putExtra("IngredientImage", ingredientImage);
           // viewHolder.itemView.context.startActivity(intent);
            val o: Activity = viewHolder.itemView.context as Activity;
            o.startActivityForResult(intent, 223);

            //viewHolder.itemView.context.startActivityFor
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size



}


