package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.fragments.GiftsFragment
import com.pickledgames.stardewvalleyguide.fragments.VillagerFragment
import com.pickledgames.stardewvalleyguide.fragments.VillagersFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun villagersFragment(): VillagersFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun villagerFragment(): VillagerFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun giftsFragment(): GiftsFragment
}
