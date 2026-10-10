package com.akas62083.task_app.ui.screens.write_task.component

import android.R.attr.onClick
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.akas62083.task_app.StartDayAndEndDayRule
import com.akas62083.task_app.ui.screens.write_task.DateType
import com.akas62083.task_app.ui.screens.write_task.SelectedMainTag
import com.akas62083.task_app.ui.screens.write_task.WriteTaskUiState
import com.akas62083.task_app.ui.theme.Task_appTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalQueries.localDate

@Composable
fun SomeDatePicker(
    uiState: WriteTaskUiState,
    onDateClick: (LocalDate) -> Unit,
    confirm: () -> Unit,
    leftArrowClicked: () -> Unit,
    rightArrowClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = { confirm() }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = uiState.dateList[0].year.toString() + "/" + uiState.dateList[0].monthValue.toString(),
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        HorizontalDivider()
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (uiState.dateList[0].dayOfWeek != DayOfWeek.SUNDAY) {
                        for (i in 1..(uiState.dateList[0].dayOfWeek.value - DayOfWeek.MONDAY.value + 1)) {
                            item {
                                EmptyDateCard()
                            }
                        }
                    }
                    items(uiState.dateList) { date ->
                        DayCard(
                            date = date,
                            uiState = uiState,
                            onClick = { onDateClick(it) }
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                         Row(
                         ) {
                             Button(
                                 onClick = { leftArrowClicked() },
                                 colors = ButtonDefaults.buttonColors(
                                     containerColor = Color.Transparent,
                                     contentColor = Color.Black
                                 ),
                                 enabled = uiState.canPushLeftArrow
                             ) {
                                 Icon(
                                     imageVector = Icons.Default.ArrowBack,
                                     contentDescription = null,
                                 )
                             }
                             Spacer(modifier = Modifier.weight(1f))
                             Button(
                                 onClick = { rightArrowClicked() },
                                 colors = ButtonDefaults.buttonColors(
                                     containerColor = Color.Transparent,
                                     contentColor = Color.Black
                                 )
                             ) {
                                 Icon(
                                     imageVector = Icons.Default.ArrowForward,
                                     contentDescription = null,
                                 )
                             }
                         }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Button(
                                onClick = { confirm() },
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0x10ffffff),
    date: LocalDate,
    uiState: WriteTaskUiState,
    onClick: (LocalDate) -> Unit
) {
    val isSelected = when(uiState.selectedMainTag) {
        is SelectedMainTag.Task -> {
            when(uiState.selectedMainTag.dateType) {
                is DateType.Single -> {
                    uiState.selectedMainTag.dateType.dates.contains(date)
                }
                is DateType.Routine -> {
                    val rules = uiState.selectedMainTag.dateType.rules.rules
                    val rule = rules.filterIsInstance<StartDayAndEndDayRule>().firstOrNull()
                    rule != null && date in rule.startDay..rule.endDay
                }
            }
        }
        is SelectedMainTag.Schedule -> {
            when(uiState.selectedMainTag.dateType) {
                is DateType.Single -> {
                    uiState.selectedMainTag.dateType.dates.contains(date)
                }
                is DateType.Routine -> {
                    val rules = uiState.selectedMainTag.dateType.rules.rules
                    val rule = rules.filterIsInstance<StartDayAndEndDayRule>().firstOrNull()
                    rule != null && date in rule.startDay..rule.endDay
                }
            }
        }
        is SelectedMainTag.Someday -> { false }
    }
    Card(
        modifier = modifier.clickable { onClick(date) }
            .border(
                color = if(date.dayOfWeek == DayOfWeek.SUNDAY) Color.Red else if(date.dayOfWeek == DayOfWeek.SATURDAY) Color.Blue else Color.Black,
                width = 0.5.dp,
            ),
        colors = CardDefaults.cardColors(
            containerColor = if(isSelected) Color(0xFFff0000) else backgroundColor
        ),
        shape = CutCornerShape(0)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = date.year.toString().substring(2, 4) + "/"
            )
            Text(
                text = date.monthValue.toString() + "/",
            )
            Text(
                text = date.dayOfMonth.toString(),
            )
        }
    }
}

@Composable
fun EmptyDateCard() {
    Card(
        modifier = Modifier.background(color = Color.Transparent),
    ) { }
}

