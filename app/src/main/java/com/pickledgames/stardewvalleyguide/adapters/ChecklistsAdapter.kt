package com.pickledgames.stardewvalleyguide.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pickledgames.stardewvalleyguide.fragments.CommunityCenterFragment
import com.pickledgames.stardewvalleyguide.fragments.FishingFragment
import com.pickledgames.stardewvalleyguide.fragments.MuseumFragment

class ChecklistsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

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
