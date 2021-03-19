package com.example.mobile_apps_assignment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class IngredientSearchScreen : AppCompatActivity() {

    private val client = OkHttpClient();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_search_screen)

        val searchBar: EditText = findViewById(R.id.IngredientSearchScreenSearchBar);
        val searchButton: ImageButton = findViewById(R.id.IngredientSearchScreenSearchButton);
        val searchListRecyclerView: RecyclerView = findViewById(R.id.IngredientSearchList);

        fun getSearchData(searchQuery: String) {
            val request = Request.Builder()
                .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/food/ingredients/autocomplete?query=$searchQuery&number=10")
                .get()
                .addHeader("x-rapidapi-key", "4c61ca64ffmshb84f7b0b1ac2333p1bfd52jsn9b60ed97c505")
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        val body = response.body!!.string();

                        val gson = GsonBuilder().create()

                       // val searchListData = gson.fromJson(body, IngredientList::class.java)
                        val searchListData = gson.fromJson(body, Array<IngredientSearchItem>::class.java).asList()
                        val searchListDataAdapter = IngredientSearchAdapter(searchListData);

                        runOnUiThread {
                            searchListRecyclerView.adapter = searchListDataAdapter

                        }

                    }
                }
            })
        }

        searchButton.setOnClickListener{
            getSearchData(searchBar.text.toString().trim());
            searchListRecyclerView.layoutManager = LinearLayoutManager(this);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 223) {
            if (resultCode == Activity.RESULT_OK) {
                val ingredient: String = data?.getStringExtra("ingredient").toString();
                val returnIntent = Intent()
                returnIntent.putExtra("ingredient", ingredient)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}