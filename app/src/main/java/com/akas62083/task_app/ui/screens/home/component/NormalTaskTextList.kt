package com.akas62083.task_app.ui.screens.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.akas62083.task_app.Status
import com.akas62083.task_app.TaskAndSchedule
import com.akas62083.task_app.ui.screens.home.HomeUiState
import com.akas62083.task_app.ui.screens.home.TaskAndScheduleAndDivider

@Composable
fun NormalTaskTextList(
    uiState: HomeUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(uiState.tasksWithTags) { tasks ->
            when (tasks) {
                is TaskAndScheduleAndDivider.Divider -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = tasks.divider,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }

                is TaskAndScheduleAndDivider.TaskOrSchedule -> {
                    Text(
                        text = when (tasks.taskAndSchedule) {
                            is TaskAndSchedule.Task -> tasks.taskAndSchedule.title + " : " + tasks.taskAndSchedule.date.toString()
                            is TaskAndSchedule.Schedule -> tasks.taskAndSchedule.title + "：" + tasks.taskAndSchedule.date.toString()
                            is TaskAndSchedule.Someday -> tasks.taskAndSchedule.title
                        },
                        color = when (tasks.taskAndSchedule) {
                            is TaskAndSchedule.Task -> if (tasks.taskAndSchedule.status != Status.Done) Color.Red else Color(
                                0xffa6201a
                            )

                            is TaskAndSchedule.Schedule -> Color.Green
                            is TaskAndSchedule.Someday -> Color.Black
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}