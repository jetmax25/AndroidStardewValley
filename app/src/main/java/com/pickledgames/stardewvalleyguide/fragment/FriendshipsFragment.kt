package com.pickledgames.stardewvalleyguide.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.adapter.FriendshipsAdapter
import kotlinx.android.synthetic.main.fragment_friendships.*

class FriendshipsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friendships, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    private fun setup() {
        friendships_view_pager.adapter = FriendshipsAdapter(fragmentManager)
        friendships_tab_layout.setupWithViewPager(friendships_view_pager)
    }

    companion object {
        fun newInstance(): FriendshipsFragment {
            return FriendshipsFragment()
        }
    }
}
