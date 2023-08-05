package com.app.quote.app.quotesapp.Fragments


import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.app.quote.app.quotesapp.R
import com.app.quote.app.quotesapp.adapters.fav_adapter
import com.app.quote.app.quotesapp.Helpers.Adapter_Items_Click
import com.app.quote.app.quotesapp.Helpers.Fav_BD_Helper
import com.app.quote.app.quotesapp.models.Quotes

import java.io.ByteArrayInputStream


class FavFragment : Fragment(), Adapter_Items_Click {

    lateinit var recycler:RecyclerView
    lateinit var AllFav:Cursor
    lateinit var Swipe:SwipeRefreshLayout
    lateinit var DB: Fav_BD_Helper
    lateinit var empty_fav:ConstraintLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_fav, container, false)
        DB= Fav_BD_Helper(activity!! as Context)
        recycler=view.findViewById(R.id.fav_recycler)
        Swipe=view.findViewById(R.id.swipe_fav)
        empty_fav=view.findViewById(R.id.empty_fav)
        LoadData(DB){
            recycler.InitRecyclerView(it)

        }
        Swipe.Refrech()

        return view
    }
    inner class DataObserver():RecyclerView.AdapterDataObserver(){
        override fun onChanged() {
            //Toast.makeText(activity,"changed",Toast.LENGTH_LONG).show()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            //Toast.makeText(contex!!,"removed + item count${itemCount}",Toast.LENGTH_LONG).show()
            EditeView(itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {

        }
    }
    private fun EditeView(Count:Int?){
        when(Count){
            0->{
               empty_fav.visibility=View.VISIBLE
            }
            else->{
                empty_fav.visibility=View.GONE
            }
        }
    }
    private fun LoadData(DB: Fav_BD_Helper, OnComplte:(Quotes:ArrayList<Quotes>)->Unit){
        AllFav=DB.allData
        val favQuotes= ArrayList<Quotes>()
        while (AllFav.moveToNext()){
            var fav=AllFav.getString(5)=="1"
            val byte_array=AllFav.getBlob(2)
            val imageStream = ByteArrayInputStream(byte_array)
            val theImage = BitmapFactory.decodeStream(imageStream)
            favQuotes.add(Quotes(AllFav.getString(0),AllFav.getString(1),theImage,AllFav.getString(3),AllFav.getString(4),fav))
        }
        AllFav.close()
        OnComplte(favQuotes)
    }
    private fun SwipeRefreshLayout.Refrech(){
        this.setOnRefreshListener{
            try {
               LoadData(DB){
                   recycler.InitRecyclerView(it)
               }
            }catch (ex1:Exception){
                Toast.makeText(activity,ex1.toString(),Toast.LENGTH_LONG).show()
            }
            Handler().postDelayed({ this.isRefreshing=false
            },1000)
        }
    }
    private fun RecyclerView.InitRecyclerView(data:ArrayList<Quotes>) {
        this.apply {
            layoutManager= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
            var adapter= fav_adapter(activity!!,data,this@FavFragment)
            this.adapter=adapter
            this.adapter?.registerAdapterDataObserver(DataObserver())
            EditeView((this.adapter as fav_adapter).itemCount)
            //EditeView(this@InitRecyclerView.adapter?.itemCount)
        }
    }


    override fun OnFavClicked(ID: String) {
        var FavDb= Fav_BD_Helper(activity!!)
        FavDb.deletdata(ID)

    }
    override fun onDestroyView() {
        //recycler.adapter?.unregisterAdapterDataObserver(DataObserver())
        return super.onDestroyView()
    }

    override fun onDestroy() {
//        recycler.adapter?.unregisterAdapterDataObserver(DataObserver())
        super.onDestroy()
    }
}
