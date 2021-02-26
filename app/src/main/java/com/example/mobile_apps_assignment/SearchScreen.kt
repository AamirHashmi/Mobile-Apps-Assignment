package com.example.mobile_apps_assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class SearchScreen : AppCompatActivity() {

    private val client = OkHttpClient();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_screen)

        val searchBar: EditText = findViewById(R.id.SearchScreenSearchBar);
        val searchButton: Button = findViewById(R.id.SearchScreenSearchButton);

      //  val searchAdapter = SearchAdapter(tempList);
        val searchListRecyclerView: RecyclerView = findViewById(R.id.SearchList);
       // searchListRecyclerView.adapter = searchAdapter;
        searchListRecyclerView.layoutManager = LinearLayoutManager(this);

        //dummy data to check if recycler view was working
       /* val recipe1 = RecipeSearchItem("Chicken curry", "");
        val recipe2 = RecipeSearchItem("Enchiladas", "");
        val recipe3 = RecipeSearchItem("Ramen", "");
        val recipe4 = RecipeSearchItem("Waffles", "");
        val recipe5 = RecipeSearchItem("Salmon", "");
        val recipe6 = RecipeSearchItem("Pizza", "");

        val tempList: MutableList<RecipeSearchItem> = ArrayList();
        tempList.add(recipe1);
        tempList.add(recipe2);
        tempList.add(recipe3);
        tempList.add(recipe4);
        tempList.add(recipe5);
        tempList.add(recipe6);

        fun filterList(query: String){
            //  val filteredList: MutableList<RecipeSearchItem> = ArrayList();
            val filteredList = tempList.filter { it.title.toLowerCase().contains(query.toLowerCase())}
            val filteredSearchAdapter = SearchAdapter(filteredList);
            searchListRecyclerView.adapter = filteredSearchAdapter
            searchListRecyclerView.layoutManager = LinearLayoutManager(this);

            Log.d("msg", filteredList.toString());

        }
*/

        searchBar.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //filterList(s.toString());
            }
        })

        fun getSearchData(searchQuery: String) {
            val request = Request.Builder()
                    .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=$searchQuery&number=10")
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
                        val searchListData = gson.fromJson(body, RecipeList::class.java)
                        val searchListDataAdapter = SearchAdapter(searchListData.results);

                        runOnUiThread {
                            searchListRecyclerView.adapter = searchListDataAdapter

                        }

                    }
                }
            })
        }

        searchButton.setOnClickListener{
            Log.d("msg", "getSearchData()");
            getSearchData(searchBar.text.toString().trim());
            searchListRecyclerView.layoutManager = LinearLayoutManager(this);
        }



    }

}