package com.example.mobile_apps_assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ShoppingListDetailScreen : AppCompatActivity() {

    private lateinit var ingredientsRecyclerView: RecyclerView;
    private lateinit var database: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_detail_page)

        ingredientsRecyclerView = findViewById(R.id.shoppingListDetailScreenIngredientRecyclerView);
        val shoppingListTitleTextView: TextView = findViewById(R.id.shoppingListDetailScreenTitleTextView);
        val deleteButton: FloatingActionButton = findViewById(R.id.deleteShoppingListButton);
        val editButton: FloatingActionButton = findViewById(R.id.editShoppingListButton);

        database = Firebase.database.reference
        val userId = Firebase.auth.currentUser!!.uid;

        val shoppingList:ShoppingList = intent.getParcelableExtra("shoppingList");
        shoppingListTitleTextView.text = shoppingList.name.toString();

        val ingredientsListDataAdapter =
            shoppingList.ingredients?.let { CreateShoppingListAdapter(it) };


        ingredientsRecyclerView.adapter = ingredientsListDataAdapter;
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this);

        deleteButton.setOnClickListener{
            deleteShoppingList(userId, shoppingList!!.id!!);
            finish();
        }

        println("UODWP")
    }

    fun deleteShoppingList (userId:String, shoppingListId:String){

          database!!.child("users").child(userId).child("shoppingLists").child(shoppingListId).removeValue()
                .addOnSuccessListener {  }
                .addOnFailureListener{
                    Log.d("msg", "FAILED IN HEREEE");
                }

    }
}