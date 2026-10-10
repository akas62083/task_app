package com.akas62083.task_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akas62083.task_app.db.entity.SubTagEntity
import com.akas62083.task_app.remote.PostService
import com.akas62083.task_app.remote.dto.PostResponse
import com.akas62083.task_app.ui.screens.home.HomeScreen
import com.akas62083.task_app.ui.screens.home.HomeViewModel
import com.akas62083.task_app.ui.screens.write_task.WriteTaskScreen
import com.akas62083.task_app.ui.screens.write_task.WriteTaskViewModel
import com.akas62083.task_app.ui.theme.Task_appTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okhttp3.Response
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Task_appTheme {
                Greeting()
            }
        }
    }
}

@Composable
fun Greeting() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {
        composable<Route.Home>() {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(viewModel, navController)
        }
        composable<Route.Write>() {
            val viewModel: WriteTaskViewModel = hiltViewModel()
            WriteTaskScreen(viewModel, navController)
        }
    }
}

object Route {
    @Serializable
    data object Home
    @Serializable
    data class Write(val date: String? = null)
}

sealed interface TaskAndSchedule {
    data class Task(
        val id: Long,
        val title: String,
        val description: String,
        val mainTag: MainTag,
        val date: LocalDate,
        val status: Status,
        val startDate: LocalDate,
        val tags: Set<SubTagEntity>
    ): TaskAndSchedule
    data class Schedule(
        val id: Long,
        val title: String,
        val description: String,
        val mainTag: MainTag,
        val date: LocalDate,
        val status: Status,
        val tags: Set<SubTagEntity>
    ): TaskAndSchedule
    data class Someday(
        val id: Long,
        val title: String,
        val description: String,
        val mainTag: MainTag,
    ): TaskAndSchedule
}
enum class MainTag {
    Task,
    Schedule,
    Someday
}
enum class Status {
    NotDone,
    Done,
    Hide
}


sealed interface Rule
data class DayOfWeekRule(
    val days: Set<DayOfWeek>,
): Rule
data class IntervalRule(
    val interval: Int
): Rule
data class StartDayAndEndDayRule(
    val startDay: LocalDate = LocalDate.MIN,
    val endDay: LocalDate = LocalDate.MAX
): Rule
data class Rules(
    val rules: List<Rule>
): Rule

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Task_appTheme {
        Greeting()
    }
}