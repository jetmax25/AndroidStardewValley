package com.pickledgames.stardewvalleyguide.ads

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Named

class AdsService @Inject constructor(
    @Named("ads_settings")
    private val sharedPreferences: SharedPreferences,
) {

    fun isAgeVerified(): Boolean =
        sharedPreferences.getBoolean(ADS_AGE_VERIFIED_KEY, false)

    fun verifyAge() {
        sharedPreferences.edit {
            putBoolean(ADS_ENABLED_KEY, true)
            putBoolean(ADS_AGE_VERIFIED_KEY, true)
        }
    }

    fun areAdsEnabled() = sharedPreferences.let {
        it.getBoolean(ADS_ENABLED_KEY, false) && it.getBoolean(ADS_AGE_VERIFIED_KEY, false)
    }

    companion object {
        const val PREFS_NAME = "ads_settings"
        private const val ADS_AGE_VERIFIED_KEY = "ads_permission_requested"
        private const val ADS_ENABLED_KEY = "ads_enabled"
    }
}
