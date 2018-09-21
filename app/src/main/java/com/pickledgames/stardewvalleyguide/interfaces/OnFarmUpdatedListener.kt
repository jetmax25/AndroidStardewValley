package com.pickledgames.stardewvalleyguide.interfaces

import com.pickledgames.stardewvalleyguide.models.Farm

interface OnFarmUpdatedListener {
    fun onFarmUpdated(farm: Farm?, position: Int)
}
