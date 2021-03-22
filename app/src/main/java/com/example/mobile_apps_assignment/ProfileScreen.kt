package com.example.mobile_apps_assignment

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_screen.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class ProfileScreen : AppCompatActivity() {


    private lateinit var favouritesListRecyclerView: RecyclerView;
    private lateinit var database: DatabaseReference
    private val storage = FirebaseStorage.getInstance();
    private lateinit var storageRef: StorageReference;

    val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_screen)

        val currentUser = Firebase.auth.currentUser;
        //reference to firebase storage
//        val storage = FirebaseStorage.getInstance();
        storageRef =  storage.reference.child("images/" + currentUser!!.uid + ".jpg");

        val navImage = storageRef.downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'
//            Toast.makeText(this, "succesuful", Toast.LENGTH_SHORT).show();
            Picasso.get().load(it).into(profilePictureImageView);
        }.addOnFailureListener {
            // Handle any errors
        }

        val userName = if(intent.hasExtra("username")) intent.getStringExtra("username") else " ";

        val profileImageView: ImageView = findViewById(R.id.profilePictureImageView);
        profileImageView.setImageResource(R.mipmap.ic_launcher_round);
        val profileNameTextView: TextView = findViewById(R.id.profileNameTextView);
        val userNameTextView: TextView = findViewById(R.id.profileUserName);

        favouritesListRecyclerView = findViewById(R.id.profileFavouritesRecipeList);

        val favouritesList: MutableList<RecipeSearchItem> = mutableListOf();



        profileNameTextView.text = currentUser!!.email;
        userNameTextView.text = userName;
        profileImageView.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }
        //Picasso.get().load(currentUser.photoUrl).into(profileImageView);


        database = Firebase.database.reference.child("users").child(currentUser!!.uid).child("favourites");

        fun updateListUI(){
            runOnUiThread {
                val favouritesListDataAdapter = SearchAdapter(favouritesList);
                favouritesListRecyclerView.adapter = favouritesListDataAdapter;
                favouritesListRecyclerView.layoutManager = LinearLayoutManager(this);
            }
        }

        val favouritesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                favouritesList.clear()
                for (snap in dataSnapshot.getChildren()) {
                    val favourite = RecipeSearchItem(snap.getValue(RecipeSearchItem::class.java)!!.id, snap.getValue(RecipeSearchItem::class.java)!!.title, snap.getValue(RecipeSearchItem::class.java)!!.image);

                    favouritesList.add(favourite)
                    println("YO")

                }
                updateListUI();

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("msg", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(favouritesListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            val imageUri = data!!.data;
            val inputStream: InputStream;

            try{
                inputStream = applicationContext!!.contentResolver!!.openInputStream(imageUri!!)!!;
                val imageBitmap = BitmapFactory.decodeStream(inputStream);
                profilePictureImageView.setImageBitmap(imageBitmap);

                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = storageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Toast.makeText(baseContext, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    Toast.makeText(baseContext, "Successfully Uploaded profile image", Toast.LENGTH_SHORT).show();
                }

            }catch (e: FileNotFoundException){
                Toast.makeText(baseContext, "Failed to read image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}