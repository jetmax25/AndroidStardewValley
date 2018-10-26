package com.pickledgames.stardewvalleyguide.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.pickledgames.stardewvalleyguide.fragments.CommunityCenterFragment
import com.pickledgames.stardewvalleyguide.fragments.FishingFragment

class ChecklistsAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    private val fragments: List<Fragment> = listOf(
            CommunityCenterFragment.newInstance(),
            FishingFragment.newInstance()
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
            else -> ""
        }
    }

    companion object {
        const val COMMUNITY_CENTER = 0
        const val FISHING = 1
    }
}
