package com.rightapps.camprompter.audio

import android.content.Context
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log

object MicManager {
    const val TAG: String = "MicManager"

    enum class InputType(type: Int) {
        BuiltIn(15),
        Bluetooth(7),
        ExternalUsb(11)
    }

    fun getAudioManager(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun getAvailableMics(context: Context) = getAudioManager(context).microphones
    fun getMicListAsString(context: Context) =
        getAudioManager(context).microphones.joinToString("|") {
            "ID: ${it.id}, Descz: ${it.description} Type: ${it.type}"
        }

    fun getId(context: Context, type: InputType): Int =
        getAvailableMics(context).find { it.type == type.ordinal }?.id ?: -1

    fun switchMic(context: Context, type: InputType, mediaRecorder: MediaRecorder) {
        val micId = getId(context, type)
        getAudioManager(context).getDevices(AudioManager.GET_DEVICES_INPUTS)
            .first { it.id == micId }?.let { device ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Log.d(
                        TAG, "switchMic: Audio source set ${
                            if (mediaRecorder.setPreferredDevice(device)) "successfully" else "failed"
                        }"
                    )
                } else {
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                }
            }

    }
}