package com.rightapps.camprompter.utils.audio

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaRecorder
import android.media.MediaRecorder.OnInfoListener
import android.os.Build
import android.util.Log
import com.rightapps.camprompter.utils.FileUtils
import com.rightapps.camprompter.utils.PrefUtils.InputType

object MicManager {
    const val TAG: String = "MicManager"

    fun getAvailable(context: Context): Set<InputType> =
        getAudioManager(context).let { audioManager ->
            mutableSetOf(InputType.BuiltIn).apply {
                audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS).map { it.type }
                    .forEach { device ->
                        when (device) {
                            AudioDeviceInfo.TYPE_BLE_HEADSET,
                            AudioDeviceInfo.TYPE_BLUETOOTH_SCO ->
                                add(InputType.Bluetooth)

                            AudioDeviceInfo.TYPE_USB_DEVICE,
                            AudioDeviceInfo.TYPE_USB_ACCESSORY,
                            AudioDeviceInfo.TYPE_USB_HEADSET ->
                                add(InputType.ExternalUsb)

                        }
                    }
            }.toSet()
        }

    private fun getAudioManager(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun getRecorder(context: Context) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else MediaRecorder()

    fun MediaRecorder.prepareSafely(context: Context, onInfoListener: OnInfoListener? = null) =
        try {
            setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)

            setAudioSamplingRate(44100)
            setOutputFile(
                FileUtils.getOutputFile(
                    context,
                    FileUtils.FileType.AUDIO_FILE
                )
            );
            onInfoListener?.let { setOnInfoListener(it) }
            setMaxDuration(60000); // Set maximum duration to 1 minute
            prepare()
        } catch (e: Exception) {
            Log.w(TAG, "prepareSafely: Failed to prepare recorder: ${e.localizedMessage}")
        }

    fun MediaRecorder.startSafely() = try {
        start()
    } catch (e: Exception) {
        Log.w(TAG, "playSafely: Failed to start recorder: ${e.localizedMessage}")
    }

    fun MediaRecorder.stopSafely() = try {
        stop()
        release()
    } catch (e: Exception) {
        Log.d(TAG, "stopSafely: Failed to stop recorder: ${e.localizedMessage}")
    }
}