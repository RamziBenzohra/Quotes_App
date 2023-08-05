package com.app.quote.app.quotesapp.Client.Helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


val DATABASE_NAME="Fav.db"
val TABLE_NAME="Fav"

val col_2="discription"
val col_3="picture"
val col_4="date"
val col_6="fav"
val col_1="ID"
val col_5="time"



class Fav_BD_Helper(context:Context):SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("create table $TABLE_NAME (ID TEXT ,"
                +"discription TEXT ,picture BLOB ,date TEXT,time TEXT ,fav TEXT )")


    }
    fun insertdata(ID:String,discription:String,picture:ByteArray,date:String,time:String,fav:String){
        var db=this.writableDatabase
        var content=ContentValues()
        content.put(col_1,ID)
        content.put(col_2,discription)
        content.put(col_3,picture)
        content.put(col_4,date)
        content.put(col_5,time)
        content.put(col_6,fav)
        db.insert(TABLE_NAME,null,content)
    }
    val allData:Cursor
        get(){
            val db=this.writableDatabase
            val res=db.rawQuery("select * from $TABLE_NAME",null)
            return res
            res.close()

        }

    fun GetIsAdded(ID:String):Boolean{
        val db=this.writableDatabase
        val res=db.rawQuery("select $col_6 from $TABLE_NAME${" where $col_1 = ${"?"}"}", arrayOf(ID))
        var bol=false
        while (res.moveToNext()){
            bol= res.getString(res.getColumnIndex(col_6))=="1"
        }
        res.close()
        return bol
    }

    fun deletdata(id:String){
        val db=this.writableDatabase
        db.delete(TABLE_NAME,"ID = ?", arrayOf(id))
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS$TABLE_NAME")
    }

}