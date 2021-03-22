package com.example.mobile_apps_assignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShoppingListsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShoppingListsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var database: DatabaseReference
    private lateinit var shoppingListRecyclerView: RecyclerView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_shopping_lists, container, false)

        shoppingListRecyclerView= view.findViewById(R.id.shoppingListFragmentList);

        val currentUser = Firebase.auth.currentUser;
        database = Firebase.database.reference.child("users").child(currentUser!!.uid).child("shoppingLists");

        val addListButton: FloatingActionButton = view.findViewById(R.id.shoppingListFragmentAddButton);
        addListButton.setOnClickListener{
            val intent = Intent(view.context, CreateShoppingListScreen::class.java);
            startActivity(intent);
        }

        val list: MutableList<ShoppingList> = mutableListOf();

        fun updateListUI(){
            requireActivity().runOnUiThread {
                val shoppingListDataAdapter = ShoppingListAdapter(list);
                shoppingListRecyclerView.adapter = shoppingListDataAdapter;
                shoppingListRecyclerView.layoutManager = LinearLayoutManager(context);
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

        return view;
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(context, "ACTIVITY PAUSED", Toast.LENGTH_SHORT).show();
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShoppingListsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShoppingListsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}