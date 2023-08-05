package com.app.quote.app.quotesapp.Activities


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.widget.Toast

import com.app.quote.app.quotesapp.models.Qoutes_firebase
import com.app.quote.app.quotesapp.R
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.AdminPanel.activity_update.*

import java.util.*

class UpdateActivity : AppCompatActivity() {
    private var Quote_Image_Uri :Uri?= null
    private val mStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val mfirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        setSupportActionBar(upd_toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var ID=intent?.extras?.getString("ID")
        var discription=intent?.extras?.getString("discription")
        var pic=intent?.extras?.getString("pic")
        var date=intent?.extras?.get("date") as Date
        etd_discription.setText(discription)
        Glide.with(this@UpdateActivity).load(mStorage.getReference(pic!!)).into(quotes)
        quotes.setOnClickListener {
            var intent= Intent().apply {
                type="image/*"
                action= Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES,arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(intent,"أختر صورة"),12)
        }
        dialog_add_fab.setUpFabs(ID!!,discription!!,pic,date)
        delete_quote.setUpFabs(ID,discription,pic,date)
        progressBar
    }
    @SuppressLint("RestrictedApi")
    private fun FloatingActionButton.setUpFabs(ID: String,
                                               olddisc: String,oldpic:String,date:Date
                                              ) {
        this.setOnClickListener {
            when(it.id){
                R.id.dialog_add_fab->{
                    if (etd_discription.text.isNullOrEmpty()){
                        etd_discription.error="الوصف لا يجب ان يكون فارغا"
                        return@setOnClickListener
                    }
                    it.visibility= View.INVISIBLE
                    progressBar.visibility= View.VISIBLE
                    delete_quote.isEnabled=false
                    if(Quote_Image_Uri==null){
                        UpdateQuot(Qoutes_firebase(0,ID,etd_discription.text.toString(),oldpic,date)){
                            it.visibility=View.VISIBLE
                            progressBar.visibility=View.INVISIBLE
                            delete_quote.isEnabled=true
                        }
                    }else{
                        UploadToFirebase(olddisc,etd_discription.text.toString()){path ->
                            UpdateQuot(Qoutes_firebase(0,ID,etd_discription.text.toString(),path, Timestamp.now().toDate())){
                                it.visibility=View.VISIBLE
                                progressBar.visibility=View.INVISIBLE
                                delete_quote.isEnabled=true
                            }
                        }
                    }
                }
                R.id.delete_quote->{
                    Quote_Image_Uri=null
                    Glide.with(this@UpdateActivity).load(mStorage.getReference(oldpic)).into(quotes)
                    etd_discription.text=null
                }
            }
        }
    }
    @SuppressLint("RestrictedApi")
    private fun UpdateQuot(qoutesFirebase: Qoutes_firebase, OnComplete:()->Unit) {
        try{
            mfirestore.collection("Quotes").document(qoutesFirebase.id).set(
                qoutesFirebase
            ).addOnSuccessListener {
                OnComplete()
            }.addOnFailureListener {
                Toast.makeText(this@UpdateActivity,"فشل الاتصال, الرجاء المحاولة مرة أخرى ",Toast.LENGTH_LONG).show()
                dialog_add_fab.visibility=View.VISIBLE
                progressBar.visibility=View.INVISIBLE
                delete_quote.isEnabled=true
            }
        }catch (ex:Exception){
            Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show()
        }
    }
    private fun UploadToFirebase(olddisc:String,disc:String,OnComplete:(path:String)->Unit) {
        try {
            var storage_ref =
                mStorage.getReference("QuotesPictures/${mAuth.currentUser!!.uid}/$disc")
            storage_ref.putFile(Quote_Image_Uri!!).addOnCompleteListener { task ->
                if (task.exception != null) {
                    Toast.makeText(
                        this@UpdateActivity,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }else if (task.isComplete) {
                    daleteOldPic(olddisc)
                    OnComplete(storage_ref.path)
                }
            }
        }catch (ex:Exception){
            Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show()
        }
    }
    private fun daleteOldPic(olddisc: String) {
        try {
            var storage_ref=mStorage.getReference("QuotesPictures/${mAuth.currentUser!!.uid}/$olddisc")
            storage_ref.delete().addOnFailureListener {
                Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
                return@addOnFailureListener
            }.addOnSuccessListener {
                Log.d("updated",it.toString())
            }
        }catch (ex:Exception){
            Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode==12 && data!=null && data.data!=null){

            quotes.setImageURI(data.data)
            Quote_Image_Uri=data.data
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}


