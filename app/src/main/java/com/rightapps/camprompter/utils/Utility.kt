package com.rightapps.camprompter.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.ui.MainActivity
import com.rightapps.camprompter.ui.gallery.GalleryActivity
import com.rightapps.camprompter.ui.settings.SettingsActivity
import com.rightapps.camprompter.utils.views.KAlertDialogType


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

    fun showGallery(context: Context) = launchActivity(context, GalleryActivity::class.java)

    fun showHome(context: Context) = launchActivity(context, MainActivity::class.java)

    fun showAppSettings(context: Context) = launchActivity(context, SettingsActivity::class.java)

    private fun launchActivity(context: Context, activity: Class<*>) = context.startActivity(
        Intent(context, activity).addFlags(
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        )
    )

    fun showSimpleAlertDialog(
        context: Context,
        title: String = "Please confirm!",
        message: String = "Are you sure?",
        confirmText: String = "OK",
        onConfirm: () -> Unit = {},
        cancelText: String = "Cancel",
        onCancel: () -> Unit = {},
        cancelable: Boolean = false,
        alertType: KAlertDialogType = KAlertDialogType.NORMAL_TYPE,
    ) = KAlertDialog(context).apply {
        setTitle(title)
        contentText = message
        setCancelable(cancelable)
        changeAlertType(alertType.ordinal)
        setConfirmClickListener("OK") { onConfirm(); this.cancel() }
        setCancelClickListener("Cancel") { onCancel(); this.cancel() }
        show()
    }

    fun registerBackPressListener(activity: AppCompatActivity, onBackPress: () -> Unit) {
        activity.onBackPressedDispatcher.addCallback(activity /* lifecycle owner */) {
            // Back is pressed... Finishing the activity
            onBackPress()
        }
    }

    fun isViewContains(view: View, p: Point): Boolean {
        val l = IntArray(2)
        view.getLocationOnScreen(l)
        val x = l[0]
        val y = l[1]
        val w = view.width
        val h = view.height
        Log.d(TAG, "isViewContains: Point: $p, (x,y): ($x, $y), w: $w, h: $h")
        return !(p.x < x || p.x > x + w || p.y < y || p.y > y + h)
    }

    fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo? =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
            }
        } catch (e: Exception) {
            Log.w(TAG, "getPackageInfoCompat: Package $packageName not found!")
            null
        }

}