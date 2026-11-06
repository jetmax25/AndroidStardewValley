package com.pickledgames.stardewvalleyguide.models

import kotlinx.parcelize.Parcelize

@Parcelize
class Artifact(
        override val name: String,
        override val description: String,
        val price: Int,
        val locations: List<String>
) : MuseumItem(name, description, "Artifacts")
