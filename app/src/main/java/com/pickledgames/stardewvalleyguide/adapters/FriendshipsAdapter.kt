package com.pickledgames.stardewvalleyguide.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.pickledgames.stardewvalleyguide.fragments.BirthdaysFragment
import com.pickledgames.stardewvalleyguide.fragments.GiftsFragment
import com.pickledgames.stardewvalleyguide.fragments.VillagersFragment

class FriendshipsAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

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
