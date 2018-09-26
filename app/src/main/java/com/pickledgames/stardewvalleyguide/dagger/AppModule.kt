package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.StardewApp
import com.pickledgames.stardewvalleyguide.misc.AdManager
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val stardewApp: StardewApp) {

    @Provides
    @Singleton
    fun providesApplication(): StardewApp {
        return stardewApp
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    fun providesAdManager(stardewApp: StardewApp): AdManager {
        return AdManager(stardewApp)
    }
}
