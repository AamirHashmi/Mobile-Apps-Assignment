package com.example.mobile_apps_assignment

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class NotificationReciever : BroadcastReceiver() {

    private lateinit var recipeId:String;
    private lateinit var recipeName:String;
    private lateinit var recipeImage:String;

    private val client = OkHttpClient();

    override fun onReceive(context: Context?, i: Intent?) {

        fun sendNotification(){
            val intent = Intent(context, RecipeDetailScreen::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            intent.putExtra("RecipeId", recipeId);
            intent.putExtra("RecipeName", recipeName);
            intent.putExtra("RecipeImage", recipeImage);

            val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(intent)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            var builder = NotificationCompat.Builder(context!!, "CHANNEL_ID")
                    .setSmallIcon(R.drawable.googleg_standard_color_18)
                    .setContentTitle("Try this Recipe")
                    .setContentText(recipeName)
                    .setContentIntent(resultPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }
        }

        val request = Request.Builder()
                .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/random?number=1")
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
                    val searchListData = gson.fromJson(body, RecipeNotificationModel::class.java)
                    recipeId = searchListData.recipes[0].id.toString();
                    recipeName = searchListData.recipes[0].title.toString();
                    recipeImage = searchListData.recipes[0].image.toString();

                    sendNotification();
                }
            }
        })


    }


}