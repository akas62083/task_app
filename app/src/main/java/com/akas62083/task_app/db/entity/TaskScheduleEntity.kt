package com.akas62083.task_app.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akas62083.task_app.MainTag
import com.akas62083.task_app.Status
import java.time.LocalDate

@Entity(tableName = "task_schedule_entity")
data class TaskScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("ta_sc_id")
    val id: Long = 0,
    @ColumnInfo("ta_sc_title")
    val title: String,
    @ColumnInfo("ta_sc_description")
    val description: String = "",
    @ColumnInfo("ta_sc_main")
    val mainTag: MainTag,

    @ColumnInfo("ta_sc_date")
    val date: LocalDate? = null, // date or routineJson. only MainTag.Task or MainTag.Schedule
    @ColumnInfo("ta_sc_routine")
    val routineJson: String? = null, // date or routineJson. only MainTag.Task or MainTag.Schedule

    @ColumnInfo("ta_sc_start_time")
    val startTime: Int? = null, // only MainTag.Task
)