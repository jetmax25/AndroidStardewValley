package com.pickledgames.stardewvalleyguide.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pickledgames.stardewvalleyguide.StardewApp
import com.pickledgames.stardewvalleyguide.models.Farm

@Database(entities = [Farm::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StardewDatabase : RoomDatabase() {

    abstract fun getFarmDao(): FarmDao

    companion object {
        private const val DATABASE_NAME = "STARDEW"

        fun buildDatabase(stardewApp: StardewApp): StardewDatabase {
            return Room.databaseBuilder<StardewDatabase>(
                    stardewApp.applicationContext,
                    StardewDatabase::class.java,
                    DATABASE_NAME
            )
                    .addMigrations(*Migrations.migrations)
                    .build()
        }
    }
}
