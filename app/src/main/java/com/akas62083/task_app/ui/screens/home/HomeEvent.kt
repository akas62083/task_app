package com.akas62083.task_app.ui.screens.home

import com.akas62083.task_app.TaskAndSchedule
import java.time.LocalDate

sealed interface HomeEvent {
    data class DateCardClicked(val date: LocalDate?): HomeEvent
    data class TaskClicked(val task: TaskAndSchedule): HomeEvent
    data object RightArrowClicked: HomeEvent
    data object LeftArrowClicked: HomeEvent
}