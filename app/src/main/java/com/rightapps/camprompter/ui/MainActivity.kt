package com.rightapps.camprompter.ui

import PermissionUtils
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.utils.KAlertDialogType
import com.rightapps.camprompter.utils.Utility
import com.rightapps.camprompter.utils.ViewUtils.gone
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        setContentView(R.layout.activity_main)
        splashLyt = findViewById(R.id.splashLyt)
        supportFragmentManager.commit {
            replace(R.id.cameraFragmentHolder, CameraFragment())
            setReorderingAllowed(true)
            replace(R.id.bottomBarHolder, BottomMenuFragment())
            // addToBackStack("CameraView")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: $permissionRequestCount")
        PermissionUtils.checkPermissions(this)
        permissionRequestCount += 1

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            splashLyt.gone()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(this, requestCode, grantResults) {
            val message = when (permissionRequestCount) {
                1 -> "Please grant ${permissions.joinToString(", ")} permission"
                2 -> "Please grant ${permissions.joinToString(", ")} permission from settings page!"
                else -> "Permission required!"
            }
            if (permissionRequestCount >= 2) {
                permissionRequestCount = 0
                topDialog = null
                Utility.showAppSettingsPage(this)
            } else {
                topDialog = PermissionUtils.getPermissionAlert(
                    this, message, KAlertDialogType.ERROR_TYPE
                ) {
                    it.cancel()
                    PermissionUtils.checkPermissions(this)
                    permissionRequestCount += 1
                }
                topDialog?.show()
            }
        }
    }


}