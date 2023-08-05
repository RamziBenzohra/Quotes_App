package com.app.quote.app.quotesapp.adapters


import com.app.quote.app.quotesapp.Helpers.Adapter_Items_Click
import com.app.quote.app.quotesapp.Helpers.Fav_BD_Helper
import android.content.Context

import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.app.quote.app.quotesapp.R
import com.app.quote.app.quotesapp.models.Qoutes_firebase
import com.bumptech.glide.Glide

import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView


class time_line_adapter(var contex:Context,var data:ArrayList<Qoutes_firebase>
                        ,var storage_instance: FirebaseStorage
                        , var Adapter_Items_Click: Adapter_Items_Click
):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var hepler = Fav_BD_Helper(contex)
    var type=""
    var TYPE_ADS=1
    var TYPE_QUOTES=0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        var viewholde:RecyclerView.ViewHolder?=null
        when(p1){
            TYPE_QUOTES->{
                viewholde= QuotesHolder(LayoutInflater.from(contex).inflate(R.layout.timeline,null,false))
                return viewholde
            }
            TYPE_ADS->{
                viewholde=
                    AdsHolder(LayoutInflater.from(contex).inflate(R.layout.ad_layout,null,false))
                return viewholde
            }

        }
        Log.d("holder_type",data[p1].ViewType.toString())
        return viewholde!!
    }
    override fun getItemViewType(position: Int): Int {
        return data[position].ViewType
    }
    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("holder",data[position].ViewType.toString())
        Log.d("holder_view_type",holder.itemViewType.toString())
        when (holder.itemViewType){
            TYPE_QUOTES->{
                holder as QuotesHolder
                holder.saving_progress.visibility=View.INVISIBLE
                holder.deleting_progress.visibility=View.INVISIBLE
                holder.delete_iv.isEnabled=true
                holder.disc.text=data[position].discription
                holder.date.text= DateFormat.format("hh:mm aa",data[position].date)
                holder.date_tv.text=DateFormat.format("yyyy/MM/dd ",data[position].date)
                val isadded=hepler.GetIsAdded(data[position].id)
                when(isadded){
                    false->{ Glide.with(contex).load(R.drawable.ic_favorite_border_black_24dp).load(R.drawable.ic_favorite_border_black_24dp).into(holder.fav)}
                    true->{ Glide.with(contex).load(R.drawable.ic_favorite_black_24dp).load(R.drawable.ic_favorite_border_black_24dp).into(holder.fav)}
                }
                holder.progress.visibility=View.VISIBLE

                Log.d("picture reference",data[position].picture)
                Glide.with(contex).load(storage_instance.getReference(data[position].picture)).load(R.drawable.image).into(holder.quotes)
                holder.progress.visibility=View.INVISIBLE
                holder.fav.setOnClickListener {
                    when(isadded){
                        false->{
                            holder.saving_progress.visibility=View.VISIBLE
                            Adapter_Items_Click.OnInsert(data[position],position)
                        }
                        true->{
                            Snackbar.make(it,"Already Added",Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                holder.update_iv.setOnClickListener {
                    Adapter_Items_Click.OnQuote(data[position].id
                        ,data[position].discription,data[position].picture,data[position].date)
                }
                holder.delete_iv.setOnClickListener {
                    it.isEnabled=false
                    holder.deleting_progress.visibility=View.VISIBLE
                    Adapter_Items_Click.Ondelete(data[position]
                        ,position)
                }
                if (position!=0){
                    if (data[position-1].ViewType==TYPE_ADS){
                        if (DateFormat.format("yyyy/MM/dd",data[position].date)==DateFormat.format("yyyy/MM/dd",data[position-2].date)){
                            holder.table.visibility=View.GONE
                        }else{
                            holder.table.visibility=View.VISIBLE
                        }
                    }else if(data[position-1].ViewType==TYPE_QUOTES){
                        if (DateFormat.format("yyyy/MM/dd",data[position].date)==DateFormat.format("yyyy/MM/dd",data[position-1].date)){
                            holder.table.visibility=View.GONE
                        }else{
                            holder.table.visibility=View.VISIBLE
                        }
                    }

                }else{
                    holder.table.visibility=View.VISIBLE
                }
            }
            TYPE_ADS->{
                holder as AdsHolder
                Adapter_Items_Click.OnLoadAd(holder.Native_frame_layout)
            }
        }
    }
    class QuotesHolder(view:View):RecyclerView.ViewHolder(view) {
        var image=view.findViewById<CircleImageView>(R.id.picture)
        var date=view.findViewById<TextView>(R.id.time_tv)
        var disc=view.findViewById<TextView>(R.id.discription)
        var quotes=view.findViewById<ImageView>(R.id.quotes)
        var timer=view.findViewById<ImageView>(R.id.timer)//fav_iv
        var fav=view.findViewById<CircleImageView>(R.id.fav_iv)
        var update_iv=view.findViewById<CircleImageView>(R.id.update_iv)
        var time=view.findViewById<TextView>(R.id.date_tv)//table
        var table=view.findViewById<LinearLayout>(R.id.table)
        var progress=view.findViewById<ProgressBar>(R.id.image_progress)
        var date_tv=view.findViewById<TextView>(R.id.date_tv)//saving_progress
        var saving_progress=view.findViewById<ProgressBar>(R.id.saving_progress)//update_iv//delete_iv
        var delete_iv=view.findViewById<CircleImageView>(R.id.delete_iv)//deleting_progress
        var deleting_progress=view.findViewById<ProgressBar>(R.id.deleting_progress)
    }
    class AdsHolder(view: View):RecyclerView.ViewHolder(view){
        var Native_frame_layout=view.findViewById<FrameLayout>(R.id.Native_frame_layout)
    }
}
