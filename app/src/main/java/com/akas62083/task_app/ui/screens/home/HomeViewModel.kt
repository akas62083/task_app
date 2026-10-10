package com.akas62083.task_app.ui.screens.home

import android.util.Log
import android.util.Log.i
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akas62083.task_app.TaskAndSchedule
import com.akas62083.task_app.remote.PostService
import com.akas62083.task_app.usecase.ChangeTaskStatusUseCase
import com.akas62083.task_app.usecase.CreateNewScheduleUseCase
import com.akas62083.task_app.usecase.CreateNewScheduleWithRoutineUseCase
import com.akas62083.task_app.usecase.CreateNewTaskUseCase
import com.akas62083.task_app.usecase.CreateNewTaskWithRoutineUseCase
import com.akas62083.task_app.usecase.GetTasksAndSchedulesByDateUseCase
import com.akas62083.task_app.usecase.GetTodaySTasksAndSchedulesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTaskAndScheduleByDateUseCase: GetTasksAndSchedulesByDateUseCase,
    private val createNewScheduleUseCase: CreateNewScheduleUseCase,
    private val createNewTaskUseCase: CreateNewTaskUseCase,
    private val createNewScheduleWithRoutineUseCase: CreateNewScheduleWithRoutineUseCase,
    private val createNewTaskWithRoutineUseCase: CreateNewTaskWithRoutineUseCase,
    private val getTasksAndSchedulesByDateUseCase: GetTasksAndSchedulesByDateUseCase,
    private val getTodaySTasksAndSchedulesUseCase: GetTodaySTasksAndSchedulesUseCase,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    private val postService: PostService
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
        viewModelScope.launch {
            Log.i("post", postService.getPosts())
        }
    }

    private fun load() {
        viewModelScope.launch {
            launch {
                combine(
                    getTasksAndSchedulesByDateUseCase().first,
                    getTasksAndSchedulesByDateUseCase().second,
                    getTodaySTasksAndSchedulesUseCase()
                ) { tasks, weekData, todaySTasks ->
                    val today = LocalDate.now()
                    val todayOfWeek = today.dayOfWeek

                    //-- weekTasks, dateList, dateListBackUp
                    val weekTasks: MutableMap<LocalDate, WeekDisplayData> = mutableMapOf()
                    val dateList: MutableList<LocalDate?> = mutableListOf()
                    var startDay: LocalDate =
                        when (todayOfWeek) {
                            DayOfWeek.SUNDAY -> today
                            else -> today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                        }

                    while (startDay.dayOfWeek != todayOfWeek) {
                        dateList.add(null)
                        startDay = startDay.plusDays(1)
                    }
                    for (i in 0..7) {
                        val todaysTasks = weekData.getOrDefault(startDay, listOf())
                        weekTasks[startDay] = WeekDisplayData(
                            dayOfWeek = startDay.dayOfWeek,
                            tasks = todaysTasks,
                            backgroundColor = if (i == 0) Color(0x80d4d3da) else Color(0x80e3e2e9),
                            display = true,
                            taskCount = todaysTasks.count { it is TaskAndSchedule.Task },
                            scheduleCount = todaysTasks.count { it is TaskAndSchedule.Schedule }
                        )
                        dateList.add(startDay)
                        startDay = startDay.plusDays(1)
                    }
                    while (dateList.size < 14) {
                        dateList.add(null)
                        startDay = startDay.plusDays(1)
                    }
                    //--

                    //-- tasksWithTags
                    val taskAndScheduleAndDivider: MutableList<TaskAndScheduleAndDivider> = mutableListOf()
                    var previousDate: LocalDate? = null
                    tasks.forEach { task ->
                        when(task) {
                            is TaskAndSchedule.Task -> {
                                if(previousDate == null) {
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.Divider(task.date.year.toString() + "/" + task.date.monthValue.toString()))
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.TaskOrSchedule(task))
                                    previousDate = task.date
                                } else if(previousDate.monthValue != task.date.monthValue || previousDate.year != task.date.year) {
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.Divider(task.date.year.toString() + "/" + task.date.monthValue.toString()))
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.TaskOrSchedule(task))
                                    previousDate = task.date
                                } else {
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.TaskOrSchedule(task))
                                    previousDate = task.date
                                }
                            }
                            is TaskAndSchedule.Schedule -> {
                                if(previousDate == null) {
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.Divider(task.date.year.toString() + "/" + task.date.monthValue.toString()))
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.TaskOrSchedule(task))
                                    previousDate = task.date
                                } else if(previousDate.monthValue != task.date.monthValue || previousDate.year != task.date.year) {
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.Divider(task.date.year.toString() + "/" + task.date.monthValue.toString()))
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.TaskOrSchedule(task))
                                    previousDate = task.date
                                } else {
                                    taskAndScheduleAndDivider.add(TaskAndScheduleAndDivider.TaskOrSchedule(task))
                                    previousDate = task.date
                                }
                            }
                            is TaskAndSchedule.Someday -> {}
                        }
                    }
                    //--

                    _uiState.update { currentState ->
                        currentState.copy(
                            tasksWithTags = taskAndScheduleAndDivider,
                            todaySTasksWithTags = todaySTasks,
                            dateMap = weekTasks,
                            dateList = dateList.map { date ->
                                if (date == currentState.clickedDate) {
                                    null
                                } else {
                                    date
                                }
                            },
                            dateListBackUp = dateList
                        )
                    }
                }.collect {}
            }
            launch {
                while (true) {
                    val currentDate = LocalDate.now()
                    if (currentDate != _uiState.value.currentDate) {
                        _uiState.update { currentState ->
                            currentState.copy(currentDate = currentDate)
                        }
                    }
                    delay(60_000L)
                }
            }
        }
    }


    fun onEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.DateCardClicked -> { dateCardClicked(event.date) }
            is HomeEvent.TaskClicked -> { taskStatusChange(event.task) }
            is HomeEvent.RightArrowClicked -> { dateCardClicked(uiState.value.clickedDate?.plusDays(1)) }
            is HomeEvent.LeftArrowClicked -> { dateCardClicked(uiState.value.clickedDate?.minusDays(1)) }
        }
    }

    private fun dateCardClicked(date: LocalDate?) {
        var rightArrowEnabled: Boolean = true
        var leftArrowEnabled: Boolean = true
        if(date != null) {
            if(date == LocalDate.now()) {
                leftArrowEnabled = false
            } else if(date == LocalDate.now().plusDays(7)) {
                rightArrowEnabled = false
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                dateList = currentState.dateListBackUp.map { date_ ->
                    if(date == date_) {
                        null
                    } else {
                        date_
                    }
                },
                clickedDate = date,
                rightArrowEnabled = rightArrowEnabled,
                leftArrowEnabled = leftArrowEnabled,
            )
        }
    }
    private fun taskStatusChange(task: TaskAndSchedule) {
        viewModelScope.launch {
            changeTaskStatusUseCase(task)
        }
    }
}

