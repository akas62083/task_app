package com.akas62083.task_app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.akas62083.task_app.db.entity.SubTagEntity
import com.akas62083.task_app.db.entity.TaskLogEntity
import com.akas62083.task_app.db.entity.TaskScheduleEntity
import com.akas62083.task_app.db.entity.TaskTagRef

@Database(
    entities = [
        TaskScheduleEntity::class,
        SubTagEntity::class,
        TaskTagRef::class,
        TaskLogEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): Dao
}