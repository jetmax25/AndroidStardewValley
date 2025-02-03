package com.pickledgames.stardewvalleyguide.repositories

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.models.Villager
import com.pickledgames.stardewvalleyguide.utils.RepositoryUtil
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Single
import java.lang.reflect.ParameterizedType

class VillagerRepository(
        private val context: Context,
        moshi: Moshi
) {

    private val villagers: MutableList<Villager> = mutableListOf()
    private val type: ParameterizedType = Types.newParameterizedType(Map::class.java, String::class.java, Villager::class.java)
    private val adapter: JsonAdapter<Map<String, Villager>> = moshi.adapter(type)

    fun getVillagers(): Single<List<Villager>> {
        if (villagers.isNotEmpty()) return Single.just(villagers)
        return getVillagersFromAssets()
                .doOnSuccess {
                    villagers.addAll(it)
                }
    }

    private fun getVillagersFromAssets(): Single<List<Villager>> {
        val inputStream = context.resources.openRawResource(R.raw.villagers)
        val json = RepositoryUtil.inputStreamToString(inputStream)
        val rawMap = adapter.fromJson(json) ?: emptyMap()

        val list = rawMap.map { (name, villager) ->
            villager.copy(name = name)
        }
        return Single.just(list)
    }
}
