package com.akas62083.task_app.db.entity

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sub_tag_entity")
data class SubTagEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("sub_id")
    val id: Long = 0,
    @ColumnInfo("sub_title")
    val title: String,
    @ColumnInfo("sub_color")
    val color: Int
)