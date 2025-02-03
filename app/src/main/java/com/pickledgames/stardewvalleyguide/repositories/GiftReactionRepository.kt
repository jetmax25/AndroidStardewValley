package com.pickledgames.stardewvalleyguide.repositories

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.models.Gift
import com.pickledgames.stardewvalleyguide.models.GiftReaction
import com.pickledgames.stardewvalleyguide.utils.RepositoryUtil
import io.reactivex.Single
import org.json.JSONArray
import java.util.*
import kotlin.math.min

class GiftReactionRepository(
        private val context: Context
) {

    private val giftReactions: MutableList<GiftReaction> = mutableListOf()
    private val villagerNameGiftReactionsMap: MutableMap<String, List<GiftReaction>> = mutableMapOf()
    private val gifts: MutableList<Gift> = mutableListOf()
    private val giftNameGiftReactionsMap: MutableMap<String, List<GiftReaction>> = mutableMapOf()

    fun getGiftReactionsByItemName(itemName: String): Single<List<GiftReaction>> {
        if (giftNameGiftReactionsMap[itemName] != null) return Single.just(giftNameGiftReactionsMap[itemName])
        return getGiftReactions()
                .map { g ->
                    val filteredGiftReactions = g.filter { it.itemName.equals(itemName, true) }
                    giftNameGiftReactionsMap[itemName] = filteredGiftReactions
                    return@map filteredGiftReactions
                }
    }

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
        val list: MutableList<GiftReaction> = mutableListOf()

        return Single.create {
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val category = jsonObject.getString("category")
                val itemName = jsonObject.getString("giftName")
                val reactionsObject = jsonObject.getJSONObject("reactions")

                reactionsObject.keys().forEach { villagerName ->
                    val villagerReaction = reactionsObject.getString(villagerName).toLowerCase(Locale.US).capitalize()
                    // Weird bug occurs where Dıslıke can't be matched (note the missing dots on i)
                    // Might be a translation issue, but don't know how to replicate so handle it here
                    val reaction = try {
                        Reaction.valueOf(villagerReaction)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        findClosestReaction(villagerReaction)
                    }

                    list.add(GiftReaction(reaction, villagerName, itemName, category))
                }
            }

            it.onSuccess(list)
        }
    }

    private fun findClosestReaction(s: String): Reaction {
        var min = Int.MAX_VALUE
        val map = mutableMapOf<Int, Reaction>()
        for (reaction: Reaction in Reaction.values()) {
            val score = levenshtein(s, reaction.type)
            map[score] = reaction
            if (score < min) min = score
        }

        return map[min] ?: Reaction.Neutral
    }

    // https://gist.github.com/ademar111190/34d3de41308389a0d0d8
    private fun levenshtein(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = Array(lhsLength) { it }
        var newCost = Array(lhsLength) { 0 }

        for (i in 1 until rhsLength) {
            newCost[0] = i

            for (j in 1 until lhsLength) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1

                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }

            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength - 1]
    }
}
