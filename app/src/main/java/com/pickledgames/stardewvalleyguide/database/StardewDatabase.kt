package com.pickledgames.stardewvalleyguide.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.pickledgames.stardewvalleyguide.StardewApp
import com.pickledgames.stardewvalleyguide.models.Farm

@Database(entities = [Farm::class], version = 1, exportSchema = false)
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
            ).build()
        }
    }
}
