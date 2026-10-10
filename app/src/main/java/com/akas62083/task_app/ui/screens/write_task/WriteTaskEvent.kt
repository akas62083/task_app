package com.akas62083.task_app.ui.screens.write_task

import com.akas62083.task_app.MainTag
import java.time.LocalDate

sealed interface WriteTaskEvent {
    data class SelectMainTag(val mainTag: MainTag): WriteTaskEvent
    data class ChangeTitle(val title: String): WriteTaskEvent
    data class ChangeDescription(val description: String): WriteTaskEvent
    data object DatePickerButtonClicked: WriteTaskEvent
    data object SaveButtonClicked: WriteTaskEvent
    data class DateTypeChange(val dateType: DateType): WriteTaskEvent
    data class SliderValueChange(val value: Float): WriteTaskEvent

    // DatePicker の処理
    data class DateClick(val date: LocalDate): WriteTaskEvent
    data object LeftArrowClicked: WriteTaskEvent
    data object RightArrowClicked: WriteTaskEvent
}