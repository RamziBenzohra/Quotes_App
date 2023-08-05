package com.app.quote.app.quotesapp.Client.adapters

import com.app.quote.app.quotesapp.Client.GlideHelper.GlideApp
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.app.quote.app.quotesapp.Client.Helpers.Adapter_Items_Click
import com.app.quote.app.quotesapp.Client.models.Quotes

import com.app.quote.app.quotesapp.Client.Helpers.Fav_BD_Helper

import com.app.quote.app.quotesapp.R
import de.hdodenhof.circleimageview.CircleImageView

class fav_adapter(var contex:Context, var data:ArrayList<Quotes>, var FavListener: Adapter_Items_Click):RecyclerView.Adapter<fav_adapter.QuotesHolder>(){
    var hepler: Fav_BD_Helper
    init {
        hepler= Fav_BD_Helper(contex)
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): QuotesHolder {
        return QuotesHolder(LayoutInflater.from(contex).inflate(R.layout.timeline,null,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: QuotesHolder, position: Int) {
       try {
           holder.disc.text=data[position].discription
           holder.date.text=data[position].time
           holder.date_tv.text=data[position].date
           holder.saving_progress.visibility=View.INVISIBLE

           holder.view3.visibility=View.GONE

           var isadded=hepler.GetIsAdded(data[position].id)
           when(isadded){
               true->{
                   GlideApp.with(contex).load(R.drawable.ic_favorite_black_24dp).into(holder.fav)}
               else->{}
           }
           holder.progress.visibility=View.VISIBLE
           holder.quotes.setImageBitmap(data[position].picture)
           holder.progress.visibility=View.INVISIBLE
           holder.fav.setOnClickListener {
               try{
                   var id=data[position].id
                   data.removeAt(position)
                   notifyItemRemoved(position)
                   //notifyItemRangeChanged(position,data.size)
                   notifyItemRangeRemoved(position,data.size)
                   FavListener.OnFavClicked(id)
                   Snackbar.make(it,"Removes from favorite",Snackbar.LENGTH_LONG).show()
               }catch(ex:Exception){
                  // Toast.makeText(contex,"message${ex.message} cause${ex.cause} localazedmessage${ex.localizedMessage}",Toast.LENGTH_LONG).show()
               }

           }
           if (position!=0){
               if (data[position].date==data[position-1].date){
                   holder.table.visibility=View.GONE
               }else{
                   holder.table.visibility=View.VISIBLE
               }
           }else{
               holder.table.visibility=View.VISIBLE
           }
       }catch (r:Exception){
           Toast.makeText(contex,r.toString(), Toast.LENGTH_LONG).show()
       }

    }

    class QuotesHolder(view:View):RecyclerView.ViewHolder(view) {
        var table=view.findViewById<LinearLayout>(R.id.table)
        var image=view.findViewById<CircleImageView>(R.id.picture)
        var date=view.findViewById<TextView>(R.id.time_tv)
        var disc=view.findViewById<TextView>(R.id.discription)
        var quotes=view.findViewById<ImageView>(R.id.quotes)
        var timer=view.findViewById<ImageView>(R.id.timer)//fav_iv
        var fav=view.findViewById<CircleImageView>(R.id.fav_iv)
        var time=view.findViewById<TextView>(R.id.date_tv)
        var progress=view.findViewById<ProgressBar>(R.id.image_progress)
        var date_tv=view.findViewById<TextView>(R.id.date_tv)//saving_progress
        var saving_progress=view.findViewById<ProgressBar>(R.id.saving_progress)
        var view3=view.findViewById<View>(R.id.view3)
    }
}