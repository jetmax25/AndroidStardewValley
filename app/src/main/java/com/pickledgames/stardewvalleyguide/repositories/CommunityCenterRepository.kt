package com.pickledgames.stardewvalleyguide.repositories

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.models.CommunityCenterBundle
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import com.pickledgames.stardewvalleyguide.models.CommunityCenterReward
import com.pickledgames.stardewvalleyguide.models.CommunityCenterRoom
import com.pickledgames.stardewvalleyguide.utils.RepositoryUtil
import io.reactivex.Single
import org.json.JSONObject

class CommunityCenterRepository(
        private val context: Context
) {

    private var rooms: MutableList<CommunityCenterRoom> = mutableListOf()

    fun getBundles(): Single<List<CommunityCenterBundle>> {
        return getRooms()
                .map {
                    val bundles = mutableListOf<CommunityCenterBundle>()
                    return@map it.fold(bundles) { acc, room ->
                        acc.addAll(room.bundles)
                        return@fold acc
                    }
                }
    }

    private fun getRooms(): Single<List<CommunityCenterRoom>> {
        if (rooms.isNotEmpty()) return Single.just(rooms)
        return getRoomsFromAssets()
                .doOnSuccess { rooms.addAll(it) }
    }

    private fun getRoomsFromAssets(): Single<List<CommunityCenterRoom>> {
        val inputStream = context.resources.openRawResource(R.raw.community_center)
        val json = RepositoryUtil.inputStreamToString(inputStream)
        val communityCenterRooms: MutableList<CommunityCenterRoom> = mutableListOf()

        return Single.create {
            val roomsJSONObject = JSONObject(json)
            for (room in roomsJSONObject.keys()) {
                val roomJSONObject = roomsJSONObject.getJSONObject(room)
                val roomReward = roomJSONObject.getString("Reward")

                val bundlesJSONObject = roomJSONObject.getJSONObject("Bundles")
                val communityCenterBundles = mutableListOf<CommunityCenterBundle>()
                for (bundle in bundlesJSONObject.keys()) {
                    val bundleJSONObject = bundlesJSONObject.getJSONObject(bundle)

                    val bundleRewardJSONObject = bundleJSONObject.getJSONObject("Reward")
                    val bundleRewardQuantity = bundleRewardJSONObject.getInt("Quantity")
                    val bundleRewardItem = bundleRewardJSONObject.getString("Item")

                    val communityCenterReward = CommunityCenterReward(bundleRewardQuantity, bundleRewardItem)

                    val itemsJSONObject = bundleJSONObject.getJSONObject("Items")
                    val communityCenterItems = mutableListOf<CommunityCenterItem>()
                    for (item in itemsJSONObject.keys()) {
                        val itemJSONObject = itemsJSONObject.getJSONObject(item)
                        val itemQuantity = itemJSONObject.getInt("Quantity")
                        val isTravelingMerchant = itemJSONObject.getBoolean("Traveling Merchant")

                        val guidesJSONArray = itemJSONObject.getJSONArray("Guide")
                        val guides = mutableListOf<String>()
                        for (i in 0 until guidesJSONArray.length()) {
                            val guide = guidesJSONArray.getString(i)
                            if (guide.isNotEmpty()) guides.add(guide)
                        }

                        val seasonsJSONObject = itemJSONObject.getJSONObject("Seasons")
                        val seasons = mutableSetOf<Season>()
                        for (season in seasonsJSONObject.keys()) {
                            val isAvailable = seasonsJSONObject.getBoolean(season)
                            if (isAvailable) seasons.add(Season.fromString(season))
                        }

                        val communityCenterItem = CommunityCenterItem(item, itemQuantity, isTravelingMerchant, guides, seasons, bundle)
                        communityCenterItems.add(communityCenterItem)
                    }

                    val needed = bundleJSONObject.getInt("Needed")

                    val communityCenterBundle = CommunityCenterBundle(bundle, communityCenterReward, communityCenterItems, needed)
                    communityCenterBundles.add(communityCenterBundle)
                }

                val communityCenterRoom = CommunityCenterRoom(room, roomReward, communityCenterBundles)
                communityCenterRooms.add(communityCenterRoom)
            }

            it.onSuccess(communityCenterRooms)
        }
    }
}
