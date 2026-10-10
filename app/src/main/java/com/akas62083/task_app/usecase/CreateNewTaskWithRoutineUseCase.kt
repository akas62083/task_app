package com.akas62083.task_app.usecase

import com.akas62083.task_app.MainTag
import com.akas62083.task_app.Rule
import com.akas62083.task_app.db.entity.SubTagEntity
import com.akas62083.task_app.db.entity.TaskScheduleEntity
import com.akas62083.task_app.db.entity.TaskTagRef
import com.akas62083.task_app.di.DefaultDispatcher
import com.akas62083.task_app.repository.DataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import javax.inject.Inject

class CreateNewTaskWithRoutineUseCase @Inject constructor(
    private val repository: DataRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        routine: Rule,
        tags: Set<SubTagEntity>
    ) {
        withContext(defaultDispatcher) {
            val id = repository.insertTask(
                TaskScheduleEntity(
                    title = title,
                    description = description,
                    mainTag = MainTag.Task,
                    routineJson = Json.encodeToString(routine)
                )
            )
            tags.forEach {
                repository.insertTaskTagRef(
                    TaskTagRef(
                        taskId = id,
                        tagId = it.id,
                    )
                )
            }
        }
    }
}