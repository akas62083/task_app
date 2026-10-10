package com.akas62083.task_app.ui.screens.home

import androidx.compose.ui.graphics.Color
import com.akas62083.task_app.TaskAndSchedule
import com.akas62083.task_app.db.entity.TaskWithTags
import java.time.DayOfWeek
import java.time.LocalDate

data class HomeUiState(
    val currentDate: LocalDate = LocalDate.now(),
    val tasksWithTags: List<TaskAndScheduleAndDivider> = emptyList(),
    val todaySTasksWithTags: List<TaskAndSchedule> = emptyList(),
    val clickedDate: LocalDate? = null,
    val normalTaskList: Boolean = true,

    val rightArrowEnabled: Boolean = false,
    val leftArrowEnabled: Boolean = false,

    val dateMap: Map<LocalDate, WeekDisplayData> = emptyMap(),
    val dateList: List<LocalDate?> = emptyList(),
    val dateListBackUp: List<LocalDate?> = emptyList(),

    )

data class WeekDisplayData(
    val dayOfWeek: DayOfWeek,
    val tasks: List<TaskAndSchedule> = emptyList(),
    val backgroundColor: Color = Color(0xffe3e2e9),
    val taskCount: Int = 0,
    val scheduleCount: Int = 0,
    val display: Boolean = false
)
sealed interface TaskAndScheduleAndDivider{
    data class TaskOrSchedule(val taskAndSchedule: TaskAndSchedule): TaskAndScheduleAndDivider
    data class Divider(val divider: String): TaskAndScheduleAndDivider
}