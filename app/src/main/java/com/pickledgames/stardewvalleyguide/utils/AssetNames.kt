package com.pickledgames.stardewvalleyguide.utils

import java.util.Locale

/**
 * Turns content names into drawable resource names.
 * Resource lookup still goes through [ImageUtil], which uses the bundled placeholder
 * when a file is not in the app.
 */
object AssetNames {

    fun slug(value: String): String {
        return value.lowercase(Locale.US)
                .replace("\\W+".toRegex(), "_")
                .trim('_')
    }

    fun itemDrawable(name: String): String {
        var cleaned = name.trim()
        cleaned = QUALITY_PARENS.replace(cleaned, "")
        cleaned = QUALITY_PREFIX.replace(cleaned, "")
        cleaned = QUALITY_SUFFIX.replace(cleaned, "")
        cleaned = cleaned.trim()
        val alias = ITEM_ALIASES[cleaned.lowercase(Locale.US)]
        if (alias != null) cleaned = alias
        val slugged = slug(cleaned)
        if (slugged.isEmpty()) return "default_image"
        if (MONEY.matches(slugged)) return "item_gold"
        return "item_$slugged"
    }

    fun bundleDrawable(name: String): String {
        var cleaned = name.replace(Regex("^(Remix|Joja):\\s*"), "")
        cleaned = cleaned.lowercase(Locale.US).replace("bundle", "").trim()
        cleaned = BUNDLE_POSSESSIVES[cleaned] ?: cleaned
        return "community_center_bundle_${slug(cleaned)}"
    }

    fun cropPhaseDrawable(cropName: String, oneBasedPhase: Int): String {
        return "item_${slug(cropName)}_phase_$oneBasedPhase"
    }

    fun cropRegrowthDrawable(cropName: String): String {
        return "item_${slug(cropName)}_regrowth"
    }

    fun cropSeedDrawables(cropName: String, seeds: String?): List<String> {
        if (!seeds.isNullOrBlank()) return listOf(itemDrawable(seeds))
        return listOf("item_${slug(cropName)}_seeds", itemDrawable(cropName))
    }

    fun farmTypeDrawable(type: String): String {
        return "farm_type_${slug(type)}"
    }

    private val ITEM_ALIASES = mapOf(
            "oripment" to "orpiment",
            "dwavish helm" to "dwarvish helm",
            "triple shot expresso" to "triple shot espresso",
            "pina colada" to "pina colada",
            "piña colada" to "pina colada",
            "more wood" to "wood",
            "cranberries seeds" to "cranberry seeds",
            "rabbits foot" to "rabbit's foot",
            "farmers lunch" to "farmer's lunch",
            "large egg" to "large egg white",
            "large brown egg" to "large egg brown"
    )

    private val QUALITY_PARENS = Regex("(?i)\\s*\\([^)]*quality[^)]*\\)")
    private val QUALITY_PREFIX = Regex("(?i)^(silver or better quality|gold or iridium quality|gold quality|silver quality)\\s+")
    private val QUALITY_SUFFIX = Regex("(?i)\\s+(silver|gold|iridium) quality$")
    private val MONEY = Regex("\\d+_\\d+g|\\d+g")

    private val BUNDLE_POSSESSIVES = mapOf(
            "blacksmiths" to "blacksmith's",
            "geologists" to "geologist's",
            "adventurers" to "adventurer's",
            "chefs" to "chef's",
            "enchanters" to "enchanter's",
            "g2500" to "2,500",
            "g5000" to "5,000",
            "g10000" to "10,000",
            "g25000" to "25,000"
    )
}
