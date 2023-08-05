package com.app.quote.app.quotesapp.Client.Fragments



import com.app.quote.app.quotesapp.Client.Helpers.Adapter_Items_Click
import com.app.quote.app.quotesapp.Client.Helpers.Fav_BD_Helper
import com.app.quote.app.quotesapp.Client.Helpers.Shared_Perfernces
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


import com.app.quote.app.quotesapp.Client.adapters.time_line_adapter

import com.app.quote.app.quotesapp.Client.models.Qoutes_firebase
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.Section

import java.io.*
import com.app.quote.app.quotesapp.R
import java.util.*
import kotlin.collections.ArrayList


class PersonFragment : Fragment(), Adapter_Items_Click {
    private lateinit var msection:Section
    lateinit var recycler:RecyclerView
    lateinit var Swipe:SwipeRefreshLayout
    lateinit var addfab:FloatingActionButton
    lateinit var loading_progresse:ProgressBar
    private val storage_instance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val mfirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private var items=ArrayList<Qoutes_firebase>()
    private var NewItems= ArrayList<Qoutes_firebase>()
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val mStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private lateinit var mresult:ByteArray
    ////
    private  var  AdLoaded:Boolean=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var View=inflater.inflate(R.layout.fragment_person, container, false)
        try {
            recycler=View.findViewById(R.id.time_recycler)
            Swipe=View.findViewById(R.id.swipe_per)
            loading_progresse=View.findViewById(R.id.loading_progresse)
            loading_progresse.visibility= android.view.View.VISIBLE
            var notification_items=activity!!.getSharedPreferences("notification",Context.MODE_PRIVATE)
            GetQuotes{
                recycler.InitRecyclerView(it)
                notification_items.edit().putInt("size",it.size).apply()
                GetNotifications{
                    try {
                        var notificztionscount=notification_items.getInt("size",0)
                        when(it.size){
                            0->{}
                            else->{
                                Toast.makeText(activity!!,"You have ${(it.size-notificztionscount)} Notifications",Toast.LENGTH_LONG).show()
                            }
                        }
                    }catch (ex:Exception){
                        Toast.makeText(activity!!,ex.toString(),Toast.LENGTH_LONG).show()
                    }

                }
            }

            Swipe.Refrech()
        }catch (ex1:Exception){
            Toast.makeText(activity,ex1.toString(),Toast.LENGTH_LONG).show()
            loading_progresse.visibility= android.view.View.INVISIBLE
        }


        return View
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
                items.clear()
                var ads_index=0
                it.getResult()!!.documents.forEach { document->
                    items.add(document.toObject(Qoutes_firebase::class.java)!!)

                    when(ads_index){
                        2->{items.add(Qoutes_firebase(1))
                        ads_index =0
                        }

                    }
                    ads_index+=1
                }
                getitems(items)

            }
        }
    }

    private fun Add_QueryListener(getitems:(ArrayList<Qoutes_firebase>)->Unit): ListenerRegistration {
        var query=mfirestore
            .collection("Quotes")
            .orderBy("date", Query.Direction.DESCENDING)
        return query.addSnapshotListener{ querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if (firebaseFirestoreException!=null){
                Toast.makeText(activity,firebaseFirestoreException.message.toString(),
                    Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }else{
                var items= ArrayList<Qoutes_firebase>()
                items.clear()
                querySnapshot!!.documents.forEach { document->
                    var quote=document.toObject(Qoutes_firebase::class.java)!!
                    items.add(quote)
                }
                getitems(items)
            }

        }
    }
    private fun GetNotifications(setNotifications:(ArrayList<Qoutes_firebase>)->Unit): ListenerRegistration{
        var query=mfirestore
            .collection("Quotes")
            .orderBy("date", Query.Direction.DESCENDING)
        return query.addSnapshotListener{ querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if (firebaseFirestoreException!=null){
                return@addSnapshotListener
            }else{
                var NewItems=ArrayList<Qoutes_firebase>()
                NewItems.clear()
                querySnapshot!!.documents.forEach { document->
                    var quote=document.toObject(Qoutes_firebase::class.java)!!
                    NewItems.add(quote)
                }
                setNotifications(NewItems)
            }

        }
    }
    /*private fun LoadData(OnComplte:((ArrayList<Quotes>))->Unit){
        val quotes= ArrayList<Quotes>()
        val lQuotesItem= mutableListOf<QuotesItem>()
        /*quotes.add(Quotes("1","واذا لم يكن من الموت بد فمن العجز ان تكون جبانا",R.drawable.img,"12:22 pm"))
        quotes.add(Quotes("2","أنت اذا اكرمت الكريم ملكته و اذا انت لأكرمت الكريم تمردا",R.drawable.one,"19:22 pm"))
        quotes.add(Quotes("3","لا بقومي شرفت بل شرفوا بي و بنفسي فخرت لا بجدودي",R.drawable.twp,"17:22 pm"))
        quotes.add(Quotes("4","كل ما يتمناه المرء يدركه تجري الرياح بما لا تشتهي السفن",R.drawable.three,"15:22 pm"))
        quotes.add(Quotes("5","عش عزيزا او مت و انت كريم بين طعن القنا و خفق البنود",R.drawable.foor,"16:22 pm"))
        quotes.add(Quotes("6","ذو العقل يشقى بالنعيم بعقله و أخو الجهالة في الشقاوة ينعم",R.drawable.five,"15:22 pm"))
        quotes.add(Quotes("7","عندما يعمل الاخوة معا تتحول الجبال الى مناجم ذهب",R.drawable.six,"16:57 pm"))*/
        quotes.add(Quotes("8","الأنسان الناجح يذهب الى عمله و كأنه ذاهب الى موعد غرامي",R.drawable.seven,"18:35 pm"))
        OnComplte(quotes)
    }*/
    private fun SwipeRefreshLayout.Refrech(){
        this.setOnRefreshListener{
            try {
                /*LoadData{
                    recycler.InitRecyclerView(it)
                }*/
                GetQuotes{
                    recycler.InitRecyclerView(it)
                }
            }catch (ex1:Exception){
                Toast.makeText(activity,ex1.toString(), Toast.LENGTH_LONG).show()
            }
            Handler().postDelayed({ this.isRefreshing=false
            },1000)
        }
    }
    private fun RecyclerView.InitRecyclerView(/*data:ArrayList<Quotes>*/data:ArrayList<Qoutes_firebase>/*item:List<Item>*/) {
        this.apply {
            layoutManager= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
            var adapter= time_line_adapter(activity!!,data,storage_instance,this@PersonFragment)
            this.adapter=adapter
            loading_progresse.visibility=View.INVISIBLE
            AdLoaded=false
        }
    }
    private fun getbyte_array():ByteArray {
        val ByteArray: ByteArray?
        val bitmap= BitmapFactory.decodeResource(this.resources, R.drawable.image)
        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        ByteArray= stream.toByteArray()
        return ByteArray
    }

    override fun OnInsert(
        Quote: Qoutes_firebase, position:Int) {
        try {
            val FavDb= Fav_BD_Helper(activity!!)
            val onemegabyte:Long=10*(1024*1024)
            storage_instance.getReference(Quote.picture).getBytes(onemegabyte).addOnCompleteListener {
                if(it.exception!=null){
                    Toast.makeText(activity,"حدث خطا الرجاء المحاولة مرة اخرى",Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }else if (it.isSuccessful || it.isComplete){
                    FavDb.insertdata(Quote.id,Quote.discription,it.result!!,
                        DateFormat.format("yyyy/MM/dd"
                        ,Quote.date).toString(),DateFormat.format("hh:mm aa",Quote.date).toString(),"1")
                    mresult=it.result!!
                    CheckPermission(it.result!!)
                    (recycler.adapter)!!.notifyItemChanged(position)
                }
            }.addOnFailureListener {
                Toast.makeText(activity,"فشل في الاتصال :تم حفظ الصورة افتراضيا",Toast.LENGTH_LONG).show()
                FavDb.insertdata(Quote.id,Quote.discription,getbyte_array(),
                    DateFormat.format("yyyy/MM/dd"
                        ,Quote.date).toString(),DateFormat.format("hh:mm aa",Quote.date).toString(),Quote.fav.toString())
            }
        }catch (ex1:Exception){
            Toast.makeText(activity,ex1.toString(),Toast.LENGTH_LONG).show()
        }
    }
    override fun OnLoadAd(View: FrameLayout) {
        MobileAds.initialize(activity,"ca-app-pub-3747911693538937~9532358321")
        when(AdLoaded){
            true->{}
            false->{
                val adLoader = AdLoader.Builder(activity!!, "ca-app-pub-3747911693538937/5716757460")
                    .forUnifiedNativeAd { unifiedNativeAd ->
                        val unifiedNativeAdView =
                            layoutInflater.inflate(R.layout.native_ad_layout, null) as UnifiedNativeAdView
                        mapUnifiedNativeAdToLayout(unifiedNativeAd, unifiedNativeAdView)
                        View.removeAllViews()
                        View.addView(unifiedNativeAdView)
                    }
                    .withAdListener(NativeAdListener())
                    .build()
                adLoader.loadAd(AdRequest.Builder().build())
            }
        }

    }
    inner class NativeAdListener:com.google.android.gms.ads.AdListener(){
        override fun onAdLoaded() {
            super.onAdLoaded()
           // adView.tag="1"
            AdLoaded=true
            //Toast.makeText(activity,"is Loading",Toast.LENGTH_LONG).show()
        }
        override fun onAdFailedToLoad(p0: Int) {
            super.onAdFailedToLoad(p0)
            //adView.tag="0"
            AdLoaded=true
            when(p0){
                AdRequest.ERROR_CODE_INTERNAL_ERROR->{
                    //Toast.makeText(activity,"ad loading failed"+ "ERROR_CODE_INTERNAL_ERROR",Toast.LENGTH_LONG).show()
                }
                AdRequest.ERROR_CODE_INVALID_REQUEST->{
                    //Toast.makeText(activity,"ad loading failed"+ "ERROR_CODE_INVALID_REQUEST",Toast.LENGTH_LONG).show()
                }
                AdRequest.ERROR_CODE_NETWORK_ERROR->{
                    //Toast.makeText(activity,"ad loading failed"+ "ERROR_CODE_NETWORK_ERROR",Toast.LENGTH_LONG).show()
                }
                AdRequest.ERROR_CODE_NO_FILL->{
                    //Toast.makeText(activity,"ad loading failed"+ "ERROR_CODE_NO_FILL",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    fun mapUnifiedNativeAdToLayout(adFromGoogle: UnifiedNativeAd, myAdView: UnifiedNativeAdView) {
        val mediaView = myAdView.findViewById<MediaView>(R.id.ad_media)
        myAdView.mediaView = mediaView
       //myAdView.here_add_loading.visibility=View.VISIBLE
        myAdView.headlineView = myAdView.findViewById(R.id.ad_headline)
        myAdView.bodyView = myAdView.findViewById(R.id.ad_body)
        myAdView.callToActionView = myAdView.findViewById(R.id.ad_call_to_action)
        myAdView.iconView = myAdView.findViewById(R.id.ad_icon)
        myAdView.priceView = myAdView.findViewById(R.id.ad_price)
        myAdView.starRatingView = myAdView.findViewById(R.id.ad_rating)
        myAdView.storeView = myAdView.findViewById(R.id.ad_store)
        myAdView.advertiserView = myAdView.findViewById(R.id.ad_advertiser)

        (myAdView.headlineView as TextView).text = adFromGoogle.headline

        if (adFromGoogle.body == null) {
            myAdView.bodyView.visibility = View.GONE
        } else {
            (myAdView.bodyView as TextView).text = adFromGoogle.body
        }

        if (adFromGoogle.callToAction == null) {
            myAdView.callToActionView.visibility = View.GONE
        } else {
            (myAdView.callToActionView as Button).text = adFromGoogle.callToAction
        }

        if (adFromGoogle.icon == null) {
            myAdView.iconView.visibility = View.GONE
        } else {
            (myAdView.iconView as ImageView).setImageDrawable(adFromGoogle.icon.drawable)
        }

        if (adFromGoogle.price == null) {
            myAdView.priceView.visibility = View.GONE
        } else {
            (myAdView.priceView as TextView).text = adFromGoogle.price
        }

        if (adFromGoogle.starRating == null) {
            myAdView.starRatingView.visibility = View.GONE
        } else {
            (myAdView.starRatingView as RatingBar).rating = adFromGoogle.starRating!!.toFloat()
        }

        if (adFromGoogle.store == null) {
            myAdView.storeView.visibility = View.GONE
        } else {
            (myAdView.storeView as TextView).text = adFromGoogle.store
        }

        if (adFromGoogle.advertiser == null) {
            myAdView.advertiserView.visibility = View.GONE
        } else {
            (myAdView.advertiserView as TextView).text = adFromGoogle.advertiser
        }

        myAdView.setNativeAd(adFromGoogle)
    }







    private fun CheckPermission(result: ByteArray){
        if (Build.VERSION.SDK_INT>=23){
            if (activity!!.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
                mresult=result
            }
            else if (activity!!.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                SaveBitmapToDevice(result)
            }
        }else{
            SaveBitmapToDevice(result)
        }
    }
    private fun SaveBitmapToDevice(result: ByteArray) {
        var bitmap=BitmapFactory.decodeByteArray(result,0,result.size)
        var filepath=Environment.getExternalStorageDirectory()
        var directoty=File(Shared_Perfernces(activity!!).GetPath())
        directoty.mkdir()
        var file=File(directoty,System.currentTimeMillis().toString()+ ".jpg")
        try {
            var outputstream=FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputstream)
            outputstream.flush()
            outputstream.close()
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ListenerRegistration{

        }.remove()

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            1->{
                if (permissions.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    SaveBitmapToDevice(mresult)
                }else if(permissions.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_DENIED){
                    Toast.makeText(activity!!,"لم يتم تحميل الصورة الى الهاتف ",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
