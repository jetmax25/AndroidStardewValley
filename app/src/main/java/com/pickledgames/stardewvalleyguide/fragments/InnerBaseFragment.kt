package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity

abstract class InnerBaseFragment : BaseFragment() {

    private var savedTitle: String? = null
    private lateinit var appTitle: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appTitle = (activity as MainActivity).resources.getString(R.string.app_name)
        return v
    }

    override fun onPause() {
        super.onPause()
        reset()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.title = savedTitle ?: appTitle
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun reset() {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).supportActionBar?.title = appTitle
    }

    protected fun setTitle(title: String) {
        (activity as MainActivity).supportActionBar?.title = title
        savedTitle = title
    }

    protected fun setTitle(@StringRes titleId: Int) {
        (activity as MainActivity).supportActionBar?.setTitle(titleId)
        savedTitle = (activity as MainActivity).getString(titleId)
    }
}
