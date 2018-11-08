package com.pickledgames.stardewvalleyguide.models

class Artifact(
        name: String,
        description: String,
        val price: Int,
        val locations: List<String>
) : MuseumItem(name, description, "Artifacts")
