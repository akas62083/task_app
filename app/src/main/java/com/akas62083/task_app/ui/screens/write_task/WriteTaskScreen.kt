package com.akas62083.task_app.ui.screens.write_task

import android.R.attr.onClick
import android.util.Log
import androidx.activity.SystemBarStyle.Companion.auto
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Ro
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.akas62083.task_app.DayOfWeekRule
import com.akas62083.task_app.Greeting
import com.akas62083.task_app.IntervalRule
import com.akas62083.task_app.MainTag
import com.akas62083.task_app.Rules
import com.akas62083.task_app.StartDayAndEndDayRule
import com.akas62083.task_app.ui.screens.write_task.component.SomeDatePicker
import com.akas62083.task_app.ui.theme.Task_appTheme
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun WriteTaskScreen(
    viewModel: WriteTaskViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(

    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 15.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(7.5.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                SelectMainTag(
                    uiState = uiState,
                    selectMainTag = { viewModel.onEvent(WriteTaskEvent.SelectMainTag(it)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(7.5.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.title,
                onValueChange = { viewModel.onEvent(WriteTaskEvent.ChangeTitle(it)) },
                singleLine = true,
                label = { Text(text = "タイトル", color = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = when(uiState.selectedMainTag) {
                        is SelectedMainTag.Task -> Color(0xbbbb0000)
                        is SelectedMainTag.Schedule -> Color(0xbb00bb00)
                        is SelectedMainTag.Someday -> Color(0xbb0000bb)
                    },
                    unfocusedBorderColor = when(uiState.selectedMainTag) {
                        is SelectedMainTag.Task -> Color(0x80bb0000)
                        is SelectedMainTag.Schedule -> Color(0x8000bb00)
                        is SelectedMainTag.Someday -> Color(0x800000bb)
                    }
                )
            )
            Spacer(modifier = Modifier.height(7.5.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.description,
                onValueChange = { viewModel.onEvent(WriteTaskEvent.ChangeDescription(it)) },
                label = { Text(text = "説明(optional)", color = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xff888888),
                    unfocusedBorderColor = Color(0x80888888)
                )
            )
            Spacer(modifier = Modifier.height(17.5.dp))
            when(uiState.selectedMainTag) {
                is SelectedMainTag.Task -> {
                    PickDateOnTask(
                        uiState,
                        onClick = { viewModel.onEvent(WriteTaskEvent.DateTypeChange(it)) },
                        datePickerButtonClicked = { viewModel.onEvent(WriteTaskEvent.DatePickerButtonClicked) },
                        onDateClick = { viewModel.onEvent(WriteTaskEvent.DateClick(it)) },
                        sliderValueChange = { viewModel.onEvent(WriteTaskEvent.SliderValueChange(it)) },
                        leftArrowClicked = { viewModel.onEvent(WriteTaskEvent.LeftArrowClicked) },
                        rightArrowClicked = { viewModel.onEvent(WriteTaskEvent.RightArrowClicked) }
                    )
                }
                is SelectedMainTag.Schedule -> {
                    PickDateOnSchedule(
                        uiState = uiState,
                        onClick = { viewModel.onEvent(WriteTaskEvent.DateTypeChange(it)) },
                        datePickerButtonClicked = { viewModel.onEvent(WriteTaskEvent.DatePickerButtonClicked) },
                        onDateClick = { viewModel.onEvent(WriteTaskEvent.DateClick(it)) },
                        leftArrowClicked = { viewModel.onEvent(WriteTaskEvent.LeftArrowClicked) },
                        rightArrowClicked = { viewModel.onEvent(WriteTaskEvent.RightArrowClicked) }
                    )
                }
                is SelectedMainTag.Someday -> {
                    PickDateOnSomeday(
                        uiState
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        viewModel.onEvent(WriteTaskEvent.SaveButtonClicked)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when(uiState.selectedMainTag) {
                            is SelectedMainTag.Task -> Color(0x80bb0000)
                            is SelectedMainTag.Schedule -> Color(0x8000bb00)
                            is SelectedMainTag.Someday -> Color(0x800000bb)
                        }
                    )
                ) {
                    Text("保存")
                }
            }
        }
    }
}

@Composable
private fun SelectMainTag(
    uiState: WriteTaskUiState,
    selectMainTag: (MainTag) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val taskColor by animateColorAsState(if(uiState.selectedMainTag is SelectedMainTag.Task) Color(0x80ff0000) else Color.Transparent)
        val scheduleColor by animateColorAsState(if(uiState.selectedMainTag is SelectedMainTag.Schedule) Color(0x8000ff00) else Color.Transparent)
        val somedayColor by animateColorAsState(if(uiState.selectedMainTag is SelectedMainTag.Someday) Color(0x800000ff) else Color.Transparent)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = CutCornerShape(5.dp),
            modifier = Modifier.border(
                color = Color.Red,
                width = 1.dp,
                shape = CutCornerShape(5.dp)
            )
                .background(
                    color = taskColor,
                    shape = CutCornerShape(5.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .clickable(
                    interactionSource = null,
                    indication = null
                ) { selectMainTag(MainTag.Task) }
        ) {
            Text(
                text = "Task",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = CutCornerShape(5.dp),
            modifier = Modifier.border(
                color = Color.Green,
                width = 1.dp,
                shape = CutCornerShape(5.dp)
            )
                .background(
                    color = scheduleColor,
                    shape = CutCornerShape(5.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .clickable(
                    interactionSource = null,
                    indication = null
                ) { selectMainTag(MainTag.Schedule) }
        ) {
            Text(
                text = "Schedule",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = CutCornerShape(5.dp),
            modifier = Modifier.border(
                color = Color.Blue,
                width = 1.dp,
                shape = CutCornerShape(5.dp)
            )
                .background(
                    color = somedayColor,
                    shape = CutCornerShape(5.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .clickable(
                    interactionSource = null,
                    indication = null
                ) { selectMainTag(MainTag.Someday) }
        ) {
            Text(
                text = "Someday",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickDateOnTask(
    uiState: WriteTaskUiState,
    modifier: Modifier = Modifier,
    onClick: (DateType) -> Unit,
    datePickerButtonClicked: () -> Unit,
    onDateClick: (LocalDate) -> Unit,
    sliderValueChange: (Float) -> Unit,
    leftArrowClicked: () -> Unit,
    rightArrowClicked: () -> Unit
) {
    val selectedTask = uiState.selectedMainTag as? SelectedMainTag.Task
    val dateType = selectedTask?.dateType
    val tabIndex = if (dateType is DateType.Single) 0 else 1
    val brush = Brush.horizontalGradient(
        listOf(Color(0x40bb0000), Color(0xffee0000))
    )
    Box(modifier) {
        Column(modifier = Modifier) {
            TabRow(
                selectedTabIndex = tabIndex,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[tabIndex])
                            .height(3.dp)
                            .padding(start = 20.dp, end = 20.dp)
                            .background(color = Color(0xbbbb0000), RoundedCornerShape(100, 100, 0, 0))
                    )
                }
            ) {
                Tab(
                    selectedContentColor = Color(0xbbbb0000),
                    unselectedContentColor = Color.Transparent,
                    selected = tabIndex == 0,
                    onClick = {
                        onClick(DateType.Single(listOf()))
                    },
                    text = {
                        Text(
                            text = "日付を選択",
                            color = Color(0xbbbb0000),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                Tab(
                    selectedContentColor = Color(0xbbbb0000),
                    unselectedContentColor = Color.Transparent,
                    selected = tabIndex == 1,
                    onClick = {
                        onClick(DateType.Routine(Rules(listOf()))) // 今までの設定を放棄するか?という注意を表示する(したい)。
                    },
                    text = {
                        Text(
                            text = "ルーチンを設定",
                            color = Color(0xbbbb0000),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedContent(
                targetState = tabIndex,
            ) { targetState ->
                when (targetState) {
                    0 -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0x20ff0000)
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(5.dp)
                            ) {
                                val singleDateType = dateType as? DateType.Single
                                singleDateType?.dates?.forEach { date ->
                                    Text(
                                        text = "- $date",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { datePickerButtonClicked() }) {
                                        Text(
                                            text = if(singleDateType?.dates?.size != 0) "日付を変更" else "日付を追加",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        val routineDateType = dateType as? DateType.Routine
                        val rules = routineDateType?.rules?.rules
                        val startDayAndEndDayRule = rules?.filterIsInstance<StartDayAndEndDayRule>()?.firstOrNull()
                        val intervalRule = rules?.filterIsInstance<IntervalRule>()?.firstOrNull()
                        val dayOfWeekRule = rules?.filterIsInstance<DayOfWeekRule>()?.firstOrNull()
                        Row {
                            Text(startDayAndEndDayRule?.startDay.toString() + "~" + startDayAndEndDayRule?.endDay?.toString())

                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x20ff0000)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(5.dp)
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    if (dateType is DateType.Single && dateType.dates.size == 1) {
                        Text(
                            text = "開始日：" + dateType.dates[0].minusDays(selectedTask.startTime.toLong()),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = "開始日：" + selectedTask?.startTime + "日前",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Slider(
                        value = selectedTask?.startTime?.toFloat() ?: 0f,
                        onValueChange = { sliderValueChange(it) },
                        valueRange = 0f..21f,
                        thumb = { },
                        track = { sliderState ->
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp) // トラックの太さ
                            ) {
                                val trackHeight = size.height
                                val width = size.width
                                val fraction =
                                    sliderState.value / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                                drawRoundRect(
                                    color = Color.LightGray.copy(alpha = 0.3f),
                                    size = size,
                                    cornerRadius = CornerRadius(trackHeight / 2)
                                )
                                drawRoundRect(
                                    brush = brush,
                                    size = size.copy(width = width * fraction),
                                    cornerRadius = CornerRadius(trackHeight / 2)
                                )
                            }
                        },
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }
        }
        if ((dateType as? DateType.Single)?.isDatePickerOpened == true) {
            SomeDatePicker(
                uiState = uiState,
                onDateClick = { onDateClick(it) },
                confirm = { datePickerButtonClicked() },
                leftArrowClicked = { leftArrowClicked() },
                rightArrowClicked = { rightArrowClicked() },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickDateOnSchedule(
    uiState: WriteTaskUiState,
    modifier: Modifier = Modifier,
    onClick: (DateType) -> Unit,
    datePickerButtonClicked: () -> Unit,
    onDateClick: (LocalDate) -> Unit,
    leftArrowClicked: () -> Unit,
    rightArrowClicked: () -> Unit
) {
    val selectedTask = uiState.selectedMainTag as? SelectedMainTag.Schedule
    val dateType = selectedTask?.dateType
    val tabIndex = if (dateType is DateType.Single) 0 else 1
    Box(modifier) {
        Column(modifier = Modifier) {
            TabRow(
                selectedTabIndex = tabIndex,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[tabIndex])
                            .height(3.dp)
                            .padding(start = 20.dp, end = 20.dp)
                            .background(color = Color(0xbb00bb00), RoundedCornerShape(100, 100, 0, 0))
                    )
                }
            ) {
                Tab(
                    selectedContentColor = Color(0xbb00bb00),
                    unselectedContentColor = Color.Transparent,
                    selected = tabIndex == 0,
                    onClick = {
                        onClick(DateType.Single(listOf()))
                    },
                    text = {
                        Text(
                            text = "日付を選択",
                            color = Color(0xbb00bb00),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                Tab(
                    selectedContentColor = Color(0xbb00bb00),
                    unselectedContentColor = Color.Transparent,
                    selected = tabIndex == 1,
                    onClick = {
                        onClick(DateType.Routine(Rules(listOf()))) // 今までの設定を放棄するか?という注意を表示する(したい)。
                    },
                    text = {
                        Text(
                            text = "ルーチンを設定",
                            color = Color(0xbb00bb00),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedContent(
                targetState = tabIndex,
            ) { targetState ->
                when (targetState) {
                    0 -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0x2000ff00)
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(5.dp)
                            ) {
                                val singleDateType = dateType as? DateType.Single
                                singleDateType?.dates?.forEach { date ->
                                    Text(
                                        text = "- $date",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { datePickerButtonClicked() }) {
                                        Text(
                                            text = if(singleDateType?.dates?.size != 0) "日付を変更" else "日付を追加",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        Column {
                            Row {
                            }
                        }
                    }
                }
            }
        }
        if ((dateType as? DateType.Single)?.isDatePickerOpened == true) {
            SomeDatePicker(
                uiState = uiState,
                onDateClick = { onDateClick(it) },
                confirm = { datePickerButtonClicked() },
                leftArrowClicked = { leftArrowClicked() },
                rightArrowClicked = { rightArrowClicked() }
            )
        }
    }
}

@Composable
private fun PickDateOnSomeday(
    uiState: WriteTaskUiState,
    modifier: Modifier = Modifier
) {

}