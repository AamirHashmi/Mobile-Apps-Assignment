
package com.example.mobile_apps_assignment

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID


class CreateShoppingListScreen : AppCompatActivity() {

    private lateinit var shoppingListTitle: EditText;
    private lateinit var ingredientList: MutableList<String>;
    private lateinit var ingredientsRecyclerView: RecyclerView;
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shopping_list_screen)

        shoppingListTitle = findViewById(R.id.ShoppingListTitleTextInput);
        ingredientsRecyclerView = findViewById(R.id.IngredientsScrollView);
        ingredientList = mutableListOf<String>()

        database = Firebase.database.reference

        val currentUser = Firebase.auth.currentUser;

        val addIngredientButton: Button = findViewById(R.id.AddIngredientToListButton);
        addIngredientButton.setOnClickListener{
            val intent = Intent(this, IngredientSearchScreen::class.java);
            startActivityForResult(intent, 619);

        }


        val createButton:Button = findViewById(R.id.CreateShoppingListButton);
        createButton.setOnClickListener{
            val shoppingListName:String = shoppingListTitle.text.toString().trim();
            val uuid: UUID = UUID.randomUUID();

            val shopListObj: ShoppingList = ShoppingList(uuid.toString(), shoppingListName, ingredientList);

            database!!.child("users").child(currentUser!!.uid).child("shoppingLists").child(uuid.toString()).setValue(shopListObj)
                .addOnSuccessListener {  }
                .addOnFailureListener{
                    Log.d("msg", "FAILED IN HEREEE");
                }

            Log.d("msg", "WORKING IN HEREEE");
            Toast.makeText(this, "IN HERE", Toast.LENGTH_SHORT);

            finish();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 619) {
            if (resultCode == Activity.RESULT_OK) {
                val result: String = data?.getStringExtra("ingredient").toString();
                Toast.makeText(this, result, Toast.LENGTH_SHORT);
                println(result);
                ingredientList.add(result);
                val ingredientsListDataAdapter = CreateShoppingListAdapter(ingredientList);

                runOnUiThread {
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