package com.akas62083.task_app.usecase

import android.R.attr.description
import android.util.Log
import android.util.Log.d
import androidx.compose.runtime.mutableStateListOf
import coil.util.CoilUtils.result
import com.akas62083.task_app.DayOfWeekRule
import com.akas62083.task_app.IntervalRule
import com.akas62083.task_app.MainTag
import com.akas62083.task_app.Rule
import com.akas62083.task_app.Rules
import com.akas62083.task_app.StartDayAndEndDayRule
import com.akas62083.task_app.Status
import com.akas62083.task_app.TaskAndSchedule
import com.akas62083.task_app.di.IoDispatcher
import com.akas62083.task_app.repository.DataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Map.entry
import javax.inject.Inject
import kotlin.collections.forEach
import kotlin.collections.sortedBy

class GetTasksAndSchedulesByDateUseCase @Inject constructor(
    private val taskStreamProvider: TaskStreamProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Pair<Flow<List<TaskAndSchedule>>, Flow<Map<LocalDate, List<TaskAndSchedule>>>> {
        val today = LocalDate.now()
        val endOfWeekly = today.plusDays(7)

        val baseFlow = taskStreamProvider.stream()
            .map { tasks ->
                tasks.filter { it is TaskAndSchedule.Task || it is TaskAndSchedule.Schedule }
                    .filter { task ->
                        when (task) {
                            is TaskAndSchedule.Task -> task.date >= today
                            is TaskAndSchedule.Schedule -> task.date >= today
                            else -> false
                        }
                    }
                    .sortedBy { task ->
                        when (task) {
                            is TaskAndSchedule.Task -> task.date
                            is TaskAndSchedule.Schedule -> task.date
                            else -> LocalDate.MAX
                        }
                    }
            }
            .flowOn(ioDispatcher)

        // 8日以降のタスクリスト
        val taskListFlow = baseFlow.map { tasks ->
            tasks.filter { task ->
                val date = when (task) {
                    is TaskAndSchedule.Task -> task.date
                    is TaskAndSchedule.Schedule -> task.date
                    else -> null
                }
                date != null && date > endOfWeekly
            }
        }

        // 今日から7日分を日付ごとにまとめた Map
        val weeklyMapFlow = baseFlow.map { tasks ->
            tasks.filter { task ->
                val date = when (task) {
                    is TaskAndSchedule.Task -> task.date
                    is TaskAndSchedule.Schedule -> task.date
                    else -> null
                }
                date != null && date in today..endOfWeekly
            }.groupBy { task ->
                when (task) {
                    is TaskAndSchedule.Task -> task.date
                    is TaskAndSchedule.Schedule -> task.date
                    else -> today // ここには来ないはず
                }
            }
        }

        return Pair(taskListFlow, weeklyMapFlow)
    }
}
