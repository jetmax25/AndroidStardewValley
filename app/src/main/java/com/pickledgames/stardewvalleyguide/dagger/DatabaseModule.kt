package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.StardewApp
import com.pickledgames.stardewvalleyguide.database.FarmDao
import com.pickledgames.stardewvalleyguide.database.StardewDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesStardewDatabase(stardewApp: StardewApp): StardewDatabase {
        return StardewDatabase.buildDatabase(stardewApp)
    }

    @Provides
    @Singleton
    fun providesGameLimitDao(stardewDatabase: StardewDatabase): FarmDao {
        return stardewDatabase.getFarmDao()
    }
}
