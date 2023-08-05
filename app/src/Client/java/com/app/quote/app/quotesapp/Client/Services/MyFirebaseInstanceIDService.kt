package com.app.quote.app.quotesapp.Client.Services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import android.util.Log


import com.app.quote.app.quotesapp.Client.models.UserToken
import com.app.quote.app.quotesapp.R

import com.app.quote.app.quotesapp.Client.Activities.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class MyFirebaseInstanceIDService : FirebaseMessagingService (){
    private val mStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }


    private val mfirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from!!)
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
            SendNotification(remoteMessage)
        }
    }
    private fun SendNotification(remoteMessage: RemoteMessage){
        var notification_intent=Intent(this, MainActivity::class.java)
        var contentIntent=PendingIntent.getActivity(this,0,notification_intent,0)
            var notification=
                NotificationCompat.Builder(this,remoteMessage.messageId!!)
            notification.setContentTitle(remoteMessage.notification!!.title)
            notification.setContentText(remoteMessage.notification!!.body)
            notification.setStyle(NotificationCompat.BigTextStyle())
                    notification.color=Color.parseColor("#2f2f2f")
        notification.setSmallIcon(R.drawable.ic_favorite_black_24dp)
        notification.setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.image))
            notification.setAutoCancel(true)
                notification.setPriority(NotificationCompat.PRIORITY_HIGH)
        notification.setCategory(NotificationCompat.CATEGORY_EVENT)
        notification.setContentIntent(contentIntent)
            var notificationmanager=getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager

            notificationmanager.notify(getID(),notification.build())
    }

    private fun getID():Int{
        return Random().nextInt(100)
    }
    override fun onNewToken(token: String?) {
        Log.d(TAG, "Token" + token)
        saveToken(token!!)
    }

    private fun saveToken(Token:String) {
        mfirestore.collection("UsersTokens").document(Token).get().addOnSuccessListener {
            if (it.exists()){
                return@addOnSuccessListener
            }
        }
        mfirestore.collection("UsersTokens")
            .document(Token)
            .set(UserToken(Token,true))
            .addOnCompleteListener {
                if (it.exception!=null){
                    return@addOnCompleteListener
                }else{

                    Log.d(TAG, "Token Registred" + Token)
                }
            }
    }

}
