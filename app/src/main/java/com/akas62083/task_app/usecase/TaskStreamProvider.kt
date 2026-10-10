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
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class TaskStreamProvider @Inject constructor(
    private val repository: DataRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    fun stream(): Flow<List<TaskAndSchedule>> {
        val date = LocalDate.now()
        val result =  combine(repository.getAllTaskAndTags(), repository.getAllTaskLogs()) { tasks, logs ->
            tasks.flatMap { entry ->
                when(entry.task.mainTag) {
                    MainTag.Task -> if(entry.task.routineJson == null && entry.task.date != null) {
                        val log = logs.find { it.taskId == entry.task.id && it.date == entry.task.date }
                        if(log != null) {
                            listOf(
                                TaskAndSchedule.Task(
                                    id = entry.task.id,
                                    title = entry.task.title,
                                    description = entry.task.description,
                                    mainTag = MainTag.Task,
                                    tags = entry.tags.toSet(),

                                    date = entry.task.date,
                                    startDate = entry.task.date.minusDays(entry.task.startTime?.toLong() ?: 0),

                                    status = log.status,
                                )
                            )
                        } else {
                            listOf(
                                TaskAndSchedule.Task(
                                    id = entry.task.id,
                                    title = entry.task.title,
                                    description = entry.task.description,
                                    mainTag = MainTag.Task,
                                    tags = entry.tags.toSet(),

                                    date = entry.task.date,
                                    startDate = entry.task.date.minusDays(entry.task.startTime?.toLong() ?: 0),

                                    status = Status.NotDone,
                                )
                            )
                        }
                    } else {
                        if(entry.task.routineJson != null) {
                            var dateRange = date..date.plusMonths(1)
                            val routine = Json.decodeFromString<Rules>(entry.task.routineJson).rules
                            val selectedDateSet = mutableSetOf<LocalDate>()
                            val rule = routine.filterIsInstance<StartDayAndEndDayRule>().firstOrNull()
                            if(rule != null) {
                                if(rule.startDay > dateRange.start) {
                                    dateRange = rule.startDay..dateRange.endInclusive
                                }
                                if(rule.endDay < dateRange.endInclusive) {
                                    dateRange = dateRange.start..rule.endDay
                                }
                            }
                            if(routine.filterIsInstance<DayOfWeekRule>().firstOrNull() != null || routine.filterIsInstance<IntervalRule>().firstOrNull() != null) {
                                val dayOfWeekRule = routine.filterIsInstance<DayOfWeekRule>().firstOrNull()
                                val intervalRule = routine.filterIsInstance<IntervalRule>().firstOrNull()
                                var date = dateRange.start
                                while(date <= dateRange.endInclusive) {
                                    if(dayOfWeekRule != null && !dayOfWeekRule.days.contains(date.dayOfWeek)) {
                                        date = date.plusDays(1)
                                        continue
                                    }
                                    if(intervalRule != null && ChronoUnit.DAYS.between(rule?.startDay, date) % intervalRule.interval != 0L) {
                                        date = date.plusDays(1)
                                        continue
                                    }
                                    selectedDateSet.add(date)
                                    date = date.plusDays(1)
                                }
                            }
                            selectedDateSet.map { date_ ->
                                val log = logs.find { it.taskId == entry.task.id && it.date == date_ }
                                if(log != null) {
                                    TaskAndSchedule.Task(
                                        id = entry.task.id,
                                        title = entry.task.title,
                                        description = entry.task.description,
                                        mainTag = MainTag.Task,
                                        tags = entry.tags.toSet(),

                                        date = date_,
                                        startDate = date_.minusDays(
                                            entry.task.startTime?.toLong() ?: 0
                                        ),

                                        status = log.status,
                                    )
                                } else {
                                    TaskAndSchedule.Task(
                                        id = entry.task.id,
                                        title = entry.task.title,
                                        description = entry.task.description,
                                        mainTag = MainTag.Task,
                                        tags = entry.tags.toSet(),

                                        date = date_,
                                        startDate = date_.minusDays(
                                            entry.task.startTime?.toLong() ?: 0
                                        ),

                                        status = Status.NotDone,
                                    )
                                }
                            }
                        } else {
                            emptyList()
                        }
                    }
                    MainTag.Schedule -> if(entry.task.routineJson == null && entry.task.date != null) {
                        val log = logs.find { it.taskId == entry.task.id && it.date == entry.task.date }
                        if(log != null) {
                            listOf(
                                TaskAndSchedule.Schedule(
                                    id = entry.task.id,
                                    title = entry.task.title,
                                    description = entry.task.description,
                                    mainTag = MainTag.Schedule,
                                    tags = entry.tags.toSet(),

                                    date = entry.task.date,

                                    status = log.status,
                                )
                            )
                        } else {
                            listOf(
                                TaskAndSchedule.Schedule(
                                    id = entry.task.id,
                                    title = entry.task.title,
                                    description = entry.task.description,
                                    mainTag = MainTag.Schedule,
                                    tags = entry.tags.toSet(),

                                    date = entry.task.date,

                                    status = Status.NotDone,
                                )
                            )
                        }
                    } else {
                        if(entry.task.routineJson != null) {
                            var dateRange = date..date.plusMonths(1)
                            val routine = Json.decodeFromString<Rules>(entry.task.routineJson).rules
                            val selectedDateSet = mutableSetOf<LocalDate>()
                            val rule = routine.filterIsInstance<StartDayAndEndDayRule>().firstOrNull()
                            if(rule != null) {
                                if(rule.startDay > dateRange.start) {
                                    dateRange = rule.startDay..dateRange.endInclusive
                                }
                                if(rule.endDay < dateRange.endInclusive) {
                                    dateRange = dateRange.start..rule.endDay
                                }
                            }
                            if(routine.filterIsInstance<DayOfWeekRule>().firstOrNull() != null || routine.filterIsInstance<IntervalRule>().firstOrNull() != null) {
                                val dayOfWeekRule = routine.filterIsInstance<DayOfWeekRule>().firstOrNull()
                                val intervalRule = routine.filterIsInstance<IntervalRule>().firstOrNull()
                                var date = dateRange.start
                                while(date <= dateRange.endInclusive) {
                                    if(dayOfWeekRule != null && !dayOfWeekRule.days.contains(date.dayOfWeek)) {
                                        date = date.plusDays(1)
                                        continue
                                    }
                                    if(intervalRule != null && ChronoUnit.DAYS.between(rule?.startDay, date) % intervalRule.interval != 0L) {
                                        date = date.plusDays(1)
                                        continue
                                    }
                                    selectedDateSet.add(date)
                                    date = date.plusDays(1)
                                }
                            }
                            selectedDateSet.map { date_ ->
                                val log = logs.find { it.taskId == entry.task.id && it.date == date_ }
                                if(log != null) {
                                    TaskAndSchedule.Schedule(
                                        id = entry.task.id,
                                        title = entry.task.title,
                                        description = entry.task.description,
                                        mainTag = MainTag.Schedule,
                                        tags = entry.tags.toSet(),

                                        date = date_,

                                        status = log.status,
                                    )
                                } else {
                                    TaskAndSchedule.Schedule(
                                        id = entry.task.id,
                                        title = entry.task.title,
                                        description = entry.task.description,
                                        mainTag = MainTag.Schedule,
                                        tags = entry.tags.toSet(),

                                        date = date_,

                                        status = Status.NotDone,
                                    )
                                }
                            }
                        } else {
                            emptyList()
                        }
                    }
                    MainTag.Someday ->  {
                        listOf(
                            TaskAndSchedule.Someday(
                                id = entry.task.id,
                                title = entry.task.title,
                                description = entry.task.description,
                                mainTag = MainTag.Someday
                            )
                        )
                    }
                }
            }
        }.flowOn(ioDispatcher)
        return result
    }
}