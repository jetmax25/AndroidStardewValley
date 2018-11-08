package com.pickledgames.stardewvalleyguide.interfaces

import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import com.pickledgames.stardewvalleyguide.models.Fish
import com.pickledgames.stardewvalleyguide.models.MuseumItem

interface OnItemCheckedListener {
    fun onItemChecked(communityCenterItem: CommunityCenterItem, isChecked: Boolean) {}
    fun onItemChecked(fish: Fish, isChecked: Boolean) {}
    fun onItemChecked(museumItem: MuseumItem, isChecked: Boolean) {}
}
