package com.example.mobile_apps_assignment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_screen.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FoodAnalysisFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoodAnalysisFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val storage = FirebaseStorage.getInstance();
    private lateinit var storageRef: StorageReference;

    private val REQUEST_IMAGE_CAPTURE = 324;
    private lateinit var currentPhotoPath: String;
    private val client = OkHttpClient()
    private val Fragment.packageManager get() = activity?.packageManager
    private lateinit var currentPhoto: Bitmap;
    private lateinit var foodImageView: ImageView;

    private lateinit var toSendImageUrl:String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val currentUser = Firebase.auth;
        storageRef =  storage.reference.child("analysis/" + currentUser!!.uid + ".jpg");

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_food_analysis, container, false);
        val openCameraButton: Button = view.findViewById(R.id.analysisFragmentOpenCameraButton);
        val analyseButton: Button = view.findViewById(R.id.foodAnalysisFragmentAnalyseButton);
        val uploadButton: Button = view.findViewById(R.id.foodAnalysisUploadButton);
        foodImageView = view.findViewById(R.id.foodAnalysisFragmentImageView);
        openCameraButton.setOnClickListener{
            dispatchTakePictureIntent();
           // Toast.makeText(view.context, "IT WORKED", Toast.LENGTH_SHORT);
            //Log.d("msg","YOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
        }

        uploadButton.setOnClickListener{
            if(foodImageView.drawable != null){
                uploadImageToFB(foodImageView.drawable.toBitmap());
            }

        }

        analyseButton.setOnClickListener{
            //add validation
            analyseButton.isEnabled = false;
            val navImage = storageRef.downloadUrl.addOnSuccessListener {
                // Got the download URL for 'users/me/profile.png'
                sendPicture(it.toString());
                analyseButton.isEnabled = true;
            }.addOnFailureListener {
                // Handle any errors
                analyseButton.isEnabled = true;
            }
        }

        return view;

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    // ...
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.mobile_apps_assignment",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.ACTION_IMAGE_CAPTURE, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            foodImageView.setImageBitmap(imageBitmap)
            currentPhoto = imageBitmap;
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun uploadImageToFB(imageBitmap:Bitmap){
        try{

            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = storageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
                Toast.makeText(requireView().context, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                Toast.makeText(requireView().context, "Successfully Uploaded profile image", Toast.LENGTH_SHORT).show();

            }

        }catch (e: FileNotFoundException){
            Toast.makeText(requireView().context, "Failed to read image", Toast.LENGTH_SHORT).show();
        }
    }

    fun sendPicture(imageUrl:String) {
        val request = Request.Builder()
            .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/food/images/analyze?imageUrl=${imageUrl}")
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
                    if (!response.isSuccessful) throw IOException("Unexpected code $response") //fix this bad response
                    val body = response.body!!.string();
                    println(body);

                }
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FoodAnalysisFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FoodAnalysisFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}