package com.example.mobile_apps_assignment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Camera
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.nio.channels.AsynchronousFileChannel.open
import java.text.SimpleDateFormat
import java.util.*

class FoodAnalysisScreen : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 324;
    private lateinit var foodImageView: ImageView;
    lateinit var currentPhotoPath: String;
    private val client = OkHttpClient()

    private lateinit var currentPhoto: Bitmap;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_analysis_screen)

        val openCameraButton: Button = findViewById(R.id.openCameraButton);
        openCameraButton.setOnClickListener{
            dispatchTakePictureIntent();
        }

        val analyseButton:Button = findViewById(R.id.SendPictureToApi);
        analyseButton.setOnClickListener{
            sendPicture();
        }

        foodImageView = findViewById(R.id.foodAnalysisScreenImageView);
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
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
                            this,
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            foodImageView.setImageBitmap(imageBitmap)
            currentPhoto = imageBitmap;
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    fun sendPicture() {
        val mediaType = "multipart/form-data; boundary=---011000010111000001101001".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, "-----011000010111000001101001\rContent-Disposition: form-data; name=\"file\"\r\r\r-----011000010111000001101001--\r\r")
        val request = Request.Builder()
                .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/food/images/classify")
                .post(body)
                .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
                .addHeader("x-rapidapi-key", "4c61ca64ffmshb84f7b0b1ac2333p1bfd52jsn9b60ed97c505")
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .build()


//        val mediaType = "image/*jpg".toMediaTypeOrNull()
//        val image = RequestBody.create(mediaType, currentPhotoPath);
//        val body = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("image", currentPhotoPath, image).build();
//
//        val newbody = RequestBody.create(mediaType, "-----011000010111000001101001\nContent-Disposition: form-data; name=\"currentPhotoPath\"\n \n \n-----011000010111000001101001--\n\n")
//
//        val request = Request.Builder()
//                .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/food/images/classify")
//                .post(newbody)
//                .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
//                .addHeader("x-rapidapi-key", "4c61ca64ffmshb84f7b0b1ac2333p1bfd52jsn9b60ed97c505")
//                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
//                .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response") //fix this bad response
                    val body = response.body!!.string();

                }
            }
        })
    }
}