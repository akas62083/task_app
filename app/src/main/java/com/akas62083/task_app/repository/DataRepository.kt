package com.akas62083.task_app.repository

import com.akas62083.task_app.db.Dao
import com.akas62083.task_app.db.entity.SubTagEntity
import com.akas62083.task_app.db.entity.TagWithTasks
import com.akas62083.task_app.db.entity.TaskLogEntity
import com.akas62083.task_app.db.entity.TaskScheduleEntity
import com.akas62083.task_app.db.entity.TaskTagRef
import com.akas62083.task_app.db.entity.TaskWithTags
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DataRepository {
    suspend fun insertTask(task: TaskScheduleEntity): Long
    suspend fun insertTasks(tasks: List<TaskScheduleEntity>): List<Long>
    suspend fun deleteTask(task: TaskScheduleEntity)
    suspend fun updateTask(task: TaskScheduleEntity)
    fun getAllTasks(): Flow<List<TaskScheduleEntity>>
    suspend fun getTaskById(id: Long): TaskScheduleEntity?

    suspend fun insertSubTag(tag: SubTagEntity): Long
    suspend fun deleteSubTag(tag: SubTagEntity)
    suspend fun updateSubTag(tag: SubTagEntity)
    fun getAllSubTags(): Flow<List<SubTagEntity>>
    suspend fun getSubTagById(id: Long): SubTagEntity?

    suspend fun insertTaskLog(log: TaskLogEntity): Long
    suspend fun deleteTaskLog(log: TaskLogEntity)
    suspend fun updateTaskLog(log: TaskLogEntity)
    fun getAllTaskLogs(): Flow<List<TaskLogEntity>>
    suspend fun getTaskLogById(id: Long): TaskLogEntity?


    suspend fun insertTaskTagRef(ref: TaskTagRef)
    suspend fun deleteTaskTagRef(ref: TaskTagRef)

    suspend fun getTagAndTasksById(id: Long): TagWithTasks?
    fun getAllTagAndTasks(): Flow<List<TagWithTasks>>
    suspend fun getTaskAndTagsById(id: Long): TaskWithTags?
    fun getAllTaskAndTags(): Flow<List<TaskWithTags>>
}
