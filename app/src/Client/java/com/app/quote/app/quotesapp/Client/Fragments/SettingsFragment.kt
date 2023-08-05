package com.app.quote.app.quotesapp.Client.Fragments


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

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.app.quote.app.quotesapp.Client.Helpers.Shared_Perfernces
import com.app.quote.app.quotesapp.Client.models.UserToken
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.app.quote.app.quotesapp.R
import com.app.quote.app.quotesapp.Client.models.Profile


class SettingsFragment : Fragment(),AdapterView.OnItemSelectedListener {




    lateinit var notification:CardView

    //lateinit var viewprofile:CardView

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



    //private lateinit var update_progress:ProgressBar
    //private lateinit var Storage_iv:CircleImageView
    private lateinit var Storage_tv:Spinner
    //
    //
    //private lateinit var Log_out_dialog: BottomSheetDialog
    //private lateinit var yes_btn:Button

    //
    //

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_settings, container, false)
        //try{

            notification_tv=view.findViewById(R.id.notification_tv)
            //Storage_iv=view.findViewById(R.id.storage_iv)
        Storage_tv=view.findViewById(R.id.Storage_tv)




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

        //

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


    override fun onNothingSelected(parent: AdapterView<*>?) {


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Shared_Perfernces(activity!!).SavePath(parent!!.getItemAtPosition(position).toString())
        Log.d("storage_path","path saved at position $position")
    }
}

