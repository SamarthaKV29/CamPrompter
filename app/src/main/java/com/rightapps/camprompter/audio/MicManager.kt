package com.rightapps.camprompter.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

object MicManager {
    const val TAG: String = "MicManager"
    private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)


    enum class InputType(type: Int) {
        BuiltIn(15),
        Bluetooth(7),
        ExternalUsb(11)
    }

    fun checkAudioPermissions(activity: AppCompatActivity): Boolean {
        val granted = ActivityCompat.checkSelfPermission(
            activity,
            permissions[0]
        ) == PERMISSION_GRANTED
        Log.d(TAG, "checkAudioPermissions: GRANTED: $granted")
        if (!granted) {

            ActivityCompat.requestPermissions(
                activity,
                permissions,
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
        return granted
    }

    fun handleOnRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ): Boolean {
        return if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PERMISSION_GRANTED
        } else {
            false
        }
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

    suspend fun switchMic(context: Context, type: InputType, mediaRecorder: MediaRecorder) {
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