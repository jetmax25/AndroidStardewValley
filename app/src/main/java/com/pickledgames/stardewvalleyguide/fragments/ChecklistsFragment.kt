package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.adapters.ChecklistsAdapter
import kotlinx.android.synthetic.main.fragment_checklists.*

class ChecklistsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_checklists, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    private fun setup() {
        checklists_view_pager?.adapter = ChecklistsAdapter(childFragmentManager)
        checklists_tab_layout?.setupWithViewPager(checklists_view_pager)
    }

    companion object {
        fun newInstance(): ChecklistsFragment {
            return ChecklistsFragment()
        }
    }
}
