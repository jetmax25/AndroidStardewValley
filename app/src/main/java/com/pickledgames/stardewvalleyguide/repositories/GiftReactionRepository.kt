package com.pickledgames.stardewvalleyguide.repositories

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.models.Gift
import com.pickledgames.stardewvalleyguide.models.GiftReaction
import com.pickledgames.stardewvalleyguide.utils.RepositoryUtil
import io.reactivex.Single
import org.json.JSONObject

class GiftReactionRepository(
        private val context: Context
) {

    private val giftReactions: MutableList<GiftReaction> = mutableListOf()
    private val villagerNameGiftReactionsMap: MutableMap<String, List<GiftReaction>> = mutableMapOf()
    private val gifts: MutableList<Gift> = mutableListOf()

    fun getGifts(): Single<List<Gift>> {
        if (gifts.isNotEmpty()) return Single.just(gifts)
        return getGiftReactionsFromAssets()
                .map { giftReactions ->
                    val set: MutableSet<Gift> = mutableSetOf()
                    giftReactions.forEach { giftReaction ->
                        set.add(Gift(giftReaction.itemName, giftReaction.category))
                    }
                    return@map set.toList()
                }
                .doOnSuccess {
                    gifts.addAll(it)
                }
    }

    fun getGiftReactionsByVillagerName(villagerName: String): Single<List<GiftReaction>> {
        if (villagerNameGiftReactionsMap[villagerName] != null) return Single.just(villagerNameGiftReactionsMap[villagerName])
        return getGiftReactions()
                .map { g ->
                    val filteredGiftReactions = g.filter { it.villagerName.equals(villagerName, true) }
                    villagerNameGiftReactionsMap[villagerName] = filteredGiftReactions
                    return@map filteredGiftReactions
                }
    }

    private fun getGiftReactions(): Single<List<GiftReaction>> {
        if (giftReactions.isNotEmpty()) return Single.just(giftReactions)
        return getGiftReactionsFromAssets()
                .doOnSuccess {
                    giftReactions.addAll(it)
                }
    }

    private fun getGiftReactionsFromAssets(): Single<List<GiftReaction>> {
        val inputStream = context.resources.openRawResource(R.raw.gift_reactions)
        val json = RepositoryUtil.inputStreamToString(inputStream)

        return Single.create {
            val categories = JSONObject(json)
            val list: MutableList<GiftReaction> = mutableListOf()
            for (category in categories.keys()) {
                val items = categories.getJSONObject(category)
                for (item in items.keys()) {
                    val villagers = items.getJSONObject(item)
                    for (villager in villagers.keys()) {
                        val reaction = Reaction.valueOf(villagers.getString(villager).toLowerCase().capitalize())
                        list.add(GiftReaction(reaction, villager, item, category))
                    }
                }
            }

            it.onSuccess(list)
        }
    }
}
