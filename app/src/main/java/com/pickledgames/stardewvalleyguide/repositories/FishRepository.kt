package com.pickledgames.stardewvalleyguide.repositories

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.enums.FishingLocationType
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.models.Availability
import com.pickledgames.stardewvalleyguide.models.Fish
import com.pickledgames.stardewvalleyguide.models.Stats
import com.pickledgames.stardewvalleyguide.utils.RepositoryUtil
import io.reactivex.Single
import org.json.JSONObject

class FishRepository(
        private val context: Context
) {

    private val fishes: MutableList<Fish> = mutableListOf()

    fun getFishes(): Single<List<Fish>> {
        if (fishes.isNotEmpty()) return Single.just(fishes)
        return getFishesFromAssets()
                .doOnSuccess { fishes.addAll(it) }
    }

    private fun getFishesFromAssets(): Single<List<Fish>> {
        val inputStream = context.resources.openRawResource(R.raw.fishes)
        val json = RepositoryUtil.inputStreamToString(inputStream)
        val fishes: MutableList<Fish> = mutableListOf()

        return Single.create {
            val fishesJSONObject = JSONObject(json)
            for (name in fishesJSONObject.keys()) {
                val fishJSONObject = fishesJSONObject.getJSONObject(name)
                val description = fishJSONObject.getString("Description")

                lateinit var commonStats: Stats
                lateinit var silverStats: Stats
                lateinit var goldStats: Stats
                val variousStatsJSONObject = fishJSONObject.getJSONObject("Stats")
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

                val availabilityJSONObject = fishJSONObject.getJSONObject("Availability")
                val weather = availabilityJSONObject.getString("Weather")
                val locations = mutableMapOf<String, FishingLocationType>()
                val locationsJSONArray = availabilityJSONObject.getJSONArray("Locations")
                for (i in 0 until locationsJSONArray.length()) {
                    val location = locationsJSONArray.getString(i)
                    locations[location] = (FishingLocationType.fromString(location))
                }
                val seasonsJSONObject = availabilityJSONObject.getJSONObject("Seasons")
                val seasons = mutableSetOf<Season>()
                for (season in seasonsJSONObject.keys()) {
                    val isAvailable = seasonsJSONObject.getBoolean(season)
                    if (isAvailable) seasons.add(Season.fromString(season))
                }
                val timeJSONObject = availabilityJSONObject.getJSONObject("Time")
                val startTime = timeJSONObject.getInt("Start")
                val endTime = timeJSONObject.getInt("End")
                val availability = Availability(weather, locations, seasons, startTime, endTime)

                val behavior = fishJSONObject.getString("Behavior")
                val xp = fishJSONObject.getInt("XP")
                val difficulty = fishJSONObject.getInt("Difficulty")
                val isLegendary = fishJSONObject.getBoolean("Legendary")
                val fishingLevel = fishJSONObject.getInt("Fishing Level")

                val fish = Fish(
                        name, description, commonStats, silverStats, goldStats, availability,
                        behavior, xp, difficulty, isLegendary, fishingLevel
                )

                fishes.add(fish)
            }

            it.onSuccess(fishes)
        }
    }
}
