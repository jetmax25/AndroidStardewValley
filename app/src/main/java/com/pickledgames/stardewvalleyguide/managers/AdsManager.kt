package com.pickledgames.stardewvalleyguide.managers

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.pickledgames.stardewvalleyguide.BuildConfig
import com.pickledgames.stardewvalleyguide.StardewApp
import java.util.*

class AdsManager(
        stardewApp: StardewApp,
        purchasesManager: PurchasesManager
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
            FISH_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/1372751378",
                    2
            ),
            MUSEUM_ITEM_FRAGMENT to StardewInterstitialAd(stardewApp, random,
                    "ca-app-pub-5594325776314197/5975706715",
                    3
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
        purchasesManager.isProSubject.subscribe { isPro = it }
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

        private var forceShow: Boolean = false
        private var shown: Boolean = false
        private val interstitialAd: InterstitialAd = InterstitialAd(context).apply {
            adUnitId = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else adId
            loadAd(AdRequest.Builder().build())
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    super.onAdFailedToLoad(errorCode)
                    forceShow = true
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    shown = true
                    forceShow = false
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    loadAd(AdRequest.Builder().build())
                }
            }
        }

        fun showAd() {
            // Don't show ad twice in a row
            if (shown) {
                shown = false
                return
            }
            // bound is exclusive
            val randomInt = random.nextInt(limit)
            if (randomInt == 0 || forceShow) {
                interstitialAd.show()
            }
        }
    }

    companion object {
        const val VILLAGER_FRAGMENT = "VILLAGER_FRAGMENT"
        const val GIFT_FRAGMENT = "GIFT_FRAGMENT"
        const val COMMUNITY_CENTER_ITEM_FRAGMENT = "COMMUNITY_CENTER_ITEM_FRAGMENT"
        const val FISH_FRAGMENT = "FISH_FRAGMENT"
        const val MUSEUM_ITEM_FRAGMENT = "MUSEUM_ITEM_FRAGMENT"
        const val CROP_FRAGMENT = "CROP_FRAGMENT"
        const val EVENT_FRAGMENT = "EVENT_FRAGMENT"
        const val RECIPE_FRAGMENT = "RECIPE_FRAGMENT"
        const val SHOP_FRAGMENT = "SHOP_FRAGMENT"
    }
}
