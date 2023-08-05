package com.app.quote.app.quotesapp.Activities



import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.app.quote.app.quotesapp.Activities.ConfirmEmailAndPhoneActivity

import com.app.quote.app.quotesapp.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.AdminPanel.activity_profile.*
import com.app.quote.app.quotesapp.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.AdminPanel.timeline.*

class ProfileActivity : AppCompatActivity(), TextWatcher {
    private lateinit var btnDialog: BottomSheetDialog
    private lateinit var etd_name: EditText

    private lateinit var progressBar: ProgressBar
    private lateinit var update_profile_btn: Button

    //
    private lateinit var mPassChangeDialog: BottomSheetDialog
    private lateinit var etd_old_pass: EditText
    private lateinit var etd_new_pass: EditText
    private lateinit var etd_new_pass_confirm: EditText
    private lateinit var btn_update: Button
    private lateinit var update_progress: ProgressBar
    //
    private lateinit var Log_out_dialog: BottomSheetDialog
    private lateinit var yes_btn: Button
    private lateinit var no_btn: Button
    //
    private lateinit var delete_account_dilaog: BottomSheetDialog
    private lateinit var delete_yes_btn: Button
    private lateinit var delete_account_progress:ProgressBar
    private lateinit var etd_email_delete_account: TextView
    private lateinit var etd_pass_delete_account: EditText
    //private lateinit var delete_no_btn: Button
    //
    private lateinit var mEmailChangeDialog: BottomSheetDialog
    private lateinit var etd_pass_email_update: EditText
    private lateinit var etd_email_email_update: EditText
    private lateinit var update_email_btn: Button
    private lateinit var update_email_progress: ProgressBar
    //
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage_instance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val mfirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val mCurrentUserRef: DocumentReference
        get() = mfirestore.document("Users/${mAuth.currentUser!!.uid}")
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
       // try {

            btnDialog = BottomSheetDialog(this)
            update_profile_fab.SetUpFab()
            change_pass_fab.SetUpFab()
            sign_out_fab.SetUpFab()
            change_prpic_fab.SetUpFab()
            var dialog_layout =
                LayoutInflater.from(this@ProfileActivity)
                    .inflate(R.layout.update_profile_dialog, null, false)
            btnDialog.apply {
                setContentView(dialog_layout)
            }

            etd_name = dialog_layout.findViewById(R.id.etd_name)


            progressBar = dialog_layout.findViewById(R.id.progressBar)
            update_profile_btn = dialog_layout.findViewById(R.id.update_profile_btn)


            mPassChangeDialog = BottomSheetDialog(this)
            var update_dialog_layout =
                LayoutInflater.from(this).inflate(R.layout.password_update_dialog, null, false)
            mPassChangeDialog.apply {
                setContentView(update_dialog_layout)
            }
            etd_old_pass = update_dialog_layout.findViewById(R.id.etd_old_pass)
            etd_new_pass = update_dialog_layout.findViewById(R.id.etd_new_pass)
            etd_new_pass_confirm = update_dialog_layout.findViewById(R.id.etd_new_pass_confirm)
            btn_update = update_dialog_layout.findViewById(R.id.btn_update)
            update_progress = update_dialog_layout.findViewById(R.id.update_progress)
            //
            Log_out_dialog = BottomSheetDialog(this)
            var sign_out_dialog =
                LayoutInflater.from(this).inflate(R.layout.sign_out_dialog, null, false)
            Log_out_dialog.apply {
                setContentView(sign_out_dialog)
            }
            yes_btn = sign_out_dialog.findViewById(R.id.yes_btn)
            //no_btn = sign_out_dialog.findViewById(R.id.no_btn)

            delete_account_dilaog = BottomSheetDialog(this)
            val delete_acount_dilaog =
                LayoutInflater.from(this).inflate(R.layout.delete_acount_dilaog, null, false)
            delete_account_dilaog.apply {
                setContentView(delete_acount_dilaog)
            }
            delete_yes_btn = delete_acount_dilaog.findViewById(R.id.delete_yes_btn)
            delete_account_progress = delete_acount_dilaog.findViewById(R.id.delete_account_progress)
            etd_email_delete_account = delete_acount_dilaog.findViewById(R.id.etd_email_delete_account)
            etd_pass_delete_account = delete_acount_dilaog.findViewById(R.id.etd_pass_delete_account)

            mEmailChangeDialog = BottomSheetDialog(this)
            var change_email_dialog =
                LayoutInflater.from(this).inflate(R.layout.change_email_dialog, null, false)
            mEmailChangeDialog.apply {
                setContentView(change_email_dialog)
            }
            etd_pass_email_update = change_email_dialog.findViewById(R.id.etd_pass_email_update)
            etd_email_email_update = change_email_dialog.findViewById(R.id.etd_email_email_update)
            //etd_new_pass_confirm = change_email_dialog.findViewById(R.id.etd_new_pass_confirm)
            update_email_btn = change_email_dialog.findViewById(R.id.update_email_btn)
            update_email_progress = change_email_dialog.findViewById(R.id.update_email_progress)

            //
        GetCurrentUser {
            user = it
            if (user.ProfilePic != "") {
                Glide.with(this).load(storage_instance.getReference(user.ProfilePic))
                    .into(propic)
            }

            name_tv.text = user.Name
            if(mAuth.currentUser!!.isEmailVerified){
                email.text = user.Email
                email.setCompoundDrawables(getDrawable(R.drawable.ic_email_black_24dp),null,getDrawable(R.drawable.ic_check_black_24dp),null)
            }else{
                email.text = user.Email
            }
            etd_email_delete_account.setText(user.Email)

            etd_name.setText(user.Name)


            update_profile_btn.SetUp()
            confirm_fab.SetUpFab()
            change_email_fab.SetUpFab()
            delete_fab.SetUpFab()

        }
            etd_new_pass.addTextChangedListener(this)
            etd_new_pass_confirm.addTextChangedListener(this)
            btn_update.SetUp()
            update_email_btn.SetUp()
            yes_btn.SetUp()

            delete_yes_btn.SetUp()
            //delete_no_btn.SetUp()
        /*}catch (ex:Exception){
            Toast.makeText(this@ProfileActivity,ex.toString(), Toast.LENGTH_LONG)
                .show()
        }*/
    }

    private fun GetCurrentUser(function: (user: User) -> Unit) {
        mCurrentUserRef.get().addOnSuccessListener {
            function(it.toObject(User::class.java)!!)
        }.addOnFailureListener {
            Toast.makeText(this@ProfileActivity, " فشل في تحميل معلومات الحساب", Toast.LENGTH_LONG)
                .show()
        }
    }



    private fun Button.SetUp() {
        this.setOnClickListener {
            when (this.id) {
                R.id.btn_update -> {
                    if (etd_old_pass.text.toString().isEmpty()) {
                        etd_old_pass.error = "ادخل كلمة المرور القديمة "
                        return@setOnClickListener
                    }
                    if (etd_new_pass.text.toString() != etd_new_pass_confirm.text.toString()) {
                        etd_new_pass_confirm.apply {
                            this.text = null
                            this.error = " كلمات المرور غير متطابقتان : يرجى تاكيد كلمة المرور"
                        }
                        return@setOnClickListener

                    } else if (etd_new_pass.text.toString().equals(etd_new_pass_confirm.text.toString())) {
                        this.visibility = View.INVISIBLE
                        update_progress.visibility = View.VISIBLE
                        ChangePassword(
                            etd_old_pass.text.toString(),
                            etd_new_pass_confirm.text.toString()
                        )
                    }
                }
                R.id.yes_btn -> {
                    try {
                        mAuth.signOut()
                        val intent = Intent(this@ProfileActivity, IntroActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        Log_out_dialog.hide()
                        Thread.sleep(500)
                        startActivity(intent)
                    } catch (ex1: Exception) {
                        Toast.makeText(this@ProfileActivity,ex1.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
                /*R.id.no_btn -> {
                    Log_out_dialog.hide()
                }*/
                R.id.delete_yes_btn->{
                    delete_account_progress.visibility=View.VISIBLE
                    delete_yes_btn.visibility=View.INVISIBLE
                    DeleteAccount()
                }

                R.id.update_profile_btn -> {
                    if (etd_name.text.toString().isEmpty()) {
                        etd_name.error = "ادخل الاسم "
                        return@setOnClickListener
                    }

                    update_profile_btn.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                    UpdateProfile(
                        User(
                            etd_name.text.toString(),
                            mAuth.currentUser!!.email!!,
                            user.ProfilePic
                        )
                    )
                }
                R.id.update_email_btn->{
                    if (etd_pass_email_update.text.toString().isEmpty()) {
                        etd_pass_email_update.error = "ادخل كلمة المرور  "
                        return@setOnClickListener
                    }
                    if (etd_email_email_update.text.toString().isEmpty()) {
                        etd_email_email_update.error = "ادخل الاميل الجديد "
                        return@setOnClickListener
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(etd_email_email_update.text.toString()).matches()) {
                        etd_email_email_update.error = "من فضلك ادخل الاميل صحيح"
                        etd_email_email_update.requestFocus()
                        return@setOnClickListener
                    }
                    update_email_btn.visibility = View.INVISIBLE
                    update_email_progress.visibility = View.VISIBLE
                    ChangeEmail()
                }
            }
        }
    }
    private fun DeleteAccount() {
        val uid=mAuth.currentUser!!.uid
        val EmailCredential=EmailAuthProvider.getCredential(mAuth.currentUser!!.email!!
            ,etd_pass_delete_account.text.trim().toString())
        mAuth.currentUser!!.reauthenticate(EmailCredential).addOnSuccessListener {
            var old_user_reference=mfirestore.document("Users/${uid}")
            old_user_reference.delete().addOnFailureListener {
                Toast.makeText(this@ProfileActivity, it.toString(), Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                mAuth.currentUser!!.delete().addOnFailureListener {
                    Toast.makeText(this@ProfileActivity, it.toString(), Toast.LENGTH_LONG).show()
                }.addOnSuccessListener {
                    delete_account_progress.visibility=View.INVISIBLE
                    delete_yes_btn.visibility=View.VISIBLE
                    Toast.makeText(this@ProfileActivity,"تم حذف الحساب",Toast.LENGTH_LONG).show()
                    delete_account_dilaog.hide()
                    Thread.sleep(500)
                    var intent=Intent(this@ProfileActivity, IntroActivity::class.java)
                    intent.apply {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this@ProfileActivity, it.toString(), Toast.LENGTH_LONG).show()
        }
    }
    private fun ChangeEmail() {
        var EmailCredential=EmailAuthProvider.getCredential(mAuth.currentUser!!.email!!
            ,etd_pass_email_update.text.trim().toString())
        mAuth.currentUser!!.reauthenticate(EmailCredential).addOnSuccessListener {

            mAuth.currentUser!!.updateEmail(etd_email_email_update.text.trim().toString())
                .addOnFailureListener {
                    update_email_btn.visibility = View.VISIBLE
                    update_email_progress.visibility = View.INVISIBLE
            }.addOnSuccessListener {
                    update_email_btn.visibility = View.VISIBLE
                    update_email_progress.visibility = View.INVISIBLE
                    mCurrentUserRef.set(User(user.Name,mAuth.currentUser!!.email!!,user.ProfilePic))
                        .addOnFailureListener {
                            update_email_btn.visibility = View.VISIBLE
                            update_email_progress.visibility = View.INVISIBLE
                            Toast.makeText(this@ProfileActivity,it.toString(),Toast.LENGTH_LONG).show()
                            mEmailChangeDialog.hide()
                        }
                        .addOnSuccessListener {
                            GetCurrentUser {
                                user=it
                            }
                            Toast.makeText(this@ProfileActivity,"تم نغيير الاميل",Toast.LENGTH_LONG).show()
                            mEmailChangeDialog.hide()
                        }

            }
        }.addOnFailureListener {
            update_email_btn.visibility = View.VISIBLE
            update_email_progress.visibility = View.INVISIBLE
            Toast.makeText(this@ProfileActivity,it.toString(),Toast.LENGTH_LONG).show()
            mEmailChangeDialog.hide()
        }

    }

    private fun UpdateProfile(
        user_data: User
    ) {
        try{

            mCurrentUserRef.set(user_data)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@ProfileActivity,
                        "تم  تحيث الحساب",
                        Toast.LENGTH_LONG
                    ).show()
                    update_profile_btn.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    btnDialog.hide()
                    GetCurrentUser {
                        user=it
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@ProfileActivity,
                        "فشل  تحيث الحساب",
                        Toast.LENGTH_LONG
                    ).show()

                    update_profile_btn.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }

        }catch (ex:Exception){
            Toast.makeText(
                this@ProfileActivity,
                ex.toString(),
                Toast.LENGTH_LONG
            ).show()
        }


    }

    private fun ChangePassword(oldPassword: String, newPassword: String) {
        if (mAuth.currentUser!!.email != null) {
            val credential =
                EmailAuthProvider.getCredential(mAuth.currentUser!!.email!!, oldPassword)
            mAuth.currentUser!!.reauthenticate(credential).addOnSuccessListener {
                mAuth.currentUser?.updatePassword(newPassword)?.addOnSuccessListener {
                    update_progress.visibility = View.INVISIBLE
                    btn_update.visibility = View.VISIBLE
                    mPassChangeDialog.dismiss()
                    Thread.sleep(500)
                    Snackbar.make(view!!, "تم تغيير كلمة المرور بنجاح", Snackbar.LENGTH_LONG).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }?.addOnFailureListener {
                    Snackbar.make(view!!, "فشل تغيير كلمة المرور", Snackbar.LENGTH_LONG).show()
                    Log.d("pass change_exeption", it.toString())
                    update_progress.visibility = View.INVISIBLE
                    btn_update.visibility = View.VISIBLE
                }
            }.addOnFailureListener {
                Snackbar.make(view!!, "فشل تغيير كلمة المرور", Snackbar.LENGTH_LONG).show()
                Log.d("auten_exeption", it.toString())
                return@addOnFailureListener
            }
        }


    }

    private fun FloatingActionButton.SetUpFab() {
        this.setOnClickListener {
            when (this.id) {
                R.id.update_profile_fab -> {
                    btnDialog.show()
                }
                R.id.change_pass_fab -> {
                    mPassChangeDialog.show()
                }
                R.id.sign_out_fab -> {
                    Log_out_dialog.show()
                }
                R.id.change_prpic_fab -> {
                    var intent = Intent().apply {
                        type = "image/*"
                        action = Intent.ACTION_GET_CONTENT
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                    }
                    startActivityForResult(Intent.createChooser(intent, "أختر صورة"), 12)
                }
                R.id.confirm_fab ->{
                    var intent=Intent(this@ProfileActivity, ConfirmEmailAndPhoneActivity::class.java)
                    intent.apply{
                      // var phone="+"+CountryData.countryAreaCodes[countries_spinner.selectedItemPosition]+etd_conf_phone.text.trim().toString()
                        putExtra("email",user.Email)
                    }
                    startActivity(intent)
                }
                R.id.delete_fab->{
                    delete_account_dilaog.show()

                }
                R.id.change_email_fab->{
                    mEmailChangeDialog.show()
                }
            }
        }
    }
    private fun UploadToFirebase(
        Quote_Image_Uri: Uri,
        image_name: String,
        OnComplete: (path: String) -> Unit
    ) {
        try {
            var storage_ref =
                storage_instance.getReference("Users_Profile_Pictures/${mAuth.currentUser!!.uid}/$image_name")
            storage_ref.putFile(Quote_Image_Uri).addOnSuccessListener {
                storage_ref.downloadUrl.addOnSuccessListener{uri->
                    OnComplete(storage_ref.path)
                }
            }.addOnFailureListener {
                Toast.makeText(
                this@ProfileActivity,
                it.message.toString(),
                Toast.LENGTH_LONG
            ).show()
            }
        }catch (ex: Exception) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 12 && data != null && data.data != null) {
            upload_progress.visibility=View.VISIBLE
            UploadToFirebase(data.data!!, Timestamp.now().toDate().time.toString()) { path ->
                GetCurrentUser {
                    user=it
                    var updatedUser= User(it.Name,it.Email,path)
                    mCurrentUserRef.set(updatedUser).addOnSuccessListener {
                        Glide.with(this)
                            .load(storage_instance.getReference(updatedUser.ProfilePic.toString()))
                            .into(propic)
                        upload_progress.visibility=View.INVISIBLE
                    }.addOnFailureListener {
                        return@addOnFailureListener
                    }
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btn_update.isEnabled = etd_new_pass_confirm.text.trim().isNotEmpty()
                && etd_new_pass.text.trim().isNotEmpty()
                && etd_old_pass.text.trim().isNotEmpty()
                && etd_old_pass.text.length > 6
                && etd_new_pass_confirm.text.length > 6
                && etd_new_pass.text.length > 6
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
