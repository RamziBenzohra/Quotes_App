package com.app.quote.app.quotesapp.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.app.quote.app.quotesapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.AdminPanel.activity_sign_in.*


class SignInActivity : AppCompatActivity(),TextWatcher {
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        etd_email_sign_in.addTextChangedListener(this@SignInActivity)
        etd_password_sign_in.addTextChangedListener(this@SignInActivity)
        btn_sign_in.setOnClickListener {
            sign_in_progress.visibility=View.VISIBLE
            btn_sign_in.visibility=View.INVISIBLE
            val email=etd_email_sign_in.text.toString().trim().toLowerCase()
            val password=etd_password_sign_in.text.toString().trim().toLowerCase()
            if (email.isEmpty()){
                etd_email_sign_in.error="Email Required"
                etd_email_sign_in.requestFocus()
                sign_in_progress.visibility=View.INVISIBLE
                btn_sign_in.visibility=View.VISIBLE
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etd_email_sign_in.error="Please Enter a Valid Email"
                etd_email_sign_in.requestFocus()
                sign_in_progress.visibility=View.INVISIBLE
                btn_sign_in.visibility=View.VISIBLE
                return@setOnClickListener
            }
            if (password.length < 6){
                etd_password_sign_in.error="6 chars required"
                etd_password_sign_in.requestFocus()
                sign_in_progress.visibility=View.INVISIBLE
                btn_sign_in.visibility=View.VISIBLE
                return@setOnClickListener
            }
            sign_in(email,password)
        }
    }
    private fun sign_in(email:String,password:String) {
        sign_in_progress.visibility=View.VISIBLE
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if (task.isSuccessful){

                sign_in_progress.visibility=View.INVISIBLE
                btn_sign_in.visibility=View.VISIBLE
                val intent= Intent(this@SignInActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }else{
                sign_in_progress.visibility=View.INVISIBLE
                btn_sign_in.visibility=View.VISIBLE
                Toast.makeText(this@SignInActivity,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btn_sign_in.isEnabled=etd_email_sign_in.text.trim().isNotEmpty()
                && etd_password_sign_in.text.trim().isNotEmpty()
                && etd_password_sign_in.text.length>6
    }
}
