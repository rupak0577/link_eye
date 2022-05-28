package kuesji.link_eye.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HistoryModel::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
