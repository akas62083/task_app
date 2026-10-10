package com.akas62083.task_app.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE task_new (
                new_ta_sc_id INTEGER PRIMARY KEY NOT NULL,
                new_ta_sc_title TEXT NOT NULL,
                new_ta_sc_description TEXT NOT NULL,
                new_ta_sc_main INTEGER NOT NULL,
                new_ta_sc_date INTEGER,
                new_ta_sc_routine TEXT,
                new_ta_sc_start_time INTEGER
            )
        """.trimIndent())

        db.execSQL("""
            INSERT INTO task_new (new_ta_sc_id, new_ta_sc_title, new_ta_sc_description, new_ta_sc_main, new_ta_sc_date, new_ta_sc_routine, new_ta_sc_start_time))
            SELECT ta_sc_id, ta_sc_title, ta_sc_description, ta_sc_main, ta_sc_date, ta_sc_routine, ta_sc_start_time
            FROM task_schedule_entity
        """.trimIndent())

        db.execSQL("""
            DROP TABLE task_schedule_entity
        """.trimIndent())

        db.execSQL("""
            ALTER TABLE task_new RENAME TO task_schedule_entity
        """.trimIndent())
    }
}