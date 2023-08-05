package com.app.quote.app.quotesapp.Client.Activities

import com.app.quote.app.quotesapp.Client.Fragments.*
import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*

import com.app.quote.app.quotesapp.Client.adapters.FragmentsAdapter
import com.app.quote.app.quotesapp.Client.models.Qoutes_firebase

import com.app.quote.app.quotesapp.Client.models.UserToken
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList
import android.widget.PopupWindow
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.support.design.widget.*
import com.app.quote.app.quotesapp.Client.Fragments.SettingsFragment

import com.app.quote.app.quotesapp.R
import kotlinx.android.synthetic.Client.activity_main.*


class MainActivity : AppCompatActivity(){

    private val intent_request_code=2
    private val mStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val mfirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var listener:ListenerRegistration
    private lateinit var VpagerAdapter: FragmentPagerAdapter

    private lateinit var UserInfo: UserToken

    @SuppressLint("RestrictedApi")
    private var Token:String?=null
    private lateinit var pw: PopupWindow
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //try {

            setSupportActionBar(toolbar)
            supportActionBar!!.title = ""
       // mfirestore.firestoreSettings.areTimestampsInSnapshotsEnabled()
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                if (it.exception!=null){
                    return@addOnCompleteListener
                }else{
                    Token = it.result!!.token

                    if(Token!=null){
                        SendToFirebase(Token!!)
                    }
                    Log.d("Token saved",Token)
                    Add_Cheched_Listener{
                        when(it){
                            true->{
                                subscription_fab.setImageResource(R.drawable.ic_notifications_none_black_24dp)
                            }
                            false->{
                                subscription_fab.setImageResource(R.drawable.ic_notifications_off_black_24dp)
                            }
                        }
                    }
                }
            }

            subscription_fab.SetUpfabs()
            val frList = ArrayList<Fragment>()
            frList.add(PersonFragment())
            frList.add(FavFragment())
            frList.add(GalleryFragment())
            frList.add(SettingsFragment())
            frList.add(InfoFragment())
            tablayout.setupWithViewPager(vpager)

            VpagerAdapter = FragmentsAdapter(supportFragmentManager, frList)
            vpager.adapter = VpagerAdapter
        vpager.offscreenPageLimit = 4
            tablayout.apply {
                this.getTabAt(0)?.setIcon(R.drawable.ic_person_outline_black_24dp)?.setText("")
                this.getTabAt(1)?.setIcon(R.drawable.ic_favorite_border_black_24dp)?.setText("")
                this.getTabAt(2)?.setIcon(R.drawable.ic_photo_album_black_24dp)?.setText("")
                this.getTabAt(3)?.setIcon(R.drawable.ic_settings_black_24dp)?.setText("")
                this.getTabAt(4)?.setIcon(R.drawable.ic_info_outline_black_24dp)?.setText("")

                this.tabMode = TabLayout.MODE_FIXED

            }
            //Log.d("view pager initialazed","view pager")




       // }catch (ex1:Exception){
          //  Toast.makeText(this,ex1.toString(),Toast.LENGTH_LONG).show()
        //}
    }

    private fun SendToFirebase(token: String) {
        mfirestore.collection("UsersTokens").document(token).get().addOnSuccessListener {
            if (it.exists()){
                return@addOnSuccessListener
            }
        }
        mfirestore.collection("UsersTokens")
            .document(token)
            .set(UserToken(token,true))
            .addOnCompleteListener {
                if (it.exception!=null){
                    Log.d("Token Registration", "Token Registred from main Activity")
                    return@addOnCompleteListener
                }else{
                    applicationContext.sendBroadcast(Intent("TokenBroadcast"))
                    Log.d(ContentValues.TAG, "Token Registred" + Token)
                }
            }
    }



    private fun Add_Cheched_Listener(getitems:(Boolean)->Unit):ListenerRegistration {
        val query=mfirestore
            .collection("UsersTokens").document(Token!!)

        return query.addSnapshotListener{ documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if (firebaseFirestoreException!=null){
                return@addSnapshotListener
            }
            getitems(documentSnapshot!!.toObject(UserToken::class.java)!!.subscription)
        }
    }
    fun GetUser(Token:String,OnComplete: (User: UserToken) -> Unit){
        mfirestore.collection("UsersTokens").document(Token).get().addOnCompleteListener {
            if (it.exception!=null){
                return@addOnCompleteListener
            }
            if (it.isSuccessful){
                UserInfo=it.result!!.toObject(UserToken::class.java)!!
                OnComplete(UserInfo)
            }
        }
    }

    private fun CircleImageView.SetUpProfile(){
        this.setOnClickListener {

        }
    }
    private fun FloatingActionButton.SetUpfabs(){
        this.setOnClickListener {
            try {
                when(this.id){

                    R.id.subscription_fab->{
                        GetUser(Token!!){
                            when(UserInfo.subscription){
                                true->{
                                    Log.d("subscribtion",UserInfo.subscription.toString())
                                    mfirestore.collection("UsersTokens").document(Token!!).update("subscription",false).addOnCompleteListener {
                                        if (it.exception!=null){
                                            toast(it.exception.toString())
                                            return@addOnCompleteListener
                                        }else{

                                            Snackbar.make(this,"Notification Off",Snackbar.LENGTH_LONG).show()
                                            this.setImageResource(R.drawable.ic_notifications_off_black_24dp)

                                        }
                                    }
                                    FirebaseMessaging.getInstance().subscribeToTopic("Notification").addOnSuccessListener {

                                    }.addOnFailureListener {

                                    }
                                }
                                false->{
                                    mfirestore.collection("UsersTokens").document(Token!!).update("subscription",true).addOnCompleteListener {
                                        if (it.exception!=null){
                                            toast(it.exception.toString())
                                            return@addOnCompleteListener
                                        }else{

                                            this.setImageResource(R.drawable.ic_notifications_none_black_24dp)
                                            Snackbar.make(this,"Notification On",Snackbar.LENGTH_LONG).show()
                                            Log.d("Notification On",UserInfo.subscription.toString())
                                        }
                                    }
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Notification").addOnSuccessListener {

                                    }.addOnFailureListener {

                                    }
                                }
                            }
                        }
                        //toast("clicked")

                    }
                }

            }catch (ex1:Exception){
                Toast.makeText(this@MainActivity,ex1.toString(),Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun toast(text: String) {
        Toast.makeText(this,text,Toast.LENGTH_LONG).show()
    }



}
