package com.app.quote.app.quotesapp.Fragments



import com.app.quote.app.quotesapp.Helpers.Adapter_Items_Click
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.app.quote.app.quotesapp.R

import com.app.quote.app.quotesapp.adapters.ImagesAdapter

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.text.format.DateFormat
import android.widget.ImageView

import com.app.quote.app.quotesapp.Helpers.Fav_BD_Helper
import com.app.quote.app.quotesapp.Helpers.Shared_Perfernces
import com.app.quote.app.quotesapp.models.Qoutes_firebase
import com.bumptech.glide.Glide


import java.io.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment(), Adapter_Items_Click {

    lateinit var  pw: Dialog
    lateinit var images_recycler: RecyclerView
    lateinit var images_Swipe: SwipeRefreshLayout
    lateinit var images_loading_progresse: ProgressBar
    lateinit var image:ImageView
    lateinit var dialog_root:ConstraintLayout
    private val storage_instance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val mfirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private var Quotes=ArrayList<Qoutes_firebase>()
    private lateinit var dbhelper: Fav_BD_Helper
    lateinit var dialog_fav:FloatingActionButton
    private lateinit var mresult:ByteArray
    private lateinit var saving_progress:ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_gallery, container, false)
        // Inflate the layout for this fragment
        dbhelper= Fav_BD_Helper(activity!!)
        images_recycler=view.findViewById(R.id.images_recycler)
        images_Swipe=view.findViewById(R.id.images_Swipe)
        images_loading_progresse=view.findViewById(R.id.images_loading_progresse)
        GetQuotes{
            images_recycler.InitRecyclerView(it)
        }
        val layout =LayoutInflater.from(activity!!).inflate(R.layout.image_gallery_dialog,null,false)
        image=layout.findViewById(R.id.popup)
        dialog_fav=layout.findViewById(R.id.dialog_fav)
        dialog_root=layout.findViewById(R.id.dialog_root)
        saving_progress=layout.findViewById(R.id.saving_progress)
        pw= Dialog(activity,android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor)
        pw.SetUpPopup(layout)
        images_Swipe.Refrech()
        return view
    }
    private fun Dialog.SetUpPopup(layout:View){
        this.apply {
            setContentView(layout)
            setCanceledOnTouchOutside(true)
        }

    }
    private fun SwipeRefreshLayout.Refrech(){
        this.setOnRefreshListener{
            try {
                GetQuotes{
                    images_recycler.InitRecyclerView(it)
                    this.isRefreshing=false
                }
            }catch (ex1:Exception){
                Toast.makeText(activity,ex1.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun GetQuotes(getitems:(ArrayList<Qoutes_firebase>)->Unit){
        var query=mfirestore
            .collection("Quotes")
            .orderBy("date", Query.Direction.DESCENDING)
        query.get().addOnCompleteListener {
            if (it.exception!=null){
                Toast.makeText(activity,it.exception!!.message.toString(),
                    Toast.LENGTH_LONG).show()
                return@addOnCompleteListener
            }else{
                Quotes.clear()
                it.getResult()!!.documents.forEach { document->
                    var quote=document.toObject(Qoutes_firebase::class.java)!!
                    Quotes.add(quote)
                }
                getitems(Quotes)
            }
        }
    }
    private fun RecyclerView.InitRecyclerView(data:ArrayList<Qoutes_firebase>) {
        this.apply {
            layoutManager= StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            var adapter= ImagesAdapter(
                activity!!,
                data,
                storage_instance,
                this@GalleryFragment
            )
            this.adapter=adapter
            images_loading_progresse.visibility=View.INVISIBLE
        }
    }
    override fun OnClicked(Quote: Qoutes_firebase, position: Int) {
        Glide.with(activity!!).load(storage_instance.getReference(Quote.picture)).into(image)
        var isadded=dbhelper.GetIsAdded(Quote.id)
        dialog_fav.Save(Quote,isadded,position)
        when(isadded){
            false->{ Glide.with(activity!!).load(R.drawable.ic_favorite_border_black_24dp).into(dialog_fav)}
            true->{ Glide.with(activity!!).load(R.drawable.ic_favorite_black_24dp).into(dialog_fav)}
        }
        pw.window.setLayout(dialog_root.layoutParams.width,dialog_root.layoutParams.height)
        pw.show()
    }
    private fun FloatingActionButton.Save(Quote: Qoutes_firebase, isadded:Boolean, position:Int){
        this.setOnClickListener {
            when(isadded){
                false->{
                    saving_progress.visibility=View.VISIBLE
                    this@GalleryFragment.OnInsert(Quote
                        ,position)
                }
                true->{
                    Snackbar.make(it,"الصورة محفوظة مسبقا", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun OnDialogUpdate(Quote: Qoutes_firebase, position: Int) {
        //var hepler: Fav_BD_Helper = Fav_BD_Helper(activity!!)
        saving_progress.visibility=View.INVISIBLE
        var isadded=dbhelper.GetIsAdded(Quote.id)

        when(isadded){
            false->{ Glide.with(activity!!).load(R.drawable.ic_favorite_border_black_24dp).into(dialog_fav)}
            true->{ Glide.with(activity!!).load(R.drawable.ic_favorite_black_24dp).into(dialog_fav)}
        }
    }
    override fun OnInsert(
        Quote: Qoutes_firebase, position:Int
    ) {
        try {
            var onemegabyte:Long=10*(1024*1024)
            storage_instance.getReference(Quote.picture).getBytes(onemegabyte).addOnCompleteListener {
                if(it.exception!=null){
                    Toast.makeText(activity,"حدث خطا الرجاء المحاولة مرة اخرى",Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }else if (it.isSuccessful || it.isComplete){
                    dbhelper.insertdata(Quote.id,Quote.discription,it.result!!,
                        DateFormat.format("yyyy/MM/dd"
                            ,Quote.date).toString(),DateFormat.format("hh:mm aa",Quote.date).toString(),"1")
                    mresult=it.result!!
                    CheckPermission(it.result!!)
                    this.OnDialogUpdate(Quote,position)
                }
            }.addOnFailureListener {
                Toast.makeText(activity,"فشل في الاتصال :تم حفظ الصورة افتراضيا",Toast.LENGTH_LONG).show()
                dbhelper.insertdata(Quote.id,Quote.discription,getbyte_array(),
                    DateFormat.format("yyyy/MM/dd"
                        ,Quote.date).toString(),DateFormat.format("hh:mm aa",Quote.date).toString(),Quote.fav.toString())
            }
        }catch (ex1:Exception){
            Toast.makeText(activity,ex1.toString(),Toast.LENGTH_LONG).show()
        }
    }
    private fun getbyte_array():ByteArray {
        var ByteArray: ByteArray?
        var bitmap= BitmapFactory.decodeResource(this.resources, R.drawable.image)
        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        ByteArray= stream.toByteArray()
        return ByteArray
    }
    private fun SaveBitmapToDevice(result: ByteArray) {
        var bitmap=BitmapFactory.decodeByteArray(result,0,result.size)
        var filepath= Environment.getExternalStorageDirectory()
        var directoty= File(Shared_Perfernces(activity!!).GetPath())
        directoty.mkdir()
        var file= File(directoty,System.currentTimeMillis().toString()+ ".jpg")
        try {
            var outputstream= FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputstream)
            outputstream.flush()
            outputstream.close()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    private fun CheckPermission(result: ByteArray){
        if (Build.VERSION.SDK_INT>=23){
            if (activity!!.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
                mresult=result
            }
            else if (activity!!.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                SaveBitmapToDevice(result)
            }
        }else{
            SaveBitmapToDevice(result)
        }
    }
}
