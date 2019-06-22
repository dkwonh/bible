package com.example.bible

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "bible", null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val createTable = "CREATE TABLE BIBLE" +
                "(ID Integer PRIMARY KEY," +
                "CONTENTS TEXT)"
        p0?.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addContents(id: Int, contents: String) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put("ID", id)
        values.put("CONTENTS", contents)

        db.insert("BIBLE", null, values)
    }

    fun getContents(id : Int ) : String {
        val db = this.readableDatabase
        val selectSQL = "SELECT CONTENTS FROM BIBLE WHERE id = $id"

        val cursor : Cursor = db.rawQuery(selectSQL, null)
        var contents = ""
        while(cursor.moveToNext()){
            contents += cursor.getString(0)
        }
        return contents
    }

    fun deleteAllContents(){
        val db = this.writableDatabase
        val deleteSQL = "DELETE FROM BIBLE"

        db.execSQL(deleteSQL)
    }
}