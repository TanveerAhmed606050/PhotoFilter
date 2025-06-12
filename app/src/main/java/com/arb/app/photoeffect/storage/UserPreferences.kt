package com.pakscrap.android.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        const val SHARED_PREF_NAME = "user_credential_Prefer"
        const val USER_DATA = "user_login_data"
        const val LOCATION_LIST = "location_list"
        const val IS_FIRST_LAUNCH = "isFirstLaunch"
        const val LATEST_SEARCH = "latest_search"
        const val READ_TERMS = "read_terms"
        const val NOTIFICATION_SWITCH = "isSelected"
    }

    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
        set(value) = sharedPreferences.edit { putBoolean(IS_FIRST_LAUNCH, value) }
    var isTermsAccept: Boolean
        get() = sharedPreferences.getBoolean(READ_TERMS, false)
        set(value) = sharedPreferences.edit { putBoolean(READ_TERMS, value) }
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    var notificationSwitchSelected: Boolean
        get() = sharedPreferences.getBoolean(NOTIFICATION_SWITCH, false)
        set(value) = sharedPreferences.edit { putBoolean(NOTIFICATION_SWITCH, value) }
    private val gson = Gson()


    fun clearStorage() = sharedPreferences.edit().clear().apply()
}