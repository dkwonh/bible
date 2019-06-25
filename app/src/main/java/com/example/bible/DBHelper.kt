package com.example.bible

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context) : SQLiteOpenHelper(context, "bible", null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val createTable = "CREATE TABLE BIBLE" +
                "(ID Integer PRIMARY KEY," +
                "CONTENTS TEXT)"
        //p0?.execSQL(createTable)
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

    fun getContents(pageid : Int) : String {
        val db = this.readableDatabase
        val selectSQL = "SELECT CONTENTS FROM BIBLE WHERE id BETWEEN $pageid AND $pageid+1000"

        val cursor : Cursor = db.rawQuery(selectSQL, null)
        var contents = ""
        var lineNum = 1
        while(cursor.moveToNext()){
            contents += "$lineNum. ${cursor.getString(0)}\n\n"
            lineNum++
        }
        contents += "\n\n"
        cursor.close()
        db.close()
        return contents
    }

    fun getContent(id : Int ) : String {
        val db = this.readableDatabase
        val selectSQL = "SELECT CONTENTS FROM BIBLE WHERE id = $id"

        val cursor : Cursor = db.rawQuery(selectSQL, null)
        var content = ""
        val lineNum = id%1000
        while(cursor.moveToNext()){
            content += "$lineNum. ${cursor.getString(0)}\n\n"
        }
        cursor.close()
        db.close()
        return content
    }

    fun deleteAllContents(){
        val db = this.writableDatabase
        val deleteSQL = "DELETE FROM BIBLE"

        db.execSQL(deleteSQL)
    }

    fun pageCalculator(partNum : Int) : Int{
        val startNum = partNum * 1000000
        val endNum = (partNum+1) * 1000000

        val db = this.readableDatabase
        val selectSQL = "SELECT id FROM BIBLE WHERE id BETWEEN $startNum AND $endNum ORDER BY id DESC LIMIT 1"

        val cursor : Cursor = db.rawQuery(selectSQL, null)
        var contents = ""
        while(cursor.moveToNext()){
            contents += cursor.getInt(0)
        }
        val pageNum = (contents.toInt()%1000000)/1000

        cursor.close()
        db.close()
        return pageNum
    }

    fun lineCalculator(partNum: Int, pageNum: Int) : Int{
        val startPartNum = partNum * 1000000
        val startPageNum = pageNum * 1000
        val endPageNum = (pageNum+1) * 1000

        val db = this.readableDatabase
        val selectSQL = "SELECT id FROM BIBLE WHERE id BETWEEN $startPartNum + $startPageNum AND $startPartNum + $endPageNum ORDER BY id DESC LIMIT 1"

        val cursor : Cursor = db.rawQuery(selectSQL, null)
        var contents = ""
        while(cursor.moveToNext()){
            contents += cursor.getInt(0)
        }
        val lineNum = contents.toInt()%1000

        cursor.close()
        db.close()
        return lineNum
    }
}