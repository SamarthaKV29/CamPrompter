package com.rightapps.camprompter.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R


object Utility {

    fun showAppSettingsPage(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    fun getPermissionAlert(
        activity: AppCompatActivity,
        message: String,
        type: KAlertDialogType,
        onConfirmClick: KAlertDialog.KAlertClickListener
    ): KAlertDialog = KAlertDialog(activity, true).apply {
        changeAlertType(type.ordinal)
        setTitle(context.getString(R.string.permission_required))
        contentText = message
        setCancelable(false)
        setConfirmClickListener("OK", onConfirmClick)
    }
}