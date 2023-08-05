package com.app.quote.app.quotesapp.Activities

import com.app.quote.app.quotesapp.Fragments.*
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

import com.app.quote.app.quotesapp.adapters.FragmentsAdapter
import com.app.quote.app.quotesapp.models.Qoutes_firebase

import com.app.quote.app.quotesapp.models.UserToken
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
import com.app.quote.app.quotesapp.Fragments.SettingsFragment
import kotlinx.android.synthetic.AdminPanel.activity_main.*
import com.app.quote.app.quotesapp.R

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
    private lateinit var btnDialog: BottomSheetDialog
    private lateinit var select_imd_btn:ImageView
    private var Quote_Image_Uri: Uri?=null
    private lateinit var delete_quote: FloatingActionButton
    private lateinit var dialog_add_fab:FloatingActionButton
    private lateinit var etd_discription:EditText
    private lateinit var progressBar:ProgressBar

    private lateinit var UserInfo: UserToken

    @SuppressLint("RestrictedApi")
    private var Token:String?=null
    private lateinit var pw: PopupWindow
    //private lateinit var profile_iv:ImageView
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //try {
            //Toast.makeText(this, "line 71",Toast.LENGTH_LONG).show()
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
            add_fab.SetUpfabs()
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
        val layout = LayoutInflater.from(this).inflate(R.layout.update_profile_dialog,null,false)
        //profile_iv=layout.findViewById(R.id.profile_iv)
        pw = PopupWindow(layout, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        pw.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pw.isOutsideTouchable = true
            profileImageView.SetUpProfile()
            btnDialog= BottomSheetDialog(this)
            var dialog_layout=LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_layout,null,false)
            btnDialog.apply {
                this.setOnShowListener {
                    var btn_sheet=(it as BottomSheetDialog).findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
                    var btn_behavior= BottomSheetBehavior.from(btn_sheet)
                    //btn_behavior.peekHeight=Resources.getSystem().displayMetrics.heightPixels
                    btn_behavior.state= BottomSheetBehavior.STATE_EXPANDED
                }
                setContentView(dialog_layout)

                this.hide()
                this.setOnCancelListener {
                   // OnHide()
                }
                this.setOnDismissListener {
                   // OnHide()
                }
            }
            select_imd_btn=dialog_layout.findViewById(R.id.quotes)
            delete_quote=dialog_layout.findViewById(R.id.delete_quote)
            dialog_add_fab=dialog_layout.findViewById(R.id.dialog_add_fab)
            etd_discription=dialog_layout.findViewById(R.id.etd_discription)
            progressBar=dialog_layout.findViewById(R.id.progressBar)
            select_imd_btn.setOnClickListener {
                var intent= Intent().apply {
                    type="image/*"
                    action= Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES,arrayOf("image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(intent,"أختر صورة"),intent_request_code)
            }
            delete_quote.setOnClickListener {
                Quote_Image_Uri=null
                select_imd_btn.setImageResource(R.drawable.ic_add_black_24dp)
                etd_discription.text=null
            }
            dialog_add_fab.setOnClickListener {
                if (etd_discription.text.isNullOrEmpty()){
                    etd_discription.error="الوصف لا يجب ان يكون فارغا"
                    return@setOnClickListener
                }
                if(Quote_Image_Uri==null){
                    Toast.makeText(this,"اختر صورة",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                it.visibility=View.INVISIBLE
                progressBar.visibility=View.VISIBLE
                delete_quote.isEnabled=false
                val document=mfirestore.collection("Quotes").document().id
                UploadToFirebase(document){path ->

                    mfirestore.collection("Quotes").document(document).set(
                        Qoutes_firebase(0,document,etd_discription.text.toString(),path,Timestamp.now().toDate())
                    ).addOnCompleteListener {
                        if (it.exception==null){
                            btnDialog.dismiss()
                            OnHide()
                            Toast.makeText(this,"تم اضافة الاقتباس",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this@MainActivity,"فشل الاتصال, الرجاء المحاولة مرة أخرى ",Toast.LENGTH_LONG).show()
                            dialog_add_fab.visibility=View.VISIBLE
                            progressBar.visibility=View.INVISIBLE
                            delete_quote.isEnabled=true
                        }
                    }
                    it.visibility=View.VISIBLE
                    progressBar.visibility=View.INVISIBLE
                    delete_quote.isEnabled=true
                }
            }
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

    override fun onStart() {
        super.onStart()
    }
    private fun OnHide() {
        Quote_Image_Uri=null
        select_imd_btn.setImageResource(R.drawable.ic_add_black_24dp)
        etd_discription.text = null
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode==intent_request_code && data!=null && data.data!=null){
            //progr_upload.visibility= View.VISIBLE
            select_imd_btn.setImageURI(data.data)
            Quote_Image_Uri=data.data
        }
    }
    private fun CircleImageView.SetUpProfile(){
        this.setOnClickListener {
            startActivity(Intent(this@MainActivity,ProfileActivity::class.java))
            //pw.showAsDropDown(profile_iv)
        }
    }
    private fun FloatingActionButton.SetUpfabs(){
        this.setOnClickListener {
            try {
                when(this.id){
                    R.id.add_fab->{
                        btnDialog.show()
                    }
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

    private fun getbytes(Uri:Uri,Complete:(bytes:ByteArray?)->Unit) {
        var selected_img_ibtm= MediaStore.Images.Media.getBitmap(this.contentResolver,Uri)
        var outpu_storage= ByteArrayOutputStream()
        selected_img_ibtm.compress(Bitmap.CompressFormat.JPEG,20,outpu_storage)
        Complete(outpu_storage.toByteArray())
    }
    private fun UploadToFirebase(byte:String,OnComplete:(path:String)->Unit) {
        var storage_ref=mStorage.getReference("QuotesPictures/$byte")

        getbytes(Quote_Image_Uri!!){
            storage_ref.putBytes(it!!).addOnCompleteListener{task ->
                if (task.exception!=null){
                    Toast.makeText(this@MainActivity,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                }else if (task.isComplete){
                    storage_ref.downloadUrl.addOnSuccessListener{
                        OnComplete(storage_ref.path)
                    }
                }
            }
        }

    }





}
