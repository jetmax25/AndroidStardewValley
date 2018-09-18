package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.fragments.*
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

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun giftFragment(): GiftFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun birthdaysFragment(): BirthdaysFragment
}
