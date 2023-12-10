package com.rightapps.camprompter.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.rightapps.camprompter.ui.gallery.GalleryActivity


object Utility {
    private const val TAG: String = "Utility"


    fun showAppSettingsPage(context: Context) {
        val uri = Uri.fromParts("package", context.packageName, null)
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = uri
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun showPreview(context: Context) {
        context.startActivity(
            Intent(context, GalleryActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            )
        )
    }

    fun getThumbnail(path: String?): Bitmap? = try {
        path?.let {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(it)
            val frame = retriever.frameAtTime
            retriever.release()
            frame
        }
    } catch (e: Exception) {
        Log.w(GalleryActivity.TAG, "getThumbnail: Failed to get thumb: ${e.localizedMessage}")
        null
    }
}