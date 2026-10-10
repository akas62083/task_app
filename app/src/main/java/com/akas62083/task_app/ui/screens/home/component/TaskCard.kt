package com.akas62083.task_app.ui.screens.home.component

import android.R.attr.onClick
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.akas62083.task_app.ui.screens.home.HomeUiState
import com.akas62083.task_app.ui.screens.home.WeekDisplayData
import java.time.LocalDate

@Composable
fun TaskCard(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    date: LocalDate,
    onClick: (LocalDate) -> Unit
) {
    val weekTasks = uiState.dateMap[date] ?: WeekDisplayData(date.dayOfWeek, listOf())
    Card(
        colors = CardDefaults.cardColors(
            containerColor = weekTasks.backgroundColor
        ),
        modifier = modifier.clickable { onClick(date) },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if(weekTasks.display) {
                Text(
                    date.monthValue.toString() + "/" + date.dayOfMonth.toString(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Task: " + weekTasks.taskCount.toString(),
                    textAlign = TextAlign.Center,
                    color = if(weekTasks.taskCount != 0) Color.Red else Color(0x40ff0000),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Sche: " + weekTasks.scheduleCount.toString(),
                    textAlign = TextAlign.Center,
                    color = if(weekTasks.scheduleCount != 0) Color.Green else Color(0x4000ff00),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}