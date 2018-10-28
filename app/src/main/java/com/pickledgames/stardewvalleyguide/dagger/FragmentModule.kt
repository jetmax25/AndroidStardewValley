package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun baseFragment(): BaseFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun innerBaseFragment(): InnerBaseFragment

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

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun communityCenterFragment(): CommunityCenterFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun editFarmsFragment(): EditFarmsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun communityCenterItemFragment(): CommunityCenterItemFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun purchasesFragment(): PurchasesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun fishingFragment(): FishingFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun fishFragment(): FishFragment
}
