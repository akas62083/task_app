package com.akas62083.task_app.usecase

import com.akas62083.task_app.DayOfWeekRule
import com.akas62083.task_app.IntervalRule
import com.akas62083.task_app.MainTag
import com.akas62083.task_app.Rules
import com.akas62083.task_app.StartDayAndEndDayRule
import com.akas62083.task_app.Status
import com.akas62083.task_app.TaskAndSchedule
import com.akas62083.task_app.di.IoDispatcher
import com.akas62083.task_app.repository.DataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetTodaySTasksAndSchedulesUseCase @Inject constructor(
    private val taskStreamProvider: TaskStreamProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(date: LocalDate = LocalDate.now()): Flow<List<TaskAndSchedule>> {
        return taskStreamProvider.stream()
            .map { tasks ->
                tasks.filter { task ->
                    when(task) {
                        is TaskAndSchedule.Task -> (task.startDate <= date && (task.status == Status.NotDone || task.date >= date)) || task.date == date
                        is TaskAndSchedule.Schedule -> task.date == date
                        is TaskAndSchedule.Someday -> false
                    }
                }.sortedBy { task ->
                    when(task) {
                        is TaskAndSchedule.Task -> task.date
                        is TaskAndSchedule.Schedule -> task.date
                        is TaskAndSchedule.Someday -> LocalDate.MAX
                    }
                }
            }
            .flowOn(ioDispatcher)
    }
}
