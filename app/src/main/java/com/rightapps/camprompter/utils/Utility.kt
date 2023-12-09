package com.rightapps.camprompter.utils

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import com.rightapps.camprompter.R
import com.rightapps.camprompter.ui.GalleryActivity
import java.io.File
import java.util.Date
import java.util.Locale


object Utility {
    const val TAG: String = "Utility"

    fun showAppSettingsPage(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    fun getOutputMediaFile(context: Context): File {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            context.getString(R.string.app_name)
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "getOutputMediaFile: Failed to create dir")
                return File("")
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
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

}