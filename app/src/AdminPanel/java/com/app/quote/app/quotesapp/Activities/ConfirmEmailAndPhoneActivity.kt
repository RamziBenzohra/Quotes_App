package com.app.quote.app.quotesapp.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.app.quote.app.quotesapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.AdminPanel.activity_confirm_email_and_phone.*


class ConfirmEmailAndPhoneActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_email_and_phone)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        try{

            val email=intent?.extras?.getString("email")

            etd_conf_email.text = email
            btn_conf_email.setOnClickListener {
                verification_progress.visibility=View.VISIBLE
                btn_conf_email.visibility=View.INVISIBLE
                SendVerificationEmail()
            }

        }catch (exeption:Exception){
            Toast.makeText(this,exeption.toString()+"cause"+exeption.message+"/"+exeption.stackTrace+"/"+exeption.cause,Toast.LENGTH_LONG).show()
        }

    }

    private fun SendVerificationEmail() {
        mAuth.currentUser!!.sendEmailVerification().addOnSuccessListener {
            verification_progress.visibility=View.INVISIBLE
            btn_conf_email.visibility=View.VISIBLE
            Toast.makeText(this@ConfirmEmailAndPhoneActivity,"تم ارسال رابط التاكيد .تفحص بريدك الالكتروني",Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this@ConfirmEmailAndPhoneActivity,it.message,Toast.LENGTH_LONG).show()
            verification_progress.visibility=View.INVISIBLE
            btn_conf_email.visibility=View.VISIBLE
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }
}
