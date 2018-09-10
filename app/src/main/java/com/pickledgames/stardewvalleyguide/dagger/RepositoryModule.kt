package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.StardewApp
import com.pickledgames.stardewvalleyguide.repository.VillagerRepository
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
}
