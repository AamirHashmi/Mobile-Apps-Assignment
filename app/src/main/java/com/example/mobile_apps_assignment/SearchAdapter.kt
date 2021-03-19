package com.example.mobile_apps_assignment

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.security.AccessController.getContext

class SearchAdapter(private val dataSet: List<RecipeSearchItem>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>()  {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

    val searchItemNameTextView: TextView = view.findViewById(R.id.SearchItemNameTextView)
    val searchItemImageView: ImageView = view.findViewById(R.id.SearchItemImageView)

}

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.search_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val currentItem = dataSet[position];
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.searchItemNameTextView.text = currentItem.title;

        var imageUrl: String = currentItem!!.image!!;
        //using picasso to load image url
        if(imageUrl.contains("https://spoonacular.com/recipeImages/")){
            imageUrl = imageUrl.replace("https://spoonacular.com/recipeImages/", "");
        }
        Picasso.get().load("https://spoonacular.com/recipeImages/" + imageUrl).into(viewHolder.searchItemImageView);


        viewHolder.itemView.setOnClickListener{
            //Toast.makeText(viewHolder.itemView.context, viewHolder.searchItemNameTextView.text.toString() + " clicked" , Toast.LENGTH_SHORT).show()

            val recipeName = viewHolder.searchItemNameTextView.text.toString().trim();
            val recipeId = currentItem.id;


            val intent = Intent(viewHolder.itemView.context, RecipeDetailScreen::class.java)

            // passing parameters to recipe detail page
            intent.putExtra("RecipeId", recipeId);
            intent.putExtra("RecipeName", recipeName);
            intent.putExtra("RecipeImage", imageUrl);
            viewHolder.itemView.context.startActivity(intent);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
