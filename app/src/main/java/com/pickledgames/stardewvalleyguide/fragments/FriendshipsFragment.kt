package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pickledgames.stardewvalleyguide.adapters.FriendshipsAdapter
import com.pickledgames.stardewvalleyguide.databinding.FragmentFriendshipsBinding

class FriendshipsFragment : Fragment() {

    private lateinit var binding: FragmentFriendshipsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentFriendshipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    private fun setup() {
        binding.friendshipsViewPager.adapter = FriendshipsAdapter(childFragmentManager)
        binding.friendshipsTabLayout.setupWithViewPager(binding.friendshipsViewPager)
    }

    companion object {
        fun newInstance(): FriendshipsFragment {
            return FriendshipsFragment()
        }
    }
}
