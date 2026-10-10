package com.akas62083.task_app.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    tableName = "task_tag_ref",
    primaryKeys = ["ref_tag_id", "ref_task_id"],
    foreignKeys = [
        ForeignKey(
            entity = SubTagEntity::class,
            parentColumns = arrayOf("sub_id"),
            childColumns = arrayOf("ref_tag_id"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskScheduleEntity::class,
            parentColumns = arrayOf("ta_sc_id"),
            childColumns = arrayOf("ref_task_id"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("ref_tag_id"),
        Index("ref_task_id")
    ]
)
data class TaskTagRef(
    @ColumnInfo("ref_tag_id")
    val tagId: Long = 0,
    @ColumnInfo("ref_task_id")
    val taskId: Long = 0
)

data class TagWithTasks(
    @Embedded
    val tag: SubTagEntity,
    @Relation(
        entity = TaskScheduleEntity::class,
        parentColumn = "sub_id",
        entityColumn = "ta_sc_id",
        associateBy = Junction(
            TaskTagRef::class,
            parentColumn = "ref_tag_id",
            entityColumn = "ref_task_id"
        )
    )
    val tasks: List<TaskScheduleEntity>
)

data class TaskWithTags(
    @Embedded
    val task: TaskScheduleEntity,
    @Relation(
        entity = SubTagEntity::class,
        parentColumn = "ta_sc_id",
        entityColumn = "sub_id",
        associateBy = Junction(
            TaskTagRef::class,
            parentColumn = "ref_task_id",
            entityColumn = "ref_tag_id"
        )
    )
    val tags: List<SubTagEntity>
)