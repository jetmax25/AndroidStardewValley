package com.pickledgames.stardewvalleyguide.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Farm ADD COLUMN fishes TEXT NOT NULL DEFAULT ''")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Farm ADD COLUMN museumItems TEXT NOT NULL DEFAULT ''")
        }
    }

    val migrations: Array<Migration> = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
}
