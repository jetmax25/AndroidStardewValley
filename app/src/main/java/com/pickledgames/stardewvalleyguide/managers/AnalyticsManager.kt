package com.pickledgames.stardewvalleyguide.managers

import android.os.Bundle
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.pickledgames.stardewvalleyguide.StardewApp


class AnalyticsManager(
        stardewApp: StardewApp,
        private val loginManager: LoginManager,
        private val purchasesManager: PurchasesManager
) {

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(stardewApp)

    fun logEvent(name: String, params: Map<String, String> = emptyMap()) {
        logEventForFirebase(name, params)
        logEventForFabric(name, params)
    }

    private fun logEventForFirebase(name: String, params: Map<String, String>) {
        val bundle = Bundle().apply {
            putString(FIRST_LOGIN_KEY, loginManager.firstLogin.toString())
            putString(LAST_LOGIN_KEY, loginManager.lastLogin.toString())
            putString(IS_AD_FREE_KEY, purchasesManager.isPro.toString())
            putString(FirebaseAnalytics.Param.ITEM_NAME, name)
            params.forEach { (key, value) -> putString(key, value) }
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    private fun logEventForFabric(name: String, params: Map<String, String>) {
        val customEvent = CustomEvent(name).apply {
            putCustomAttribute(FIRST_LOGIN_KEY, loginManager.firstLogin.toString())
            putCustomAttribute(LAST_LOGIN_KEY, loginManager.lastLogin.toString())
            putCustomAttribute(IS_AD_FREE_KEY, purchasesManager.isPro.toString())
            params.forEach { (key, value) -> putCustomAttribute(key, value) }
        }

        Answers.getInstance().logCustom(customEvent)
    }

    companion object {
        const val FIRST_LOGIN_KEY = "first_login"
        const val LAST_LOGIN_KEY = "last_login"
        const val IS_AD_FREE_KEY = "is_ad_free"
    }
}
