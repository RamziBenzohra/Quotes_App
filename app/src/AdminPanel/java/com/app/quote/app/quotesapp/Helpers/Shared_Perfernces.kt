package com.app.quote.app.quotesapp.Helpers

import android.content.Context
import android.content.SharedPreferences


data class Shared_Perfernces(var Contex:Context) {

    val Shared_Name="TokenShared"
    val value_key="Token"
    val Subscription="Subscription"
    val Storage_Path="Storage_Path"
    val shared:SharedPreferences
    get() {
       return this.Contex.getSharedPreferences(this.Shared_Name,Context.MODE_PRIVATE)
    }
    fun SaveValue(subscription:Boolean){
        shared.edit().putBoolean(this.Subscription,subscription).apply()
    }
    fun SaveValue(Token:String){
        shared.edit().putString(this.value_key,Token).apply()
    }
    fun SavePath(Path:String){
        shared.edit().putString(this.Storage_Path,Path).apply()
    }
    fun GetSubscription():Boolean{
       return shared.getBoolean(this.Subscription,true)
    }
    fun GetToken():String{
        return shared.getString(this.value_key,"")!!
    }
    fun GetPath():String{
        return shared.getString(this.Storage_Path,"/storage/emulated/0/قوال خلدها التاريخ")!!
    }
}
