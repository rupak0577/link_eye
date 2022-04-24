package kuesji.link_eye

import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import kuesji.link_eye.HistoryHelper.HistoryModel
import java.util.ArrayList

class HistoryHelper(private val context: Context) {
    inner class HistoryModel {
        var id = 0
        var epoch = 0
        var content = ""

        constructor() {}
        constructor(id: Int, epoch: Int, content: String) {
            this.id = id
            this.epoch = epoch
            this.content = content
        }
    }

    var database: SQLiteDatabase
    fun close() {
        database.close()
    }

    fun insert(content: String?) {
        val values = ContentValues()
        values.put("date", System.currentTimeMillis() / 1000)
        values.put("content", content)
        database.insert("history", null, values)
    }

    fun list(): List<HistoryModel> {
        val result: MutableList<HistoryModel> = ArrayList()
        val cursor =
            database.rawQuery("select id,date,content from history order by date desc", null)
        if (cursor.moveToFirst()) {
            do {
                result.add(HistoryModel(cursor.getInt(0), cursor.getInt(1), cursor.getString(2)))
            } while (cursor.moveToNext())
        }
        return result
    }

    fun search(query: String): List<HistoryModel> {
        val result: MutableList<HistoryModel> = ArrayList()
        val cursor = database.rawQuery(
            "select id,date,content from history where content like ? order by date desc", arrayOf(
                "%$query%"
            )
        )
        if (cursor.moveToFirst()) {
            do {
                result.add(HistoryModel(cursor.getInt(0), cursor.getInt(1), cursor.getString(2)))
            } while (cursor.moveToNext())
        }
        return result
    }

    fun delete(id: Int) {
        database.delete("history", "id=?", arrayOf(id.toString()))
    }

    fun clear() {
        database.execSQL("delete from history")
    }

    init {
        database = context.openOrCreateDatabase("app.db", Context.MODE_PRIVATE, null)
        database.execSQL("create table if not exists history ( id integer primary key, date integer not null, content text not null );")
    }
}