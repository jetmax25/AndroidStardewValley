package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity

open class InnerBaseFragment : BaseFragment() {

    lateinit var savedTitle: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return v
    }

    override fun onPause() {
        super.onPause()
        reset()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.title = savedTitle
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun reset() {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).supportActionBar?.title = (activity as MainActivity).resources.getString(R.string.app_name)
    }

    protected fun setTitle(title: String) {
        (activity as MainActivity).supportActionBar?.title = title
    }

    protected fun setTitle(@StringRes titleId: Int) {
        (activity as MainActivity).supportActionBar?.setTitle(titleId)
        savedTitle = (activity as MainActivity).getString(titleId)
    }
}
