package com.akas62083.task_app.ui.screens.home.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akas62083.task_app.Status
import com.akas62083.task_app.TaskAndSchedule
import com.akas62083.task_app.ui.screens.home.HomeUiState
import java.time.LocalDate

@Composable
fun DateDialog(
    uiState: HomeUiState,
    taskClicked: (TaskAndSchedule) -> Unit,
    addTask: (LocalDate) -> Unit,
    leftArrowClicked: () -> Unit,
    rightArrowClicked: () -> Unit,
    modifier: Modifier = Modifier,
    dateValue: LocalDate,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (dateValue == uiState.currentDate) Color(0xffd4d3da) else Color(0xffe3e2e9)
        ),
        modifier = modifier.clickable(
            enabled = false
        ) {}, // 何もしないことを明記。親のクリック判定を上書き。
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "${dateValue.monthValue}/${dateValue.dayOfMonth}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable(
                            enabled = uiState.leftArrowEnabled
                        ) { leftArrowClicked() }
                    )
                    Column {
                        val tasks = if (dateValue == uiState.currentDate) {
                            uiState.todaySTasksWithTags
                        } else {
                            uiState.dateMap[dateValue]?.tasks ?: emptyList()
                        }

                        tasks.forEach { task ->
                            when (task) {
                                is TaskAndSchedule.Task -> {
                                    if (task.status == Status.Hide) return@forEach
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = task.status == Status.Done,
                                            onCheckedChange = { taskClicked(task) }
                                        )
                                        Text(
                                            text = if(task.date == dateValue) task.title else task.title + "：" + task.date.toString(),
                                            color = if(task.date == dateValue) Color.Red else Color.Black,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                is TaskAndSchedule.Schedule -> {
                                    if (task.status == Status.Hide) return@forEach
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = task.status == Status.Done,
                                            onCheckedChange = { taskClicked(task) }
                                        )
                                        Text(
                                            text = task.title,
                                            color = Color.Green,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                is TaskAndSchedule.Someday -> {}
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.clickable(
                            enabled = uiState.rightArrowEnabled
                        ) { rightArrowClicked() }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(9f))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(9f))
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            addTask(dateValue)
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
