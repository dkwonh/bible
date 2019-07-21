package com.example.bible

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.DateFormat
import java.text.DateFormat.MEDIUM
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DBHelper(context: Context) : SQLiteOpenHelper(context, "bible", null, 1) {
    override fun onCreate(p0: SQLiteDatabase?) {
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addContents(id: Int, contents: String) {
        val db = writableDatabase
        val values = ContentValues()

        values.put("ID", id)
        values.put("CONTENTS", contents)

        db.insert("bible", null, values)
        db.close()

    }

    fun getContents(pageId: Int): ArrayList<String> {
        val db = readableDatabase
        val selectSQL = "SELECT CONTENTS FROM BIBLE WHERE id BETWEEN $pageId AND $pageId+1000"

        val cursor: Cursor = db.rawQuery(selectSQL, null)
        val contents = arrayListOf<String>()
        var lineNum = 1
        while (cursor.moveToNext()) {
            contents.add("\n$lineNum. ${cursor.getString(0)}\n")
            lineNum++
        }
        cursor.close()
        db.close()
        return contents
    }
    /*
    fun getContent(id: Int): String {
        val db = readableDatabase
        val selectSQL = "SELECT CONTENTS FROM BIBLE WHERE id = $id"

        val cursor: Cursor = db.rawQuery(selectSQL, null)
        var content = ""
        val lineNum = id % 1000
        while (cursor.moveToNext()) {
            content += "$lineNum. ${cursor.getString(0)}\n\n"
        }
        cursor.close()
        db.close()
        return content
    }

    fun deleteAllContents() {
        val db = writableDatabase
        val deleteSQL = "DELETE FROM BIBLE"

        db.execSQL(deleteSQL)
        db.close()

    }

    fun createTable() {
        val db = writableDatabase
        val sql = "CREATE TABLE MEMO (ID DATETIME PRIMARY KEY, CONTENTS TEXT)"

        db.execSQL(sql)
        db.close()

    }

    fun dropTable() {
        val db = writableDatabase
        val sql = "DROP TABLE MEMO"

        db.execSQL(sql)
        db.close()

    }*/

    fun pageCalculator(partNum: Int): Int {
        val startNum = partNum * 1000000
        val endNum = (partNum + 1) * 1000000

        val db = readableDatabase
        val selectSQL = "SELECT id FROM BIBLE WHERE id BETWEEN $startNum AND $endNum ORDER BY id DESC LIMIT 1"

        val cursor: Cursor = db.rawQuery(selectSQL, null)
        var contents = ""
        while (cursor.moveToNext()) {
            contents += cursor.getInt(0)
        }
        val pageNum = (contents.toInt() % 1000000) / 1000

        cursor.close()
        db.close()
        return pageNum
    }

    fun lineCalculator(partNum: Int, pageNum: Int): Int {
        val startPartNum = partNum * 1000000
        val startPageNum = pageNum * 1000
        val endPageNum = (pageNum + 1) * 1000

        val db = readableDatabase
        val selectSQL =
            "SELECT id FROM BIBLE WHERE id BETWEEN $startPartNum + $startPageNum AND $startPartNum + $endPageNum ORDER BY id DESC LIMIT 1"

        val cursor: Cursor = db.rawQuery(selectSQL, null)
        var contents = ""
        while (cursor.moveToNext()) {
            contents += cursor.getInt(0)
        }
        val lineNum = contents.toInt() % 1000

        cursor.close()
        db.close()
        return lineNum
    }

    fun addMemo(contents: String) {
        val db = writableDatabase
        val values = ContentValues()
        val format = DateFormat.getDateTimeInstance(MEDIUM,MEDIUM, Locale.KOREA).format(Date())

        values.put("ID", format)
        values.put("CONTENTS", contents)

        db.insert("MEMO", null, values)
        db.close()
    }
    fun addMemo(id : String, contents: String){
        val db = writableDatabase
        val values = ContentValues()

        values.put("CONTENTS",contents)

        db.update("MEMO",values,"ID = '$id'",null)
        db.close()
    }

    fun getAllMemo(): HashMap<String,String> {
        val memo = HashMap<String,String>()
        val db = readableDatabase
        val sql = "SELECT * FROM MEMO ORDER BY ID DESC"

        val cursor: Cursor = db.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            memo[cursor.getString(0)] = cursor.getString(1)
        }

        cursor.close()
        db.close()
        return memo
    }

    /*fun getAllMemoID() : ArrayList<String>{
        val memoId = ArrayList<String>()

        val db = readableDatabase
        val sql = "SELECT ID FROM MEMO"

        val cursor: Cursor = db.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            memoId.add(cursor.getString(0))
        }

        cursor.close()
        db.close()
        return memoId

    }

    fun deleteAllMemo() {
        val db = writableDatabase

        db.delete("MEMO",null,null)
        db.close()
    }*/

    fun deleteMemo(id: String) {
        val db = writableDatabase
        val str = "%$id%"
        val sql = "DELETE FROM MEMO WHERE ID LIKE '$str'"

        db.execSQL(sql)
        db.close()
    }
}