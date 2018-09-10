package com.pickledgames.stardewvalleyguide.repository

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.model.Villager
import com.pickledgames.stardewvalleyguide.util.RepositoryUtil
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Single
import java.lang.reflect.ParameterizedType

class VillagerRepository(
        private val context: Context,
        moshi: Moshi
) {

    var villagers: MutableList<Villager> = mutableListOf()
    var type: ParameterizedType = Types.newParameterizedType(List::class.java, Villager::class.java)
    var adapter: JsonAdapter<List<Villager>> = moshi.adapter<List<Villager>>(type)

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
        val list = adapter.fromJson(json)
        return Single.just(list)
    }
}
