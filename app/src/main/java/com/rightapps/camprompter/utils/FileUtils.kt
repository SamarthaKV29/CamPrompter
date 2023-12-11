package com.rightapps.camprompter.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.rightapps.camprompter.R
import java.io.File
import java.util.Date
import java.util.Locale

object FileUtils {
    private const val TAG: String = "FileUtils"

    enum class FileType {
        AUDIO_FILE,
        VIDEO_FILE
    }

    fun getAudioRecordingsDir() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Environment.DIRECTORY_RECORDINGS else Environment.DIRECTORY_MUSIC

    private fun getMediaDirPath(context: Context, type: FileType) = when (type) {
        FileType.VIDEO_FILE -> "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path}/${
            context.getString(R.string.app_name)
        }"

        FileType.AUDIO_FILE -> "${
            Environment.getExternalStoragePublicDirectory(
                getAudioRecordingsDir()
            ).path
        }/${
            context.getString(R.string.app_name)
        }"
    }

    fun rescanMedia(
        context: Context,
        type: FileType,
        scanCompletedListener: OnScanCompletedListener? = null
    ) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(
                getMediaDirPath(context, type)
            ),
            arrayOf(
                getMediaMimeType(type)
            ), scanCompletedListener
        )
    }

    private fun getMediaMimeType(type: FileType) = when (type) {
        FileType.VIDEO_FILE -> "video/mp4"
        FileType.AUDIO_FILE -> "audio/aac"
    }

    private fun getMediaDir(context: Context, type: FileType): File? {
        return File(getMediaDirPath(context, type)).apply {
            if (!exists()) try {
                mkdirs()
            } catch (_: Exception) {
                Log.d(TAG, "getMediaDir: Failed to create data dir")
            }
        }.takeIf { it.exists() }
    }

    fun getOutputFile(context: Context, type: FileType): File? = getMediaDir(context, type)?.let {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        when (type) {
            FileType.VIDEO_FILE -> File("${it.path}${File.separator}VID_$timeStamp.mp4")
            FileType.AUDIO_FILE -> File("${it.path}${File.separator}AUD_$timeStamp.aac")
        }
    }

    fun deleteMedia(uri: Uri): Boolean = uri.path?.let {
        File(it).takeIf { file -> file.exists() }?.delete()
    } ?: false

    fun removeMedia(context: Context, uri: Uri): Boolean = try {
        val wasDeleted = context.contentResolver.delete(uri, null, null) >= 1
        Log.d(TAG, "removeMedia: file was deleted: $wasDeleted")
        wasDeleted
    } catch (e: Exception) {
        Log.d(TAG, "removeMedia: Failed to remove media: ${e.localizedMessage}")
        false
    }

    fun getMediaDataCursor(context: Context, type: FileType) = when (type) {
        FileType.VIDEO_FILE -> context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE),
            MediaStore.Video.Media.DATA + " like ? ",
            arrayOf("%/DCIM/${context.getString(R.string.app_name)}/%"),
            null
        )

        FileType.AUDIO_FILE -> context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE),
            MediaStore.Audio.Media.DATA + " like ? ",
            arrayOf("%/${getAudioRecordingsDir()}/${context.getString(R.string.app_name)}/%"),
            null
        )
    }


}