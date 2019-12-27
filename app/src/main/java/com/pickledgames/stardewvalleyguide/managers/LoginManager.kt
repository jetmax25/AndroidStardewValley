package com.pickledgames.stardewvalleyguide.managers

import android.content.SharedPreferences
import org.threeten.bp.Instant

@Suppress("SetterBackingFieldAssignment")
class LoginManager(private val sharedPreferences: SharedPreferences) {

    var numberOfLogins: Int = sharedPreferences.getInt(NUMBER_OF_LOGINS, 0)
        set(value) {
            sharedPreferences.edit().putInt(NUMBER_OF_LOGINS, value).apply()
        }

    var firstLogin: Instant = Instant.parse(sharedPreferences.getString(FIRST_LOGIN, Instant.now().toString()))
        set(value) {
            sharedPreferences.edit().putString(FIRST_LOGIN, value.toString()).apply()
        }

    var lastLogin: Instant = Instant.parse(sharedPreferences.getString(LAST_LOGIN, Instant.now().toString()))
        set(value) {
            sharedPreferences.edit().putString(LAST_LOGIN, value.toString()).apply()
        }

    var reviewed: Boolean = sharedPreferences.getBoolean(REVIEWED, false)
        set(value) {
            sharedPreferences.edit().putBoolean(REVIEWED, value).apply()
        }

    var declinedReview: Boolean = sharedPreferences.getBoolean(DECLINED_REVIEW, false)
        set(value) {
            sharedPreferences.edit().putBoolean(DECLINED_REVIEW, value).apply()
        }

    val shouldShowReview: Boolean
        get() {
            return !reviewed && !declinedReview && numberOfLogins > 0 && numberOfLogins % 3 == 0
        }

    companion object {
        private const val NUMBER_OF_LOGINS = "NUMBER_OF_LOGINS"
        private const val FIRST_LOGIN = "FIRST_LOGIN"
        private const val LAST_LOGIN = "LAST_LOGIN"
        private const val REVIEWED = "REVIEWED"
        private const val DECLINED_REVIEW = "DECLINED_REVIEW"
    }
}
