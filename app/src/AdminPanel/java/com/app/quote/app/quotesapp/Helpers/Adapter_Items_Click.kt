package com.app.quote.app.quotesapp.Helpers

import android.widget.FrameLayout
import com.app.quote.app.quotesapp.models.Qoutes_firebase
import java.util.*

interface Adapter_Items_Click {
    fun OnInsert(Quote: Qoutes_firebase, position:Int){

    }
    fun OnQuote(ID:String, discription:String, pic:String, date: Date){

    }
    fun Ondelete(Quote: Qoutes_firebase, position:Int){

    }
    fun OnClicked(Quote: Qoutes_firebase, position:Int){}
    fun OnDialogUpdate(Quote: Qoutes_firebase, position:Int){}
    fun OnLoadAd(View: FrameLayout){

    }
    fun OnFavClicked(ID:String){}
}