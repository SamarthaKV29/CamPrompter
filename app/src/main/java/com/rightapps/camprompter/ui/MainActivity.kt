package com.rightapps.camprompter.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.audio.MicManager
import com.rightapps.camprompter.utils.KAlertDialogType
import com.rightapps.camprompter.utils.Utility
import com.rightapps.camprompter.utils.Utility.getPermissionAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "MainActivity"
        var permissionRequestCount = 0;


    }

    private lateinit var splashLyt: RelativeLayout
    private var topDialog: KAlertDialog? = null
    private lateinit var topbarMenuBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        setContentView(R.layout.activity_main)
        splashLyt = findViewById(R.id.splashLyt)
        topbarMenuBtn = findViewById(R.id.topbarMenuBtn)
        topbarMenuBtn.setOnClickListener {
            Utility.showAppSettingsPage(this)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        MicManager.checkAudioPermissions(this)
        permissionRequestCount += 1

        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            runOnUiThread {
                splashLyt.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        Log.d(TAG, "onResume: ${MicManager.getMicListAsString(applicationContext)}")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val granted = MicManager.handleOnRequestPermissionsResult(requestCode, grantResults)
        if (!granted) {
            val message = when (permissionRequestCount) {
                1 -> "Please grant Audio Record permission"
                2 -> "Please grant from settings page!"
                else -> "Audio permission required!"
            }
            if (permissionRequestCount >= 2) {
                permissionRequestCount = 0
                topDialog = null
                Utility.showAppSettingsPage(this)
            } else {
                topDialog = getPermissionAlert(
                    this, message, KAlertDialogType.ERROR_TYPE
                ) {
                    it.cancel()
                    MicManager.checkAudioPermissions(this)
                    permissionRequestCount += 1
                }
                topDialog?.show()
            }
        }
    }


}