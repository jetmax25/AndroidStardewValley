package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.StardewApp
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.PurchasesManager
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
    fun providesAdsManager(stardewApp: StardewApp, purchasesManager: PurchasesManager): AdsManager {
        return AdsManager(stardewApp, purchasesManager)
    }

    @Provides
    @Singleton
    fun providesPurchasesManager(stardewApp: StardewApp): PurchasesManager {
        return PurchasesManager(stardewApp)
    }
}
