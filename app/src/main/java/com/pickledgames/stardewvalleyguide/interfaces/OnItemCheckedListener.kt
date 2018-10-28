package com.pickledgames.stardewvalleyguide.interfaces

import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import com.pickledgames.stardewvalleyguide.models.Fish

interface OnItemCheckedListener {
    fun onItemChecked(communityCenterItem: CommunityCenterItem, isChecked: Boolean) {}
    fun onItemChecked(fish: Fish, isChecked: Boolean) {}
}
