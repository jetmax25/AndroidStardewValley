package com.pickledgames.stardewvalleyguide.interfaces

import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem

interface OnItemCheckedListener {
    fun onItemChecked(communityCenterItem: CommunityCenterItem, isChecked: Boolean)
}
