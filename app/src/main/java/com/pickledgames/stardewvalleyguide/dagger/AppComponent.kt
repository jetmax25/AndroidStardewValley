package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.StardewApp
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    DatabaseModule::class,
    FragmentModule::class,
    RepositoryModule::class
])
@Singleton
interface AppComponent {
    fun inject(stardewApp: StardewApp)
}
