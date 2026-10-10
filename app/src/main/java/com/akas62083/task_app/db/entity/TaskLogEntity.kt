package com.akas62083.task_app.db.entity

import androidx.activity.SystemBarStyle.Companion.auto
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.akas62083.task_app.Status
import java.time.LocalDate

@Entity(
    tableName = "task_log_entity",
    indices = [
        Index(value = ["log_task_id", "log_date"], unique = true),
        Index(value = ["log_date"])
    ]
)
data class TaskLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("log_id")
    val id: Long = 0,
    @ColumnInfo("log_task_id")
    val taskId: Long,
    @ColumnInfo("log_date")
    val date: LocalDate,
    @ColumnInfo("log_status")
    val status: Status
)