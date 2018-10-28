package com.pickledgames.stardewvalleyguide.utils

import android.content.SharedPreferences
import android.content.res.Resources
import android.support.constraint.Group
import android.view.View
import android.widget.TextView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.models.Farm
import com.wajahatkarim3.easyflipview.EasyFlipView

object ChecklistFragmentUtil {

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
}
