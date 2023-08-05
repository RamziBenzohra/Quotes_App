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
import com.app.quote.app.quotesapp.models.User


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.AdminPanel.activity_sign_up.*


class SignUpActivity : AppCompatActivity() ,TextWatcher{
    private val mAuth:FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val mfirestore:FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val mStorage:FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val mCurrentUserRef:DocumentReference
    get() = mfirestore.document("Users/${mAuth.currentUser!!.uid.toString()}")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        etd_name_sign_up.addTextChangedListener(this@SignUpActivity)
        etd_email_sign_up.addTextChangedListener(this@SignUpActivity)
        etd_password_sign_up.addTextChangedListener(this@SignUpActivity)
        etd_password_confirm_sign_up.addTextChangedListener(this@SignUpActivity)
        btn_sign_up.setOnClickListener {
            val name=etd_name_sign_up.text.toString().trim().toLowerCase()
            val email=etd_email_sign_up.text.toString().trim().toLowerCase()
            val password=etd_password_sign_up.text.toString().trim().toLowerCase()
            if (name.isEmpty()){
                etd_name_sign_up.error="أدخل الاسم"
                etd_name_sign_up.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()){
                etd_email_sign_up.error="أدخل الاميل"
                etd_email_sign_up.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etd_email_sign_up.error="من فضلك ادخل الاميل صحيح"
                etd_email_sign_up.requestFocus()
                return@setOnClickListener
            }
            if (password.length <= 6){
                etd_password_sign_up.error="كلمة السر قصيرة "
                etd_password_sign_up.requestFocus()
                return@setOnClickListener
            }
            if (etd_password_confirm_sign_up.text.toString().trim() != etd_password_sign_up.text.toString().trim()){
                etd_password_confirm_sign_up.error=" تأكيد كلمة السر غير متطابق "
                etd_password_confirm_sign_up.requestFocus()
                return@setOnClickListener
            }else{
                CreateNewAccount(email, password,name)
            }
        }
    }
    private fun CreateNewAccount(email:String,password:String,name:String) {
        sign_up_prog.visibility= View.VISIBLE
        btn_sign_up.visibility=View.INVISIBLE
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if (task.isSuccessful){
                mCurrentUserRef.set(User(name, email, ""))
                    .addOnSuccessListener {
                        var request=UserProfileChangeRequest.Builder()
                        request.setDisplayName(name)
                        mAuth.currentUser!!.updateProfile(request.build())
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener {
                                Toast.makeText(this@SignUpActivity,it.toString(),Toast.LENGTH_LONG).show()
                            }

                        val intent= Intent(this@SignUpActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"فشل في فتح حساب جديد",Toast.LENGTH_LONG).show()

                    }
                sign_up_prog.visibility=View.INVISIBLE
                btn_sign_up.visibility= View.VISIBLE

            }else {
                sign_up_prog.visibility=View.INVISIBLE
                btn_sign_up.visibility= View.VISIBLE
                Toast.makeText(this,"فشل في فتح حساب جديد",Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btn_sign_up.isEnabled=etd_name_sign_up.text.isNotEmpty()
                && etd_email_sign_up.text.isNotEmpty()
                && etd_password_sign_up.text.trim().isNotEmpty() && etd_password_sign_up.text.length>=6
                && etd_password_confirm_sign_up.text.trim().isNotEmpty() && etd_password_confirm_sign_up.text.length>=6
                //&& etd_password_confirm_sign_up.text.trim()== etd_password_sign_up.text.trim()
    }
}
