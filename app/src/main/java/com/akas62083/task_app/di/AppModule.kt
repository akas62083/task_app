package com.akas62083.task_app.di

import android.R.attr.level
import android.content.Context
import androidx.room.Room
import com.akas62083.task_app.db.AppDatabase
import com.akas62083.task_app.db.Dao
import com.akas62083.task_app.db.MIGRATION_1_2
import com.akas62083.task_app.remote.PostService
import com.akas62083.task_app.remote.PostServiceImpl
import com.akas62083.task_app.usecase.CreateNewScheduleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.prefs.Preferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
        .build()

    @Provides
    fun provideDao(database: AppDatabase): Dao = database.dao()

    @Provides
    fun provideClient(): HttpClient = HttpClient(Android) {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    @Provides
    @Singleton
    fun providePostService(client: HttpClient): PostService {
        return PostServiceImpl(client)
    }

}