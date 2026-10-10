package com.akas62083.task_app.ui.screens.write_task

import android.util.Log
import com.akas62083.task_app.MainTag
import com.akas62083.task_app.Rule
import com.akas62083.task_app.Rules
import com.akas62083.task_app.StartDayAndEndDayRule
import java.time.DayOfWeek
import java.time.LocalDate

data class WriteTaskUiState(
    val title: String = "",
    val description: String = "",
    val selectedMainTag: SelectedMainTag,

    private val localDateInDatePicker: LocalDate = LocalDate.now(),
    private val endDateInDatePicker: LocalDate = LocalDate.now().plusMonths(1),
) {
    val dateList: List<LocalDate> get() {
        val list = mutableListOf<LocalDate>()
        var currentDate = localDateInDatePicker
        while (currentDate.isBefore(endDateInDatePicker)) {
            list.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        return list.toList()
    }
    val canPushLeftArrow: Boolean get() {
        return dateList[0] != LocalDate.now()
    }
}

sealed interface SelectedMainTag {
    data class Task(
        val dateType: DateType,
        val startTime: Int = 5,
    ): SelectedMainTag
    data class Schedule(
        val dateType: DateType
    ): SelectedMainTag
    data object Someday: SelectedMainTag
}

sealed interface DateType {
    data class Single(
        val dates: List<LocalDate>,
        val isDatePickerOpened: Boolean = false
    ): DateType
    data class Routine(
        val rules: Rules = Rules(listOf(StartDayAndEndDayRule(LocalDate.MIN, LocalDate.MAX))),
        val isDatePickerOpened: RoutineDatePickerStatus = RoutineDatePickerStatus.CLOSED
    ): DateType
}

enum class RoutineDatePickerStatus {
    CLOSED,
    STARTDAY,
    ENDDAY
}