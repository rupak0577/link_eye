package kuesji.link_eye.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryModel(
    val date: Long,
    val content: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}