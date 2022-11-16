package com.rajchenbergstudios.hoygenda.data.today

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "today_table")
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Today(
    val content: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    val type: TodayType = TodayType.TASK,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val createdDateFormat: String
        get() = DateFormat.getDateInstance().format(created)
}
