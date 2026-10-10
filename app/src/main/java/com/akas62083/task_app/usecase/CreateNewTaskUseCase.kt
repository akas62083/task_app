package com.akas62083.task_app.usecase

import android.util.Log
import com.akas62083.task_app.MainTag
import com.akas62083.task_app.db.entity.SubTagEntity
import com.akas62083.task_app.db.entity.TaskScheduleEntity
import com.akas62083.task_app.db.entity.TaskTagRef
import com.akas62083.task_app.di.DefaultDispatcher
import com.akas62083.task_app.repository.DataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class CreateNewTaskUseCase @Inject constructor(
    private val repository: DataRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        date: List<LocalDate>,
        startTime: Int,
        tags: Set<SubTagEntity>
    ) {
        withContext(defaultDispatcher) {
            val list: List<TaskScheduleEntity> = date.map { date ->
                TaskScheduleEntity(
                    title = title,
                    description = description,
                    date = date,
                    mainTag = MainTag.Task,
                    startTime = startTime
                )
            }
            val id = repository.insertTasks(list)
           // val id = repository.insertTask(
           //     TaskScheduleEntity(
           //         title = title,
           //         description = description,
           //         date = date,
           //         mainTag = MainTag.Task,
           //         startTime = startTime
           //     )
           // )
           // tags.forEach {
           //     repository.insertTaskTagRef(
           //         TaskTagRef(
           //             taskId = id,
           //             tagId = it.id,
           //         )
           //     )
           // }
        }
    }
}