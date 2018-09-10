package com.pickledgames.stardewvalleyguide.dagger

import com.pickledgames.stardewvalleyguide.fragment.VillagersFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindVillagersFragment(): VillagersFragment
}
