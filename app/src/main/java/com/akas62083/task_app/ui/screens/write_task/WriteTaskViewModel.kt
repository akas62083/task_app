package com.akas62083.task_app.ui.screens.write_task

import android.R.attr.x
import android.util.Log
import android.util.Log.e
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.akas62083.task_app.MainTag
import com.akas62083.task_app.Route
import com.akas62083.task_app.usecase.CreateNewScheduleUseCase
import com.akas62083.task_app.usecase.CreateNewScheduleWithRoutineUseCase
import com.akas62083.task_app.usecase.CreateNewTaskUseCase
import com.akas62083.task_app.usecase.CreateNewTaskWithRoutineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WriteTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createNewScheduleUseCase: CreateNewScheduleUseCase,
    private val createNewScheduleWithRoutineUseCase: CreateNewScheduleWithRoutineUseCase,
    private val createNewTaskUseCase: CreateNewTaskUseCase,
    private val createNewTaskWithRoutineUseCase: CreateNewTaskWithRoutineUseCase
): ViewModel() {
    private val args = savedStateHandle.toRoute<Route.Write>()
    private val date: LocalDate? = args.date?.let { LocalDate.parse(it) }
    private val _uiState = MutableStateFlow(WriteTaskUiState(selectedMainTag = SelectedMainTag.Task(DateType.Single(dates = if(date == null) emptyList() else listOf(date)))))
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: WriteTaskEvent) {
        when(event) {
            is WriteTaskEvent.SelectMainTag -> { selectMainTag(event.mainTag) }
            is WriteTaskEvent.ChangeTitle -> { changeTitle(event.title) }
            is WriteTaskEvent.ChangeDescription -> { changeDescription(event.description) }
            is WriteTaskEvent.DatePickerButtonClicked -> { togleDatePicker() }
            is WriteTaskEvent.SaveButtonClicked -> { save() }
            is WriteTaskEvent.DateTypeChange -> { dateTypeChange(event.dateType) }
            is WriteTaskEvent.DateClick -> { dateClicked(event.date) }
            is WriteTaskEvent.SliderValueChange -> { sliderValueChange(event.value) }
            is WriteTaskEvent.LeftArrowClicked -> { datePickerRangeChange(false) }
            is WriteTaskEvent.RightArrowClicked -> { datePickerRangeChange(true) }
        }
    }

    private fun selectMainTag(mainTag: MainTag) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedMainTag = when(mainTag) {
                    MainTag.Task -> SelectedMainTag.Task(DateType.Single(dates = if(date == null) emptyList() else listOf(date)))
                    MainTag.Schedule -> SelectedMainTag.Schedule(DateType.Single(dates = if(date == null) emptyList() else listOf(date)))
                    MainTag.Someday -> SelectedMainTag.Someday
                }
            )
        }
    }
    private fun changeTitle(title: String) {
        _uiState.update { currentState ->
            currentState.copy(title = title)
        }
    }
    private fun changeDescription(description: String) {
        _uiState.update { currentState ->
            currentState.copy(description = description)
        }
    }
    private fun togleDatePicker() {
        var endDate = LocalDate.now().plusMonths(1)
        while(endDate.plusDays(1).monthValue == endDate.monthValue) {
            endDate = endDate.plusDays(1)
        }
        _uiState.update { currentState ->
            val selectedMainTag = currentState.selectedMainTag
            currentState.copy(
                selectedMainTag = when(selectedMainTag) {
                    is SelectedMainTag.Task -> {
                        when(selectedMainTag.dateType) {
                            is DateType.Single -> {
                                selectedMainTag.copy(dateType = selectedMainTag.dateType.copy(isDatePickerOpened = !selectedMainTag.dateType.isDatePickerOpened))
                            }
                            is DateType.Routine -> {
                                selectedMainTag
                            }
                        }
                    }
                    is SelectedMainTag.Schedule -> {
                        when(selectedMainTag.dateType) {
                            is DateType.Single -> {
                                selectedMainTag.copy(dateType = selectedMainTag.dateType.copy(isDatePickerOpened = !selectedMainTag.dateType.isDatePickerOpened))
                            }
                            is DateType.Routine -> {
                                selectedMainTag
                            }
                        }
                    }
                    is SelectedMainTag.Someday -> {
                        selectedMainTag
                    }
                },
                endDateInDatePicker = endDate,
                localDateInDatePicker = LocalDate.now()
            )
        }
    }


    private fun dateClicked(date: LocalDate) {
        val selectedMainTag = uiState.value.selectedMainTag
        val isDatePicked: Boolean = when(selectedMainTag) {
            is SelectedMainTag.Task -> {
                when(selectedMainTag.dateType) {
                    is DateType.Single -> {
                        selectedMainTag.dateType.dates.contains(date)
                    }
                    is DateType.Routine -> { false }
                }
            }
            is SelectedMainTag.Schedule -> {
                when(selectedMainTag.dateType) {
                    is DateType.Single -> {
                        selectedMainTag.dateType.dates.contains(date)
                    }
                    is DateType.Routine -> { false }
                }
            }
            is SelectedMainTag.Someday -> {
                false
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                selectedMainTag = when(selectedMainTag) {
                    is SelectedMainTag.Task -> {
                        when(selectedMainTag.dateType) {
                            is DateType.Single -> {
                                if(isDatePicked) {
                                    selectedMainTag.copy(dateType = selectedMainTag.dateType.copy(dates = selectedMainTag.dateType.dates.minus(date)))
                                } else {
                                    selectedMainTag.copy(dateType = selectedMainTag.dateType.copy(dates = selectedMainTag.dateType.dates.plus(date)))
                                }
                            }
                            is DateType.Routine -> {
                                selectedMainTag
                            }
                        }
                    }
                    is SelectedMainTag.Schedule -> {
                        when(selectedMainTag.dateType) {
                            is DateType.Single -> {
                                if(isDatePicked) {
                                    selectedMainTag.copy(dateType = selectedMainTag.dateType.copy(dates = selectedMainTag.dateType.dates.minus(date)))
                                } else {
                                    selectedMainTag.copy(dateType = selectedMainTag.dateType.copy(dates = selectedMainTag.dateType.dates.plus(date)))
                                }
                            }
                            is DateType.Routine -> {
                                selectedMainTag
                            }
                        }
                    }
                    is SelectedMainTag.Someday -> {
                        selectedMainTag
                    }
                }
            )
        }
    }
    private fun save() {
        viewModelScope.launch {
            val selectedMainTag = uiState.value.selectedMainTag
            when (selectedMainTag) {
                is SelectedMainTag.Task -> {
                    when (selectedMainTag.dateType) {
                        is DateType.Single -> {
                            createNewTaskUseCase(
                                title = uiState.value.title,
                                description = uiState.value.description,
                                date = selectedMainTag.dateType.dates,
                                startTime = selectedMainTag.startTime,
                                tags = setOf()
                            )
                        }
                        is DateType.Routine -> {}
                    }
                }

                is SelectedMainTag.Schedule -> {
                    when (selectedMainTag.dateType) {
                        is DateType.Single -> {
                            createNewScheduleUseCase(
                                title = uiState.value.title,
                                description = uiState.value.description,
                                date = selectedMainTag.dateType.dates,
                                tags = setOf()
                            )
                        }
                        is DateType.Routine -> {}
                    }
                }

                is SelectedMainTag.Someday -> {}
            }
        }
    }
    private fun dateTypeChange(dateType: DateType) {
        val selectedMainTag = uiState.value.selectedMainTag
        _uiState.update { currentState ->
            currentState.copy(selectedMainTag =
                when(selectedMainTag) {
                    is SelectedMainTag.Task -> {
                        selectedMainTag.copy(dateType = dateType)
                    }
                    is SelectedMainTag.Schedule -> {
                        selectedMainTag.copy(dateType = dateType)
                    }
                    is SelectedMainTag.Someday -> {
                        selectedMainTag
                    }
                }
            )
        }
    }
    private fun sliderValueChange(value: Float) {
        val selectedMainTag = uiState.value.selectedMainTag as SelectedMainTag.Task
        _uiState.update { currentState ->
            currentState.copy(selectedMainTag = selectedMainTag.copy(startTime = value.toInt()))
        }
    }
    private fun datePickerRangeChange(isForward: Boolean) { // datePicker で、範囲を変えるとき、新たにさらに先の範囲を表示させるのか(true)、1つ前の範囲の表示に戻すのか(false)
        val currentEndDate = uiState.value.dateList.last()
        val localDate = uiState.value.dateList.first()
        val newEndDate = if(isForward) {
            currentEndDate.plusMonths(1)
        } else {
            if(currentEndDate.minusMonths(1) < LocalDate.now().plusMonths(1)) {
                LocalDate.now().plusMonths(1)
            } else {
                currentEndDate.minusMonths(1)
            }
        }
        val newLocalDate = if(isForward) {
            localDate.plusMonths(1)
        } else {
            if(localDate.minusMonths(1) < LocalDate.now()) {
                LocalDate.now()
            } else {
                localDate.minusMonths(1)
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                endDateInDatePicker = newEndDate,
                localDateInDatePicker = newLocalDate
            )
        }
    }
}