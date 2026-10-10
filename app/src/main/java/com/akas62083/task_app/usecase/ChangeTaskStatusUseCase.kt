package com.akas62083.task_app.usecase

import com.akas62083.task_app.Status
import com.akas62083.task_app.TaskAndSchedule
import com.akas62083.task_app.db.entity.TaskLogEntity
import com.akas62083.task_app.repository.DataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class ChangeTaskStatusUseCase @Inject constructor(
    private val repository: DataRepository,
) {
    suspend operator fun invoke(
        task: TaskAndSchedule
    ) {
        val logs = repository.getAllTaskLogs().take(1)
        when(task) {
            is TaskAndSchedule.Task -> {
                val log = logs.first().firstOrNull{ it.taskId == task.id && it.date == task.date }
                if(log != null) {
                    repository.updateTaskLog(
                        TaskLogEntity(
                            id = log.id,
                            taskId = log.taskId,
                            date = log.date,
                            status = if(log.status == Status.NotDone) Status.Done else Status.NotDone
                        )
                    )
                } else {
                    repository.insertTaskLog(
                        TaskLogEntity(
                            taskId = task.id,
                            date = task.date,
                            status = Status.Done
                        )
                    )
                }
            }
            is TaskAndSchedule.Schedule -> {
                val log = logs.first().firstOrNull{ it.taskId == task.id && it.date == task.date }
                if(log != null) {
                    repository.updateTaskLog(
                        TaskLogEntity(
                            id = log.id,
                            taskId = log.taskId,
                            date = log.date,
                            status = if(log.status == Status.NotDone) Status.Done else Status.NotDone
                        )
                    )
                } else {
                    repository.insertTaskLog(
                        TaskLogEntity(
                            taskId = task.id,
                            date = task.date,
                            status = Status.Done
                        )
                    )
                }
            }
            is TaskAndSchedule.Someday -> {}
        }
    }
}