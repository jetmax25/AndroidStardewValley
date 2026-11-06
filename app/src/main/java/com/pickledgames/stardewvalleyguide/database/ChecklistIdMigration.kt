package com.pickledgames.stardewvalleyguide.database

/**
 * Rewrites saved checklist ids when a reference-data name is corrected.
 * Farm progress is stored as the old display name, so a rename would otherwise uncheck it.
 */
object ChecklistIdMigration {

    private val replacements = listOf(
            "Blacksmiths Bundle" to "Blacksmith's Bundle",
            "Geologists Bundle" to "Geologist's Bundle",
            "Adventurers Bundle" to "Adventurer's Bundle",
            "Chefs Bundle" to "Chef's Bundle",
            "Enchanters Bundle" to "Enchanter's Bundle",
            "Rabbits Foot" to "Rabbit's Foot",
            "Wine Silver Quality" to "Silver or better quality Wine",
            "Ancient Fruit (Gold Quality)" to "Gold quality Ancient Fruit",
            "Void Salmon (Gold or iridium Quality)" to "Gold or Iridium quality Void Salmon",
            "On Foraging " to "On Foraging",
            "The Fisherman, Act I:" to "The Fisherman, Act I",
            "The Fisherman, Act II:" to "The Fisherman, Act II",
            " ???" to "???",
            "Goblins\n        by M. Jasper" to "Goblins by M. Jasper"
    )

    fun migrateId(id: String): String {
        var updated = id
        for ((old, new) in replacements) {
            updated = updated.replace(old, new)
        }
        return updated
    }

    fun migrateSet(stored: String): String {
        if (stored.isEmpty()) return stored
        return stored.split(",").joinToString(",") { migrateId(it) }
    }
}
