package com.pickledgames.stardewvalleyguide.utils

import android.content.res.Resources
import android.support.constraint.Group
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.pickledgames.stardewvalleyguide.R

object FragmentUtil {

    fun setupSearchView(m: MenuItem?, q: SearchView.OnQueryTextListener, f: View.OnFocusChangeListener?) {
        m?.actionView?.let {
            val searchView = it as android.support.v7.widget.SearchView
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchView.onActionViewCollapsed()
            searchView.setOnQueryTextListener(q)
            searchView.setOnQueryTextFocusChangeListener(f)
        }
    }

    fun setupToggleFilterSettings(toggleTextView: TextView?, resources: Resources, group: Group?) {
        toggleTextView?.setOnClickListener {
            if (toggleTextView.text == resources.getString(R.string.hide_filter_settings)) {
                group?.visibility = View.GONE
                toggleTextView.text = resources.getString(R.string.show_filter_settings)
                toggleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_double_down, 0)
            } else {
                group?.visibility = View.VISIBLE
                toggleTextView.text = resources.getString(R.string.hide_filter_settings)
                toggleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_double_up, 0)
            }
        }
    }
}
