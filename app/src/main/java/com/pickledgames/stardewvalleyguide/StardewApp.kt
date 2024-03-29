package com.pickledgames.stardewvalleyguide

import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.jakewharton.threetenabp.AndroidThreeTen
import com.pickledgames.stardewvalleyguide.dagger.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class StardewApp : MultiDexApplication(), HasAndroidInjector {

    @Inject lateinit var androidInjector : DispatchingAndroidInjector<Any>
    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .databaseModule(DatabaseModule())
                .repositoryModule(RepositoryModule())
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
        AndroidThreeTen.init(this)
        Fresco.initialize(this)
        initMobileAds()
    }


    private fun initMobileAds() {
        MobileAds.initialize(this) {}
        val requestConfiguration = RequestConfiguration.Builder()
            .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    companion object {
        const val PREFS = "PREFS"
    }
}
