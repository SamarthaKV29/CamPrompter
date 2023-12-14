package com.rightapps.camprompter.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.ui.MainActivity
import com.rightapps.camprompter.ui.gallery.GalleryActivity
import com.rightapps.camprompter.ui.settings.CameraSettingsFragment
import com.rightapps.camprompter.ui.settings.SettingsActivity


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

    fun showCamSettings(activity: MainActivity) {
        val animatingRelativeLayout = activity.binding.bottomDrawer
        if (animatingRelativeLayout.isVisible) animatingRelativeLayout.hide() else animatingRelativeLayout.show()
        activity.supportFragmentManager.commit {
            replace(R.id.bottomDrawerFragmentHolder, CameraSettingsFragment())
        }
    }

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
        onConfirm: () -> Unit = {},
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
}