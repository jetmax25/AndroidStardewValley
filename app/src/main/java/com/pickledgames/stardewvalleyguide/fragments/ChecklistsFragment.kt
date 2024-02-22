package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pickledgames.stardewvalleyguide.adapters.ChecklistsAdapter
import com.pickledgames.stardewvalleyguide.databinding.FragmentChecklistsBinding

class ChecklistsFragment : Fragment() {

    private lateinit var binding: FragmentChecklistsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentChecklistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    private fun setup() {
        binding.checklistsViewPager.adapter = ChecklistsAdapter(childFragmentManager)
        binding.checklistsTabLayout.setupWithViewPager(binding.checklistsViewPager)
    }

    companion object {
        fun newInstance(): ChecklistsFragment {
            return ChecklistsFragment()
        }
    }
}
