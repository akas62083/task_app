package com.akas62083.task_app.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.akas62083.task_app.db.entity.SubTagEntity
import com.akas62083.task_app.db.entity.TagWithTasks
import com.akas62083.task_app.db.entity.TaskLogEntity
import com.akas62083.task_app.db.entity.TaskScheduleEntity
import com.akas62083.task_app.db.entity.TaskTagRef
import com.akas62083.task_app.db.entity.TaskWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    suspend fun insertTask(task: TaskScheduleEntity): Long
    @Insert
    suspend fun insertTasks(tasks: List<TaskScheduleEntity>): List<Long>
    @Delete
    suspend fun deleteTask(task: TaskScheduleEntity)
    @Update
    suspend fun updateTask(task: TaskScheduleEntity)
    @Query("SELECT * FROM task_schedule_entity")
    fun getAllTasks(): Flow<List<TaskScheduleEntity>>
    @Query("SELECT * FROM task_schedule_entity WHERE ta_sc_id = :id")
    suspend fun getTaskById(id: Long): TaskScheduleEntity?

    @Insert
    suspend fun insertSubTag(tag: SubTagEntity): Long
    @Delete
    suspend fun deleteSubTag(tag: SubTagEntity)
    @Update
    suspend fun updateSubTag(tag: SubTagEntity)
    @Query("SELECT * FROM sub_tag_entity")
    fun getAllSubTags(): Flow<List<SubTagEntity>>
    @Query("SELECT * FROM sub_tag_entity WHERE sub_id = :id")
    suspend fun getSubTagById(id: Long): SubTagEntity?

    @Insert
    suspend fun insertTaskLog(log: TaskLogEntity): Long
    @Delete
    suspend fun deleteTaskLog(log: TaskLogEntity)
    @Update
    suspend fun updateTaskLog(log: TaskLogEntity)
    @Query("select * from task_log_entity")
    fun getAllTaskLogs(): Flow<List<TaskLogEntity>>
    @Query("select * from task_log_entity where log_id = :id")
    suspend fun getTaskLogById(id: Long): TaskLogEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTagRef(ref: TaskTagRef)
    @Delete
    suspend fun deleteTaskTagRef(ref: TaskTagRef)

    @Transaction
    @Query("select * from sub_tag_entity where sub_id = :id limit 1")
    suspend fun getTagAndTasksById(id: Long): TagWithTasks?
    @Transaction
    @Query("select * from task_schedule_entity where ta_sc_id = :id limit 1")
    suspend fun getTaskAndTagsById(id: Long): TaskWithTags?
    @Transaction
    @Query("select * from sub_tag_entity order by sub_title asc")
    fun getAllTagWithTasks(): Flow<List<TagWithTasks>>
    @Transaction
    @Query("select * from task_schedule_entity order by ta_sc_title asc")
    fun getAllTaskWithTags(): Flow<List<TaskWithTags>>
}