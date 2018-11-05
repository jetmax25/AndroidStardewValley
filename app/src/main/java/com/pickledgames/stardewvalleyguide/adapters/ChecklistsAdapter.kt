package com.pickledgames.stardewvalleyguide.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.pickledgames.stardewvalleyguide.fragments.CommunityCenterFragment
import com.pickledgames.stardewvalleyguide.fragments.FishingFragment
import com.pickledgames.stardewvalleyguide.fragments.MuseumFragment

class ChecklistsAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    private val fragments: List<Fragment> = listOf(
            CommunityCenterFragment.newInstance(),
            FishingFragment.newInstance(),
            MuseumFragment.newInstance()
    )

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            COMMUNITY_CENTER -> "Community Center"
            FISHING -> "Fishing"
            MUSEUM -> "Museum"
            else -> ""
        }
    }

    companion object {
        const val COMMUNITY_CENTER = 0
        const val FISHING = 1
        const val MUSEUM = 2
    }
}
