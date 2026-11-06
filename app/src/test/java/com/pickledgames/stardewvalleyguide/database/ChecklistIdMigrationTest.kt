package com.pickledgames.stardewvalleyguide.database

import org.junit.Assert.assertEquals
import org.junit.Test

class ChecklistIdMigrationTest {

    @Test
    fun renamedBundleAndItemIdsStayChecked() {
        val stored = "Copper BarBlacksmiths Bundle,Rabbits FootEnchanters Bundle,Wine Silver QualityThe Missing Bundle"
        val updated = ChecklistIdMigration.migrateSet(stored)
        assertEquals(
                "Copper BarBlacksmith's Bundle,Rabbit's FootEnchanter's Bundle,Silver or better quality WineThe Missing Bundle",
                updated
        )
    }

    @Test
    fun renamedLostBookIdsStayChecked() {
        assertEquals(
                "On ForagingLost Books",
                ChecklistIdMigration.migrateId("On Foraging Lost Books")
        )
        assertEquals(
                "???Lost Books",
                ChecklistIdMigration.migrateId(" ???Lost Books")
        )
    }
}
