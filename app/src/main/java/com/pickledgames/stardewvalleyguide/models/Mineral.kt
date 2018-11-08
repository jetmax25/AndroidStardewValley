package com.pickledgames.stardewvalleyguide.models

class Mineral(
        name: String,
        val price: Int,
        description: String,
        val minMineLevel: Int,
        val maxMineLevel: Int,
        val locations: List<String>
) : MuseumItem(name, description, "Minerals")
