package com.example.mobile_apps_assignment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ShoppingListDetailScreen : AppCompatActivity() {

    private lateinit var ingredientsRecyclerView: RecyclerView;
    private lateinit var shoppingListTitleTextView: TextView;
    private lateinit var database: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_detail_page)

        ingredientsRecyclerView = findViewById(R.id.shoppingListDetailScreenIngredientRecyclerView);
         shoppingListTitleTextView = findViewById(R.id.shoppingListDetailScreenTitleTextView);
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

        editButton.setOnClickListener{
            val intent = Intent(this, ShoppingListEditScreen::class.java);
            intent.putExtra("shoppingList", shoppingList);
            startActivityForResult(intent, 303);
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 303) {
            if (resultCode == Activity.RESULT_OK) {
                val result: ShoppingList = data!!.getParcelableExtra("newShoppingList")

                val ingredientsListDataAdapter = CreateShoppingListAdapter(result!!.ingredients!!);

                runOnUiThread {
                    shoppingListTitleTextView.text = result.name;
                    ingredientsRecyclerView.adapter = ingredientsListDataAdapter;
                    ingredientsRecyclerView.layoutManager = LinearLayoutManager(this);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}