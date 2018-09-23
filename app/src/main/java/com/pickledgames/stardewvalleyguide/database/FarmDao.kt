package com.pickledgames.stardewvalleyguide.database

import android.arch.persistence.room.*
import com.pickledgames.stardewvalleyguide.models.Farm
import io.reactivex.Single

@Dao
interface FarmDao {

    @Query("SELECT * FROM Farm")
    fun getAllFarms(): Single<List<Farm>>

    @Insert()
    fun insertFarm(farm: Farm): Long

    @Delete()
    fun deleteFarm(farm: Farm)

    @Update()
    fun updateFarm(farm: Farm): Int
}
