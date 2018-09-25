package com.pickledgames.stardewvalleyguide.misc

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.pickledgames.stardewvalleyguide.StardewApp
import java.util.*

class AdManager(stardewApp: StardewApp) {

    private val builder: AdRequest.Builder = AdRequest.Builder()
    // TODO: Replace with real AdMob Ad IDs
    private val ads: HashMap<String, InterstitialAd> = hashMapOf(
            VILLAGER_FRAGMENT to InterstitialAd(stardewApp).apply { adUnitId = "ca-app-pub-3940256099942544/1033173712" },
            GIFT_FRAGMENT to InterstitialAd(stardewApp).apply { adUnitId = "ca-app-pub-3940256099942544/1033173712" },
            COMMUNITY_CENTER_ITEM_FRAGMENT to InterstitialAd(stardewApp).apply { adUnitId = "ca-app-pub-3940256099942544/1033173712" }
    )
    private val counts: HashMap<String, Int> = hashMapOf(
            VILLAGER_FRAGMENT to 0,
            GIFT_FRAGMENT to 0,
            COMMUNITY_CENTER_ITEM_FRAGMENT to 0
    )

    fun showAdFor(name: String) {
        if (counts[name] == null || counts[name]!! > LIMIT) return
        val random = (counts[name]!!..LIMIT).shuffled().last()
        if (random == LIMIT) {
            ads[name]?.apply {
                loadAd(builder.build())
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        counts[name] = LIMIT + 1
                        show()
                    }
                }
            }
        } else {
            counts[name]!!.inc()
        }
    }

    companion object {
        const val LIMIT = 3
        const val VILLAGER_FRAGMENT = "VILLAGER_FRAGMENT"
        const val GIFT_FRAGMENT = "GIFT_FRAGMENT"
        const val COMMUNITY_CENTER_ITEM_FRAGMENT = "COMMUNITY_CENTER_ITEM_FRAGMENT"
    }
}
