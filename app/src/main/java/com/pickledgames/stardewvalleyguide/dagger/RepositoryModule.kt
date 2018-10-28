package com.pickledgames.stardewvalleyguide.dagger

import android.content.SharedPreferences
import com.pickledgames.stardewvalleyguide.StardewApp
import com.pickledgames.stardewvalleyguide.database.FarmDao
import com.pickledgames.stardewvalleyguide.repositories.*
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun providesVillagerModule(stardewApp: StardewApp, moshi: Moshi): VillagerRepository {
        return VillagerRepository(stardewApp, moshi)
    }

    @Provides
    @Singleton
    fun providesGiftReactionRepository(stardewApp: StardewApp): GiftReactionRepository {
        return GiftReactionRepository(stardewApp)
    }

    @Provides
    @Singleton
    fun providesFarmRepository(farmDao: FarmDao, sharedPreferences: SharedPreferences): FarmRepository {
        return FarmRepository(farmDao, sharedPreferences)
    }

    @Provides
    @Singleton
    fun providesCommunityCenterRepository(stardewApp: StardewApp): CommunityCenterRepository {
        return CommunityCenterRepository(stardewApp)
    }

    @Provides
    @Singleton
    fun providesFishRepository(stardewApp: StardewApp): FishRepository {
        return FishRepository(stardewApp)
    }
}
