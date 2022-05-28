package kuesji.link_eye

import android.content.Context
import androidx.room.Room
import kuesji.link_eye.db.AppDatabase
import kuesji.link_eye.db.HistoryModel

class HistoryHelper(context: Context) {
    private var db: AppDatabase

    init {
        db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "app.db"
        ).build()
    }

    fun insert(content: String) {
        db.historyDao().insert(HistoryModel(System.currentTimeMillis() / 1000, content))
    }

    fun list(): List<HistoryModel> {
        return db.historyDao().getAllByDateDesc()
    }

    fun search(query: String): List<HistoryModel> {
        return db.historyDao().search(query)
    }

    fun delete(id: Int) {
        db.historyDao().delete(id)
    }
}