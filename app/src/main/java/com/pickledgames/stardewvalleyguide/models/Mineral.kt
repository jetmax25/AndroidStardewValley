package com.pickledgames.stardewvalleyguide.models

import kotlinx.android.parcel.Parcelize

@Parcelize
class Mineral(
        override val name: String,
        val price: Int,
        override val description: String,
        val minMineLevel: Int,
        val maxMineLevel: Int,
        val locations: List<String>
) : MuseumItem(name, description, "Minerals")
