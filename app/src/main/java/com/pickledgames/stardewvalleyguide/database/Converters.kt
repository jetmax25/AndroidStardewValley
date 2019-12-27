package com.pickledgames.stardewvalleyguide.database

import androidx.room.TypeConverter
import com.pickledgames.stardewvalleyguide.enums.FarmType

class Converters {

    @TypeConverter
    fun farmTypeToString(farmType: FarmType): String {
        return farmType.type
    }

    @TypeConverter
    fun stringToFarmType(value: String): FarmType {
        return when (value) {
            "Standard" -> FarmType.Standard
            "Riverland" -> FarmType.Riverland
            "Forest" -> FarmType.Forest
            "Hilltop" -> FarmType.Hilltop
            "Wilderness" -> FarmType.Wilderness
            else -> FarmType.Standard
        }
    }

    @TypeConverter
    fun mutableSetToString(mutableSet: MutableSet<String>): String {
        return mutableSet.asSequence().toList().joinToString(",")
    }

    @TypeConverter
    fun stringToMutableSet(value: String): MutableSet<String> {
        return value.split(",").toMutableSet()
    }
}
