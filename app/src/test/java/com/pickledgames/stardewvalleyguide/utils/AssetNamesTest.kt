package com.pickledgames.stardewvalleyguide.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class AssetNamesTest {

    @Test
    fun itemNamesResolveToExistingSpriteFiles() {
        assertEquals("item_orpiment", AssetNames.itemDrawable("Oripment"))
        assertEquals("item_dwarvish_helm", AssetNames.itemDrawable("Dwavish Helm"))
        assertEquals("item_triple_shot_espresso", AssetNames.itemDrawable("Triple Shot Expresso"))
        assertEquals("item_cranberry_seeds", AssetNames.itemDrawable("Cranberries Seeds"))
        assertEquals("item_wood", AssetNames.itemDrawable("More Wood"))
        assertEquals("item_wine", AssetNames.itemDrawable("Silver or better quality Wine"))
        assertEquals("item_ancient_fruit", AssetNames.itemDrawable("Gold quality Ancient Fruit"))
        assertEquals("item_void_salmon", AssetNames.itemDrawable("Void Salmon (Gold or iridium Quality)"))
        assertEquals("item_gold", AssetNames.itemDrawable("2,500g"))
        assertEquals("item_gold", AssetNames.itemDrawable("10,000g"))
    }

    @Test
    fun bundleNamesKeepPossessiveSprites() {
        assertEquals("community_center_bundle_blacksmith_s", AssetNames.bundleDrawable("Blacksmith's Bundle"))
        assertEquals("community_center_bundle_blacksmith_s", AssetNames.bundleDrawable("Remix: Blacksmiths Bundle"))
        assertEquals("community_center_bundle_spring_foraging", AssetNames.bundleDrawable("Remix: Spring Foraging Bundle"))
        assertEquals("item_large_egg_white", AssetNames.itemDrawable("Large Egg"))
        assertEquals("community_center_bundle_2_500", AssetNames.bundleDrawable("2,500 Bundle"))
    }

    @Test
    fun farmTypesSlugSpaces() {
        assertEquals("farm_type_four_corners", AssetNames.farmTypeDrawable("Four Corners"))
        assertEquals("farm_type_standard", AssetNames.farmTypeDrawable("Standard"))
    }
}
