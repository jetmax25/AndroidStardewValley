package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.StardewApp
import com.pickledgames.stardewvalleyguide.database.FarmDao
import com.pickledgames.stardewvalleyguide.repositories.CommunityCenterRepository
import com.pickledgames.stardewvalleyguide.repositories.FarmRepository
import com.pickledgames.stardewvalleyguide.repositories.GiftReactionRepository
import com.pickledgames.stardewvalleyguide.repositories.VillagerRepository
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
    fun providesFarmRepository(farmDao: FarmDao): FarmRepository {
        return FarmRepository(farmDao)
    }

    @Provides
    @Singleton
    fun providesCommunityCenterRepository(stardewApp: StardewApp): CommunityCenterRepository {
        return CommunityCenterRepository(stardewApp)
    }
}
