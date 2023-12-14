package com.rightapps.camprompter.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.rightapps.camprompter.R

object PrefUtils {
    private const val KEY_USB_MIC = "prefer_usb_mic"
    private const val KEY_4K = "prefer_qhd"
    private const val KEY_SELECTED_MIC = "selected_mic"
    private const val KEY_SELECTED_RES = "selected_res"

    interface SettingOption<E> where E : Enum<E> {
        val text: String
        val resource: Int
        override fun toString(): String
    }

    enum class VideoResolution(
        override val text: String,
        override val resource: Int
    ) : SettingOption<VideoResolution> {
        RESOLUTION_HD("HD", R.drawable.baseline_hd_64),
        RESOLUTION_4K("4K", R.drawable.baseline_4k_64);

        companion object {
            val key = KEY_SELECTED_RES
        }

        override fun toString(): String {
            return "$ordinal: $text"
        }
    }

    enum class InputType(override val text: String, override val resource: Int) :
        SettingOption<InputType> {
        BuiltIn("Built-In", R.drawable.mic_icon),
        Bluetooth("Bluetooth", R.drawable.baseline_bluetooth_64),
        ExternalUsb("USB Headset", R.drawable.baseline_mic_external_on_64);

        companion object {
            val key = KEY_SELECTED_MIC
        }

        override fun toString(): String {
            return "$ordinal: $text"
        }
    }

    private fun getDefaultPrefs(context: Context): SharedPreferences? =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun is4KPreferred(context: Context): Boolean =
        getDefaultPrefs(context)?.getBoolean(KEY_4K, false) ?: false

    fun isUsbMicPreferred(context: Context): Boolean =
        getDefaultPrefs(context)?.getBoolean(KEY_USB_MIC, false) ?: false

    fun getSelected(context: Context, key: String, default: Int): Int =
        getDefaultPrefs(context)?.getInt(key, default) ?: default

    fun setSelected(context: Context, key: String, value: Int) =
        getDefaultPrefs(context)?.edit()?.putInt(key, value)?.apply()

}