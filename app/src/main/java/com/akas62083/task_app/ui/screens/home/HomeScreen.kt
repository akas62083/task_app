package com.akas62083.task_app.ui.screens.home

import android.graphics.ComposeShader
import android.graphics.PorterDuff
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.akas62083.task_app.Route
import com.akas62083.task_app.ui.screens.home.component.DateDialog
import com.akas62083.task_app.ui.screens.home.component.TaskCard
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.LocalDate

/*
**構成**

Scaffold(
    fab() // タスク追加ボタン
) {
    SharedTransitionLayout {
        Box {
            Column {
                Box {
                    Column {
                        LazyVerticalGrid {
                            Card() // 裏の日付カード
                        }
                    }
                    Column {
                        AnimatedContent {
                            LazyVerticalGrid {
                                TaskCard() // 日付カード
                            }
                        }
                    }
                }
                LazyColumn {
                    TaskTextList() // タスク一覧表示
                }
            }
            AnimatedContent {
                DateDialog() // 日付ダイアログ
            }
        }
    }
}
*/
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toArgb
import com.akas62083.task_app.ui.screens.home.component.NormalTaskTextList
import com.akas62083.task_app.ui.screens.home.component.TaskTextList

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val dateCardOffsetX = remember { Animatable(0f) }
    val dateCardOffsetY = remember { Animatable(0f) }
    val dateListOffsetX = remember { Animatable(0f) }
    val dateListOffsetY = remember { Animatable(0f) }
    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Route.Write(null)) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->
        SharedTransitionLayout {
            Box(
                modifier = Modifier.padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp)
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Box {
                        Column(modifier = Modifier) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(7),
                            ) {
                                items(uiState.dateListBackUp) { date ->
                                    if(date != null) {
                                        Spacer(
                                            modifier = Modifier.weight(1f)
                                                .aspectRatio(0.618f)
                                                .padding(2.5.dp)
                                                .background(
                                                    color = Color(0x80d4d3da),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                        )
                                    } else {
                                        Spacer(
                                            modifier = Modifier.weight(1f)
                                                .aspectRatio(0.618f),
                                        )
                                    }
                                }
                            }
                        }
                        Column(modifier = Modifier) {
                            AnimatedContent(
                                targetState = uiState.dateList
                            ) { dates ->
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(7),
                                ) {
                                    items(dates) { date ->
                                        if (date != null) {
                                            TaskCard(
                                                uiState = uiState,
                                                modifier = Modifier.weight(1f)
                                                    .aspectRatio(0.618f)
                                                    .padding(2.5.dp)
                                                    .sharedBounds(
                                                        rememberSharedContentState(date.toString()),
                                                        animatedVisibilityScope = this@AnimatedContent
                                                    ),
                                                date = date,
                                                onClick = {
                                                    viewModel.onEvent(
                                                        HomeEvent.DateCardClicked(
                                                            it
                                                        )
                                                    )
                                                }
                                            )
                                        } else {
                                            Spacer(
                                                modifier = Modifier.weight(1f)
                                                    .aspectRatio(0.618f),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NormalTaskTextList(
                            uiState = uiState,
                            modifier = Modifier.pointerInput(screenWidthPx) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        scope.launch {
                                            dateListOffsetX.snapTo(dateListOffsetX.value + (dragAmount.x / 2))
                                        }
                                    },
                                    onDragEnd = {
                                        scope.launch {
                                            if (dateListOffsetX.value > screenWidthPx / 10) {
                                                if(!uiState.normalTaskList) {

                                                }
                                                dateListOffsetX.animateTo(0f)
                                            } else if(dateListOffsetX.value < -screenWidthPx / 10) {
                                                if(uiState.normalTaskList) {

                                                }
                                                dateListOffsetX.animateTo(0f)
                                            } else {
                                                dateListOffsetX.animateTo(0f)
                                            }
                                        }
                                    }
                                )
                            }.offset(
                                x = if(uiState.normalTaskList) dateListOffsetX.value.dp else dateListOffsetX.value.dp - screenWidthPx.dp,
                                y = dateListOffsetY.value.dp
                            )
                        )
                        //TaskTextList( // なんの関数か分からん。上のとほぼ同じやん。
                        //    uiState = uiState,
                        //    modifier = Modifier.pointerInput(screenWidthPx) {
                        //        detectDragGestures(
                        //            onDrag = { change, dragAmount ->
                        //                change.consume()
                        //                scope.launch {
                        //                    dateListOffsetX.snapTo(dateListOffsetX.value + (dragAmount.x / 2))
                        //                }
                        //            },
                        //            onDragEnd = {
                        //                scope.launch {
                        //                    if (dateListOffsetX.value > screenWidthPx / 10) {
                        //                        if(!uiState.normalTaskList) {
                        //
                        //                        }
                        //                        dateListOffsetX.animateTo(0f)
                        //                    } else if(dateListOffsetX.value < -screenWidthPx / 10) {
                        //                        if(uiState.normalTaskList) {
                        //
                        //                        }
                        //                        dateListOffsetX.animateTo(0f)
                        //                    } else {
                        //                        dateListOffsetX.animateTo(0f)
                        //                    }
                        //                }
                        //            }
                        //        )
                        //    }.offset(
                        //        x = if(uiState.normalTaskList) dateListOffsetX.value.dp.plus(screenWidthPx.dp) else dateListOffsetX.value.dp,
                        //        y = dateListOffsetY.value.dp
                        //    )
                        //)
                    }
                }
                AnimatedContent(
                    modifier = Modifier.fillMaxSize(),
                    targetState = uiState.clickedDate
                ) { state ->
                    if (state != null) {
                        val dateValue: LocalDate = state
                        Box(modifier = Modifier.fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { viewModel.onEvent(HomeEvent.DateCardClicked(null)) },
                            contentAlignment = Alignment.Center
                        ) {
                            DateDialog(
                                uiState = uiState,
                                taskClicked = { viewModel.onEvent(HomeEvent.TaskClicked(it)) },
                                modifier = Modifier.fillMaxWidth()
                                    .aspectRatio(0.618f)
                                    .padding(all = 50.dp)
                                    .pointerInput(screenWidthPx) {
                                        detectDragGestures(
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                scope.launch {
                                                    dateCardOffsetX.snapTo(dateCardOffsetX.value + dragAmount.x)
                                                    dateCardOffsetY.snapTo(dateCardOffsetY.value + dragAmount.y)
                                                }
                                            },
                                            onDragEnd = {
                                                scope.launch {
                                                    if (dateCardOffsetX.value > screenWidthPx / 10) {
                                                        if(uiState.leftArrowEnabled) {
                                                            viewModel.onEvent(HomeEvent.LeftArrowClicked)
                                                        }
                                                        dateCardOffsetX.animateTo(0f)
                                                        dateCardOffsetY.animateTo(0f)
                                                    } else if (dateCardOffsetX.value < -screenWidthPx / 10) {
                                                        if (uiState.rightArrowEnabled) {
                                                            viewModel.onEvent(HomeEvent.RightArrowClicked)
                                                        }
                                                        dateCardOffsetX.animateTo(0f)
                                                        dateCardOffsetY.animateTo(0f)
                                                    } else if(dateCardOffsetY.value > screenHeightPx / 20) {
                                                        if(uiState.clickedDate == LocalDate.now().plusWeeks(1)) {
                                                            viewModel.onEvent(HomeEvent.DateCardClicked(LocalDate.now()))
                                                        }
                                                        dateCardOffsetX.animateTo(0f)
                                                        dateCardOffsetY.animateTo(0f)
                                                    } else if (dateCardOffsetY.value < -screenHeightPx / 20) {
                                                        if(uiState.clickedDate == LocalDate.now()) {
                                                            viewModel.onEvent(HomeEvent.DateCardClicked(LocalDate.now().plusWeeks(1)))
                                                        }
                                                        dateCardOffsetX.animateTo(0f)
                                                        dateCardOffsetY.animateTo(0f)
                                                    } else {
                                                        dateCardOffsetX.animateTo(0f)
                                                        dateCardOffsetY.animateTo(0f)
                                                    }
                                                }
                                            }
                                        )
                                    }
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(dateValue.toString()),
                                        animatedVisibilityScope = this@AnimatedContent,
                                        zIndexInOverlay = 1f
                                    ),
                                addTask = { navController.navigate(Route.Write(dateValue.toString())) },
                                rightArrowClicked = { viewModel.onEvent(HomeEvent.RightArrowClicked) },
                                leftArrowClicked = { viewModel.onEvent(HomeEvent.LeftArrowClicked) },
                                dateValue = dateValue
                            )
                        }
                    }
                }
            }
        }
    }
}
