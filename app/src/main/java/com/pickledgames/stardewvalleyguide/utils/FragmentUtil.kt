package com.pickledgames.stardewvalleyguide.utils

import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.view.View

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
}
