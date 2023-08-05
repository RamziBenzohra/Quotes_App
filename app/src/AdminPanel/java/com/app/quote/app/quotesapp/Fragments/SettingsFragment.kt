package com.app.quote.app.quotesapp.Fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.app.quote.app.quotesapp.Activities.IntroActivity


import com.app.quote.app.quotesapp.Activities.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.app.quote.app.quotesapp.Helpers.Shared_Perfernces
import com.app.quote.app.quotesapp.models.UserToken
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.app.quote.app.quotesapp.R
import com.app.quote.app.quotesapp.models.Profile


class SettingsFragment : Fragment(),TextWatcher,AdapterView.OnItemSelectedListener {



    lateinit var edit_profile:CardView
    lateinit var change_password:CardView
    lateinit var notification:CardView
    lateinit var log_out:CardView
    //lateinit var viewprofile:CardView
    lateinit var information_card:CardView
    lateinit var notification_tv: Switch
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage_instance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val mfirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private var Token:String?=null

    private lateinit var mbtnDialog: BottomSheetDialog
    private lateinit var mPassChangeDialog: BottomSheetDialog
    private lateinit var delete_quote: FloatingActionButton
    private lateinit var edit_dialog_add_fab: FloatingActionButton
    private lateinit var etd_discription:EditText
    private lateinit var edit_progressBar:ProgressBar
    private lateinit var FirstIntro:EditText
    private lateinit var SecondIntro:EditText
    private lateinit var WebSite:EditText
    private lateinit var Phone:EditText
    private lateinit var Email:EditText
    private lateinit var Facebook:EditText
    private lateinit var Instegram:EditText
    private lateinit var Twitter:EditText
    private lateinit var Pinterest:EditText
    private lateinit var Youtube:EditText
    //
    private lateinit var etd_old_pass:EditText
    private lateinit var etd_new_pass:EditText
    private lateinit var etd_new_pass_confirm:EditText
    private lateinit var btn_update:Button
    private lateinit var update_progress:ProgressBar
    //private lateinit var Storage_iv:CircleImageView
    private lateinit var Storage_tv:Spinner
    //
    //
    private lateinit var Log_out_dialog: BottomSheetDialog
    private lateinit var yes_btn:Button
    private lateinit var no_btn:Button
    //
    //

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_settings, container, false)
        //try{
            edit_profile=view.findViewById(R.id.edite_profile)
            change_password=view.findViewById(R.id.change_password)
            notification=view.findViewById(R.id.notification)
            //viewprofile=view.findViewById(R.id.viewprofile)
            information_card=view.findViewById(R.id.information_card)
            log_out=view.findViewById(R.id.log_out)
            notification_tv=view.findViewById(R.id.notification_tv)
            //Storage_iv=view.findViewById(R.id.storage_iv)
        Storage_tv=view.findViewById(R.id.Storage_tv)
            edit_profile.Settings()
            change_password.Settings()
            notification.Settings()
            log_out.Settings()
            //viewprofile.Settings()
            information_card.Settings()

            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {it->
                if (it.exception!=null){
                    return@addOnCompleteListener
                }else {
                    Token = it.result!!.token

                    Add_Cheched_Listener(Token!!){
                        notification_tv.isChecked=it
                    }
                    notification_tv.setOnCheckedChangeListener{ compoundButton: CompoundButton, isChecked: Boolean ->
                        notification_tv.SetUpSwith()
                    }
                }
            }
            mbtnDialog= BottomSheetDialog(activity!!)
        mPassChangeDialog= BottomSheetDialog(activity!!)
            var dialog_layout=LayoutInflater.from(activity!!).inflate(R.layout.edit_profile_dialog,null,false)
            mbtnDialog.apply {
                setContentView(dialog_layout)
            }
            edit_dialog_add_fab=dialog_layout.findViewById(R.id.edit_dialog_add_fab)
            edit_progressBar=dialog_layout.findViewById(R.id.edit_progressBar)
            FirstIntro=dialog_layout.findViewById(R.id.firs_intro)
            SecondIntro=dialog_layout.findViewById(R.id.second_intro)
            WebSite=dialog_layout.findViewById(R.id.web_site)
            Phone=dialog_layout.findViewById(R.id.phone)
            Email=dialog_layout.findViewById(R.id.email)
            Facebook=dialog_layout.findViewById(R.id.facebook)
            Instegram=dialog_layout.findViewById(R.id.instagram)
            Twitter=dialog_layout.findViewById(R.id.twitter)
            Pinterest=dialog_layout.findViewById(R.id.pintrest)
            Youtube=dialog_layout.findViewById(R.id.youtube)
        var update_dialog_layout=LayoutInflater.from(activity!!).inflate(R.layout.password_update_dialog,null,false)
        mPassChangeDialog.apply {
            setContentView(update_dialog_layout)
        }
            edit_dialog_add_fab.Upload()
        etd_old_pass=update_dialog_layout.findViewById(R.id.etd_old_pass)
        etd_new_pass=update_dialog_layout.findViewById(R.id.etd_new_pass)
        etd_new_pass_confirm=update_dialog_layout.findViewById(R.id.etd_new_pass_confirm)
        btn_update=update_dialog_layout.findViewById(R.id.btn_update)
        update_progress=update_dialog_layout.findViewById(R.id.update_progress)
        //
        Log_out_dialog=BottomSheetDialog(activity!!)
        var sign_out_dialog=LayoutInflater.from(activity!!).inflate(R.layout.sign_out_dialog,null,false)
        Log_out_dialog.apply {

            setContentView(sign_out_dialog)
        }
        yes_btn=sign_out_dialog.findViewById(R.id.yes_btn)
        //no_btn=sign_out_dialog.findViewById(R.id.no_btn)
        //
        etd_new_pass.addTextChangedListener(this@SettingsFragment)
        etd_new_pass_confirm.addTextChangedListener(this@SettingsFragment)
        btn_update.SetUp()
        Storage_tv.SetUp()
        return view
    }
    private fun Spinner.SetUp(){
        this.apply {
            ArrayAdapter.createFromResource(activity!!,R.array.storage_spinner,android.R.layout.simple_spinner_item).also { adp->
                adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                this@SetUp.adapter=adp
                this@SetUp.onItemSelectedListener=this@SettingsFragment
            }
        }
    }
    @SuppressLint("RestrictedApi")
    private fun FloatingActionButton.Upload(){
        this.setOnClickListener {
            var profile= Profile(FirstIntro.text.toString(),SecondIntro.text.toString()
                ,WebSite.text.toString(),Phone.text.toString(),Email.text.toString()
                ,Facebook.text.toString()
                ,Instegram.text.toString(),Twitter.text.toString(),Pinterest.text.toString(),Youtube.text.toString())
            edit_progressBar.visibility=View.VISIBLE
            this.visibility=View.INVISIBLE
            mfirestore.collection("Profile").document("Profile").set(profile).addOnCompleteListener {
                if (it.exception!=null){
                    toast(it.exception.toString())
                    return@addOnCompleteListener
                }else{
                    edit_progressBar.visibility=View.INVISIBLE
                    this.visibility=View.VISIBLE
                    mbtnDialog.dismiss()
                    Snackbar.make(this,"Updated", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun Switch.SetUpSwith(){
        when(this.isChecked){
            false->{
                //subscription_fab.setImageResource(R.drawable.ic_notifications_none_black_24dp)
                mfirestore.collection("UsersTokens").document(Token!!).update("subscription",false).addOnCompleteListener {
                    if (it.exception!=null){
                        toast(it.exception.toString())
                        return@addOnCompleteListener
                    }else{

                        //Snackbar.make(this,"Notification Off", Snackbar.LENGTH_LONG).show()
                        //notification_tv.setCompoundDrawables(activity!!.getDrawable(R.drawable.ic_notifications_off_black_24dp),null,null,null)
                        //Log.d("Notification Off",UserInfo.subscription.toString())
                    }
                }

            }
            true->{
                mfirestore.collection("UsersTokens").document(Token!!).update("subscription",true).addOnCompleteListener {
                    if (it.exception!=null){
                        toast(it.exception.toString())
                        return@addOnCompleteListener
                    }else{

                        //notification_tv.setCompoundDrawables(activity!!.getDrawable(R.drawable.ic_notifications_none_black_24dp),null,null,null)
                        //Snackbar.make(this,"Notification On",Snackbar.LENGTH_LONG).show()
                    }
                }

            }
        }
    }
    private fun toast(text: String) {
        Toast.makeText(activity!!,text,Toast.LENGTH_LONG).show()
    }
    private fun Add_Cheched_Listener(Token:String,getitems:(Boolean)->Unit):ListenerRegistration {
        val query=mfirestore
            .collection("UsersTokens").document(Token)

        return query.addSnapshotListener{ documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if (firebaseFirestoreException!=null){
                return@addSnapshotListener
            }
            getitems(documentSnapshot!!.toObject(UserToken::class.java)!!.subscription)
        }
    }

    private fun CardView.Settings(){
        this.setOnClickListener {
            when(this.id){

                R.id.edite_profile->{
                    mbtnDialog.show()
                }
                R.id.change_password->{
                    mPassChangeDialog.show()
                }
                R.id.notification->{

                }
                R.id.log_out->{
                   Log_out_dialog.show()

                }
                R.id.information_card->{

                }
            }
        }
    }
    private fun ChangePassword(oldPassword:String,newPassword: String) {
        if (mAuth.currentUser!!.email!= null){
            val credential=EmailAuthProvider.getCredential(mAuth.currentUser!!.email!!,oldPassword)
            mAuth.currentUser!!.reauthenticate(credential).addOnSuccessListener {
                mAuth.currentUser?.updatePassword(newPassword)?.addOnSuccessListener {
                    update_progress.visibility=View.INVISIBLE
                    btn_update.visibility=View.VISIBLE
                    mPassChangeDialog.dismiss()
                    Thread.sleep(500)
                    Snackbar.make(view!!,"تم تغيير كلمة المرور بنجاح",Snackbar.LENGTH_LONG).show()
                    startActivity(Intent(activity!!, SignInActivity::class.java))
                    activity!!.finish()
                }?.addOnFailureListener {
                    Snackbar.make(view!!,"فشل تغيير كلمة المرور",Snackbar.LENGTH_LONG).show()
                    Log.d("pass change_exeption",it.toString())
                    update_progress.visibility=View.INVISIBLE
                    btn_update.visibility=View.VISIBLE
                }
            }.addOnFailureListener {
                Snackbar.make(view!!,"فشل تغيير كلمة المرور",Snackbar.LENGTH_LONG).show()
                Log.d("auten_exeption",it.toString())
                return@addOnFailureListener
            }
        }


    }
    private fun Button.SetUp() {
        this.setOnClickListener {
            when(this.id){
                R.id.btn_update->{
                    if(etd_old_pass.text.toString().isEmpty()){
                        etd_old_pass.error="ادخل كلمة المرور القديمة "
                        return@setOnClickListener
                    }
                    if (etd_new_pass.text.toString()!=etd_new_pass_confirm.text.toString()){
                        etd_new_pass_confirm.apply {
                            this.text=null
                            this.error=" كلمات المرور غير متطابقتان : يرجى تاكيد كلمة المرور"
                        }
                        return@setOnClickListener

                    }else if(etd_new_pass.text.toString().equals(etd_new_pass_confirm.text.toString())){
                        this.visibility= View.INVISIBLE
                        update_progress.visibility= View.VISIBLE
                        ChangePassword(etd_old_pass.text.toString(),etd_new_pass_confirm.text.toString())
                    }
                }
                R.id.yes_btn->{
                    try {
                        mAuth.signOut()
                        val intent= Intent(activity!!, IntroActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        Log_out_dialog.hide()
                        Thread.sleep(500)
                        startActivity(intent)
                    }catch (ex1:Exception){
                        //Toast.makeText(this@ProfileActivity,ex1.message.toString(), Toast.LENGTH_LONG).show()
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
        btn_update.isEnabled=etd_new_pass_confirm.text.trim().isNotEmpty()
                && etd_new_pass.text.trim().isNotEmpty()
                && etd_old_pass.text.trim().isNotEmpty()
                && etd_old_pass.text.length>6
                && etd_new_pass_confirm.text.length>6
                && etd_new_pass.text.length>6
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Shared_Perfernces(activity!!).SavePath(parent!!.getItemAtPosition(position).toString())
        Log.d("storage_path","path saved at position $position")
    }
}

