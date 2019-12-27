package com.pickledgames.stardewvalleyguide

import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.ads.MobileAds
import com.jakewharton.threetenabp.AndroidThreeTen
import com.pickledgames.stardewvalleyguide.dagger.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.fabric.sdk.android.Fabric
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
        MobileAds.initialize(this, "ca-app-pub-5594325776314197~2267317062")
        AndroidThreeTen.init(this)
        Fabric.with(this, Crashlytics())
        Fresco.initialize(this)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    companion object {
        const val PREFS = "PREFS"
    }
}
