package com.pickledgames.stardewvalleyguide.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pickledgames.stardewvalleyguide.enums.FarmType
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Farm(
        val name: String,
        val farmType: FarmType,
        val communityCenterItems: MutableSet<String> = mutableSetOf(),
        val fishes: MutableSet<String> = mutableSetOf(),
        val museumItems: MutableSet<String> = mutableSetOf(),
        @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable {

    fun getCompletedItemsCount(communityCenterBundle: CommunityCenterBundle): Int {
        var completed = 0
        for (item: CommunityCenterItem in communityCenterBundle.items) {
            if (communityCenterItems.contains(item.uniqueId)) completed++
        }
        return completed
    }

    fun getCompletedItemsCount(museumItemCollection: MuseumItemCollection): Int {
        var completed = 0
        for (item: String in museumItems) {
            if (item.contains(museumItemCollection.name, true)) completed++
        }
        return completed
    }
}
