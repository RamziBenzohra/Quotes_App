package com.app.quote.app.quotesapp.Client.adapters

import com.app.quote.app.quotesapp.Client.GlideHelper.GlideApp
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar

import com.app.quote.app.quotesapp.Client.Helpers.Adapter_Items_Click
import com.app.quote.app.quotesapp.Client.models.Qoutes_firebase
import com.app.quote.app.quotesapp.R

import com.google.firebase.storage.FirebaseStorage

import java.util.ArrayList

class ImagesAdapter(
    var Contex: Context,
    var quotes: ArrayList<Qoutes_firebase>,
    var storageInstance: FirebaseStorage, var onClick: Adapter_Items_Click
) : RecyclerView.Adapter<ImagesAdapter.myImagesViewHolder>() {
    override fun onBindViewHolder(p0: myImagesViewHolder, p1: Int) {
        p0.images_loading_progresse.visibility=View.VISIBLE
        GlideApp.with(Contex).load(storageInstance.getReference(quotes[p1].picture)).placeholder(R.drawable.image).into(p0.images)
        p0.images_loading_progresse.visibility=View.INVISIBLE

        p0.images.setOnClickListener {
            onClick.OnClicked(quotes[p1],p1)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): myImagesViewHolder {
        var view=LayoutInflater.from(Contex).inflate(R.layout.images_model,null,false)
        return myImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
       return quotes.size
    }


    class myImagesViewHolder(var view:View):RecyclerView.ViewHolder(view){
        var images=view.findViewById<ImageView>(R.id.image_model)
        var images_loading_progresse=view.findViewById<ProgressBar>(R.id.image_loading_progress)
    }

}
