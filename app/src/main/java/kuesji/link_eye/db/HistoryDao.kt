package kuesji.link_eye.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY date DESC")
    fun getAllByDateDesc(): List<HistoryModel>

    @Query("SELECT * FROM history WHERE content LIKE :content ORDER BY date DESC")
    fun search(content: String): List<HistoryModel>

    @Insert
    fun insert(item: HistoryModel)

    @Delete
    fun delete(item: HistoryModel)

    @Query("DELETE FROM history WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM history")
    fun clear()
}
