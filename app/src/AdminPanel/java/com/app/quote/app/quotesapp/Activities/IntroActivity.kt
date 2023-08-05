package com.app.quote.app.quotesapp.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.app.quote.app.quotesapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.AdminPanel.activity_intro.*


class IntroActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        FirebaseMessaging.getInstance().isAutoInitEnabled=true
        intro_sign_in.setOnClickListener {
            val intent= Intent(this@IntroActivity, SignInActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        intro_create_new.setOnClickListener {
            val intent= Intent(this@IntroActivity, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }
    override fun onStart() {
        super.onStart()
        //Toast.makeText(this, mAuth.currentUser!!.email.toString(),Toast.LENGTH_LONG).show()
        if(mAuth.currentUser?.uid !=null){
            val intent= Intent(this@IntroActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }
}
/*
*  <activity android:name=".Activities.ConfirmEmailAndPhoneActivity"></activity>


        <activity android:name=".Activities.ProfileActivity" />
        <activity android:name=".Activities.UpdateActivity" />

        <service android:name=".Services.MyFirebaseInstanceIDService">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3747911693538937~9532358321" />

        <!--
 Set custom default icon. This is used when no icon is set for incoming notification messages.
     See README(https://goo.gl/l4GJaQ) for more.
        -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/ic_favorite_black_24dp" />
        <!--
 Set color used with incoming notification messages. This is used when no color is set for the incoming
             notification message. See README(https://goo.gl/6BKBk7) for more.
        -->
        <activity android:name=".Activities.IntroActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".Activities.MainActivity" />
        <activity android:name=".Activities.SignUpActivity" />
        <activity android:name=".Activities.SignInActivity" />
        *
        * */