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

class DataRepositoryImpl @Inject constructor(
    val dao: Dao
): DataRepository  {
    override suspend fun insertTask(task: TaskScheduleEntity): Long = dao.insertTask(task)
    override suspend fun insertTasks(tasks: List<TaskScheduleEntity>): List<Long> = dao.insertTasks(tasks)
    override suspend fun deleteTask(task: TaskScheduleEntity) = dao.deleteTask(task)
    override suspend fun updateTask(task: TaskScheduleEntity) = dao.updateTask(task)
    override fun getAllTasks(): Flow<List<TaskScheduleEntity>> = dao.getAllTasks()
    override suspend fun getTaskById(id: Long): TaskScheduleEntity? = dao.getTaskById(id)

    override suspend fun insertSubTag(tag: SubTagEntity): Long = dao.insertSubTag(tag)
    override suspend fun deleteSubTag(tag: SubTagEntity) = dao.deleteSubTag(tag)
    override suspend fun updateSubTag(tag: SubTagEntity) = dao.updateSubTag(tag)
    override fun getAllSubTags(): Flow<List<SubTagEntity>> = dao.getAllSubTags()
    override suspend fun getSubTagById(id: Long): SubTagEntity? = dao.getSubTagById(id)

    override suspend fun insertTaskLog(log: TaskLogEntity): Long = dao.insertTaskLog(log)
    override suspend fun deleteTaskLog(log: TaskLogEntity) = dao.deleteTaskLog(log)
    override suspend fun updateTaskLog(log: TaskLogEntity) = dao.updateTaskLog(log)
    override fun getAllTaskLogs(): Flow<List<TaskLogEntity>> = dao.getAllTaskLogs()
    override suspend fun getTaskLogById(id: Long): TaskLogEntity? = dao.getTaskLogById(id)

    override suspend fun insertTaskTagRef(ref: TaskTagRef) = dao.insertTaskTagRef(ref)
    override suspend fun deleteTaskTagRef(ref: TaskTagRef) = dao.deleteTaskTagRef(ref)

    override suspend fun getTagAndTasksById(id: Long): TagWithTasks? = dao.getTagAndTasksById(id)
    override fun getAllTagAndTasks(): Flow<List<TagWithTasks>> = dao.getAllTagWithTasks()
    override suspend fun getTaskAndTagsById(id: Long): TaskWithTags? = dao.getTaskAndTagsById(id)
    override fun getAllTaskAndTags(): Flow<List<TaskWithTags>> = dao.getAllTaskWithTags()
}
