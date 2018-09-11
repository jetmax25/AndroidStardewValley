package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity

open class InnerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return v
    }

    override fun onStop() {
        super.onStop()
        reset()
    }

    private fun reset() {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).supportActionBar?.title = (activity as MainActivity).resources.getString(R.string.app_name)
    }

    protected fun setTitle(title: String) {
        (activity as MainActivity).supportActionBar?.title = title
    }
}
