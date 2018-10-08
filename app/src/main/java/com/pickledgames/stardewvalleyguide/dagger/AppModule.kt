package com.pickledgames.stardewvalleyguide.dagger

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.pickledgames.stardewvalleyguide.StardewApp
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.managers.LoginManager
import com.pickledgames.stardewvalleyguide.managers.PurchasesManager
import com.squareup.moshi.Moshi
import dagger.Lazy
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
    fun providesSharedPreferences(): SharedPreferences {
        return stardewApp.getSharedPreferences(StardewApp.PREFS, MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providesAdsManager(stardewApp: StardewApp, purchasesManager: PurchasesManager): AdsManager {
        return AdsManager(stardewApp, purchasesManager)
    }

    @Provides
    @Singleton
    fun providesPurchasesManager(stardewApp: StardewApp, analyticsManager: Lazy<AnalyticsManager>): PurchasesManager {
        return PurchasesManager(stardewApp, analyticsManager)
    }

    @Provides
    @Singleton
    fun providesAnalyticsManager(stardewApp: StardewApp, loginManager: LoginManager, purchasesManager: PurchasesManager): AnalyticsManager {
        return AnalyticsManager(stardewApp, loginManager, purchasesManager)
    }

    @Provides
    fun providesLoginManager(sharedPreferences: SharedPreferences): LoginManager {
        return LoginManager(sharedPreferences)
    }
}
