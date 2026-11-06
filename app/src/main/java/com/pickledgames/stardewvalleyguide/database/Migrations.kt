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

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.query("SELECT id, communityCenterItems, museumItems FROM Farm").use { cursor ->
                val idIndex = cursor.getColumnIndex("id")
                val communityIndex = cursor.getColumnIndex("communityCenterItems")
                val museumIndex = cursor.getColumnIndex("museumItems")
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val community = cursor.getString(communityIndex) ?: ""
                    val museum = cursor.getString(museumIndex) ?: ""
                    val updatedCommunity = ChecklistIdMigration.migrateSet(community)
                    val updatedMuseum = ChecklistIdMigration.migrateSet(museum)
                    if (updatedCommunity != community || updatedMuseum != museum) {
                        database.execSQL(
                                "UPDATE Farm SET communityCenterItems = ?, museumItems = ? WHERE id = ?",
                                arrayOf(updatedCommunity, updatedMuseum, id)
                        )
                    }
                }
            }
        }
    }

    val migrations: Array<Migration> = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
}
