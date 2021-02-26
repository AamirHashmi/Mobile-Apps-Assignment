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

class CreateShoppingListAdapter(private val dataSet: MutableList<String>) :
    RecyclerView.Adapter<CreateShoppingListAdapter.ViewHolder>()  {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val itemTextView: TextView = view.findViewById(R.id.createShoppingListItemTextView);


    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.create_shopping_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val currentItem = dataSet[position];
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.itemTextView.text = currentItem;



        viewHolder.itemView.setOnClickListener{
            Toast.makeText(viewHolder.itemView.context, viewHolder.itemTextView.text.toString() + " clicked" , Toast.LENGTH_SHORT).show()

            //val intent = Intent(viewHolder.itemView.context, RecipeDetailScreen::class.java)

            // passing parameters to recipe detail page

           // viewHolder.itemView.context.startActivity(intent);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
