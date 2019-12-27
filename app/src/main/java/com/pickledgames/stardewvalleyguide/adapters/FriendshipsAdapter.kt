package com.pickledgames.stardewvalleyguide.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pickledgames.stardewvalleyguide.fragments.BirthdaysFragment
import com.pickledgames.stardewvalleyguide.fragments.GiftsFragment
import com.pickledgames.stardewvalleyguide.fragments.VillagersFragment

class FriendshipsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments: List<Fragment> = listOf(
            VillagersFragment.newInstance(),
            GiftsFragment.newInstance(),
            BirthdaysFragment.newInstance()
    )

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Villagers"
            1 -> "Gifts"
            2 -> "Birthdays"
            else -> ""
        }
    }
}
