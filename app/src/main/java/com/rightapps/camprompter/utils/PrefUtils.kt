package com.rightapps.camprompter.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object PrefUtils {
    private const val KEY_USB_MIC = "prefer_usb_mic"
    private const val KEY_4K = "prefer_qhd"

    private fun getDefaultPrefs(context: Context): SharedPreferences? =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun is4KPreferred(context: Context): Boolean =
        getDefaultPrefs(context)?.getBoolean(KEY_4K, false) ?: false

    fun isUsbMicPreferred(context: Context): Boolean =
        getDefaultPrefs(context)?.getBoolean(KEY_USB_MIC, false) ?: false
}