package com.pickledgames.stardewvalleyguide.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.enums.FarmType
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Farm(
        val name: String,
        val farmType: FarmType,
        val communityCenterItems: MutableSet<String> = mutableSetOf(),
        @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable
