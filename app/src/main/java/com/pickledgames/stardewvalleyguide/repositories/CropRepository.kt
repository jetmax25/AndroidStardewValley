package com.pickledgames.stardewvalleyguide.repositories

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.models.Crop
import com.pickledgames.stardewvalleyguide.models.Stats
import com.pickledgames.stardewvalleyguide.utils.RepositoryUtil
import io.reactivex.Single
import org.json.JSONObject

class CropRepository(
        private val context: Context
) {

    private val crops: MutableList<Crop> = mutableListOf()

    fun getCrops(): Single<List<Crop>> {
        if (crops.isNotEmpty()) return Single.just(crops)
        return getCropsFromAssets()
                .doOnSuccess { crops.addAll(it) }
    }

    private fun getCropsFromAssets(): Single<List<Crop>> {
        val inputStream = context.resources.openRawResource(R.raw.crops)
        val json = RepositoryUtil.inputStreamToString(inputStream)
        val crops: MutableList<Crop> = mutableListOf()

        return Single.create {
            val cropsJSONObject = JSONObject(json)
            for (name in cropsJSONObject.keys()) {
                val cropJSONObject = cropsJSONObject.getJSONObject(name)
                val seeds = if (cropJSONObject.has("Seeds")) cropJSONObject.getString("Seeds") else null
                val harvestTime = cropJSONObject.getString("Harvest Time").toInt()
                val goldPerDay = cropJSONObject.getDouble("Gold Per Day")
                val seedPrice = cropJSONObject.getInt("Seed Price")
                val regrowthDays = cropJSONObject.getInt("Regrowth Days")

                val stages = mutableListOf(0)
                val stagesJSONArray = cropJSONObject.getJSONArray("Stages")

                var total = 0
                for (i in 0 until stagesJSONArray.length()) {
                    val stage = stagesJSONArray.getInt(i)
                    if (stage != 0) {
                        total += stage
                        stages.add(total)
                    }
                }

                val seasonsJSONObject = cropJSONObject.getJSONObject("Seasons")
                val seasons = mutableSetOf<Season>()
                for (season in seasonsJSONObject.keys()) {
                    val isAvailable = seasonsJSONObject.getBoolean(season)
                    if (isAvailable) seasons.add(Season.fromString(season))
                }

                lateinit var commonStats: Stats
                lateinit var silverStats: Stats
                lateinit var goldStats: Stats
                val variousStatsJSONObject = cropJSONObject.getJSONObject("Stats")
                for (stats in variousStatsJSONObject.keys()) {
                    val statsJSONObject = variousStatsJSONObject.getJSONObject(stats)
                    val health = statsJSONObject.getInt("Health")
                    val energy = statsJSONObject.getInt("Energy")
                    val price = statsJSONObject.getInt("Price")
                    when (stats) {
                        "Common" -> commonStats = Stats(health, energy, price)
                        "Silver" -> silverStats = Stats(health, energy, price)
                        "Gold" -> goldStats = Stats(health, energy, price)
                    }
                }

                val crop = Crop(
                        name, seeds, harvestTime, goldPerDay, seedPrice, regrowthDays,
                        stages, seasons, commonStats, silverStats, goldStats
                )

                crops.add(crop)
            }

            it.onSuccess(crops)
        }
    }
}
