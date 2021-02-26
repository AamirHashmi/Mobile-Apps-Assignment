package com.example.mobile_apps_assignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ShoppingListsScreen : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var shoppingListRecyclerView: RecyclerView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_lists_screen)

        shoppingListRecyclerView= findViewById(R.id.shoppingListScreenRecyclerView);

        val currentUser = Firebase.auth.currentUser;
        database = Firebase.database.reference.child("users").child(currentUser!!.uid).child("shoppingLists");

        val addListButton: FloatingActionButton = findViewById(R.id.addShoppingListButton);
        addListButton.setOnClickListener{
            val intent = Intent(this, CreateShoppingListScreen::class.java);
            startActivity(intent);
        }

        val list: MutableList<ShoppingList> = mutableListOf();

        fun updateListUI(){
            runOnUiThread {
                val shoppingListDataAdapter = ShoppingListAdapter(list);
                shoppingListRecyclerView.adapter = shoppingListDataAdapter;
                shoppingListRecyclerView.layoutManager = LinearLayoutManager(this);
            }
        }

        val shoppingListListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                list.clear()
                for (snap in dataSnapshot.getChildren()) {
                    val shoppingList = ShoppingList(snap.getValue(ShoppingList::class.java)!!.id, snap.getValue(ShoppingList::class.java)!!.name, snap.getValue(ShoppingList::class.java)!!.ingredients )

                    list.add(shoppingList)
                    println("YO")
                    // here you can access to name property like university.name

                }
                updateListUI();
                println("dnwo")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("msg", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(shoppingListListener)


    }

}