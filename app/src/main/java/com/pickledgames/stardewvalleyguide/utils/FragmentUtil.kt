package com.pickledgames.stardewvalleyguide.utils

import android.content.SharedPreferences
import android.content.res.Resources
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.Group
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.models.Farm
import com.wajahatkarim3.easyflipview.EasyFlipView

object FragmentUtil {

    fun setupSearchView(m: MenuItem?, q: SearchView.OnQueryTextListener, f: View.OnFocusChangeListener?) {
        m?.actionView?.let {
            val searchView = it as SearchView
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchView.onActionViewCollapsed()
            searchView.setOnQueryTextListener(q)
            searchView.setOnQueryTextFocusChangeListener(f)
        }
    }

    fun flipSelectedFarmText(easyFlipView: EasyFlipView?, front: TextView?, back: TextView?, resources: Resources, farm: Farm) {
        easyFlipView?.isFrontSide?.let { _ ->
            back?.text = String.format(resources.getString(R.string.farm_name_template, farm.name))
        }

        easyFlipView?.isBackSide?.let { _ ->
            front?.text = String.format(resources.getString(R.string.farm_name_template, farm.name))
        }

        easyFlipView?.flipTheView()
    }

    fun setupToggleFilterSettings(toggleTextView: TextView?, resources: Resources, group: Group?, sharedPreferences: SharedPreferences, key: String) {
        var hidden = sharedPreferences.getBoolean(key, false)
        toggleFilterSettings(hidden, toggleTextView, resources, group)

        toggleTextView?.setOnClickListener {
            hidden = !hidden
            toggleFilterSettings(hidden, toggleTextView, resources, group)
            sharedPreferences.edit().putBoolean(key, hidden).apply()
        }
    }

    private fun toggleFilterSettings(hidden: Boolean, toggleTextView: TextView?, resources: Resources, group: Group?) {
        if (hidden) {
            group?.visibility = View.GONE
            toggleTextView?.text = resources.getString(R.string.show_filter_settings)
            toggleTextView?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_double_down, 0)
        } else {
            group?.visibility = View.VISIBLE
            toggleTextView?.text = resources.getString(R.string.hide_filter_settings)
            toggleTextView?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_double_up, 0)
        }
    }

    private fun getFormattedTimeString(time: Int): String {
        return when (time) {
            in 6 until 12 -> "$time AM"
            12 -> "12 PM"
            in 13 until 24 -> "${time % 12} PM"
            24 -> "12 AM"
            else -> "${time % 24} AM"
        }
    }

    fun setTimeRangeText(startTime: Int, endTime: Int, textView: TextView?, resources: Resources) {
        val formattedStartTime = getFormattedTimeString(startTime)
        val formattedEndTime = getFormattedTimeString(endTime)
        textView?.text = String.format(resources.getString(R.string.time_range_template), formattedStartTime, formattedEndTime)
    }
}
