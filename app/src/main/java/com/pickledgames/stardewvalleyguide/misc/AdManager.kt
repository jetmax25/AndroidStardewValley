package com.pickledgames.stardewvalleyguide.misc

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.pickledgames.stardewvalleyguide.BuildConfig
import com.pickledgames.stardewvalleyguide.StardewApp
import java.util.*

class AdManager(
        stardewApp: StardewApp,
        purchaseManager: PurchaseManager
) {

    private val random: Random = Random()
    private val ads: HashMap<String, StardewInterstitialAd> = hashMapOf(
            VILLAGER_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/6267368292",
                    3
            ),
            GIFT_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/8401850693",
                    3
            ),
            COMMUNITY_CENTER_ITEM_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/4573629781",
                    3
            ),
            FISHING_ITEM_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/1372751378",
                    2
            ),
            CROP_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/7443050293",
                    2
            ),
            EVENT_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/3041056264",
                    3
            ),
            RECIPE_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/9548137754",
                    3
            ),
            SHOP_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/2561709969",
                    2
            )
    )
    private var isPro: Boolean = false

    init {
        @Suppress("CheckResult")
        // TODO: May cause memory leak, should investigate further
        purchaseManager.isProSubject.subscribe {
            isPro = it
        }
    }

    fun showAdFor(name: String) {
        if (!isPro) {
            ads[name]?.showAd()
        }
    }

    class StardewInterstitialAd(
            context: Context,
            private val random: Random,
            private val adId: String,
            private val limit: Int
    ) {

        private val interstitialAd: InterstitialAd = InterstitialAd(context).apply {
            adUnitId = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else adId
        }
        private var forceShow: Boolean = false
        private var shown: Boolean = false

        fun showAd() {
            if (shown) return
            // bound is exclusive
            val randomInt = random.nextInt(limit)
            if (randomInt == 0 || forceShow) {
                interstitialAd.apply {
                    loadAd(AdRequest.Builder().build())
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            show()
                        }

                        override fun onAdFailedToLoad(errorCode: Int) {
                            super.onAdFailedToLoad(errorCode)
                            forceShow = true
                        }

                        override fun onAdOpened() {
                            super.onAdOpened()
                            shown = true
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val VILLAGER_FRAGMENT = "VILLAGER_FRAGMENT"
        const val GIFT_FRAGMENT = "GIFT_FRAGMENT"
        const val COMMUNITY_CENTER_ITEM_FRAGMENT = "COMMUNITY_CENTER_ITEM_FRAGMENT"
        const val FISHING_ITEM_FRAGMENT = "FISHING_ITEM_FRAGMENT"
        const val CROP_FRAGMENT = "CROP_FRAGMENT"
        const val EVENT_FRAGMENT = "EVENT_FRAGMENT"
        const val RECIPE_FRAGMENT = "RECIPE_FRAGMENT"
        const val SHOP_FRAGMENT = "SHOP_FRAGMENT"
    }
}
