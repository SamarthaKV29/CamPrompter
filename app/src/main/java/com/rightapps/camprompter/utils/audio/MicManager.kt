package com.rightapps.camprompter.utils.audio

import android.content.Context
import android.media.AudioManager
import android.media.MediaRecorder
import android.media.MediaRecorder.OnInfoListener
import android.os.Build
import android.util.Log
import com.rightapps.camprompter.utils.FileUtils

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

    fun getRecorder(context: Context) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else MediaRecorder()

    fun MediaRecorder.prepareSafely(context: Context, onInfoListener: OnInfoListener? = null) =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setAudioSource(MediaRecorder.AudioSource.VOICE_PERFORMANCE)
            } else {
                setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            }
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