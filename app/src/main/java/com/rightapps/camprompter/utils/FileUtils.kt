package com.rightapps.camprompter.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.rightapps.camprompter.R
import java.io.File
import java.util.Date
import java.util.Locale

object FileUtils {
    private const val TAG: String = "FileUtils"

    private fun getDataDir(context: Context) =
        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path}/${
            context.getString(R.string.app_name)
        }"

    fun rescanMedia(context: Context, scanCompletedListener: OnScanCompletedListener? = null) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(
                getDataDir(context)
            ),
            null, scanCompletedListener
        )
    }

    fun getOutputMediaFile(context: Context): File? {
        val mediaStorageDir = File(getDataDir(context))
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "getOutputMediaFile: Failed to create dir")
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
    }

    fun deleteDataFile(uri: Uri): Boolean = uri.path?.let {
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

    fun getMediaDataDirCursor(context: Context) = context.contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE),
        MediaStore.Video.Media.DATA + " like ? ",
        arrayOf("%/DCIM/${context.getString(R.string.app_name)}/%"),
        null
    )
}