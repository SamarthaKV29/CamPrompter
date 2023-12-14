package com.rightapps.camprompter.ui

import PermissionUtils
import android.app.ActionBar.LayoutParams
import android.content.res.Configuration
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.commit
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.databinding.ActivityMainBinding
import com.rightapps.camprompter.ui.settings.SettingsFragment
import com.rightapps.camprompter.utils.KAlertDialogType
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.Utility
import com.rightapps.camprompter.utils.audio.MicManager
import com.rightapps.camprompter.utils.audio.MicManager.prepareSafely
import com.rightapps.camprompter.utils.audio.MicManager.startSafely
import com.rightapps.camprompter.utils.audio.MicManager.stopSafely

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "MainActivity"
        var permissionRequestCount = 0;
    }

    lateinit var binding: ActivityMainBinding

    private val sharedGlue: UISharedGlue by viewModels()
    private var topDialog: KAlertDialog? = null
    private var recorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            replace(R.id.mainFragmentHolder, CameraFragment())
            setReorderingAllowed(true)
            replace(R.id.bottomBarHolder, BottomMenuFragment())
            // addToBackStack("CameraView")
        }

        Utility.registerBackPressListener(this) {
            Utility.showSimpleAlertDialog(this,
                message = "Are you sure you want to exit?",
                onConfirm = {
                    finish()
                })
        }

        // Cannot be done via xml
        binding.bottomDrawer.background.alpha = 225
        binding.bottomDrawer.layoutParams =
            ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    windowManager.currentWindowMetrics.bounds.height() / 3
                } else {
                    applicationContext.resources.displayMetrics.heightPixels / 3
                }
            }).apply {
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
    }

    override fun onStart() {
        super.onStart()
        PermissionUtils.checkPermissions(this)
        permissionRequestCount += 1

        sharedGlue.isRecordingAudio.observe(this) { isRecordingAudio ->
            if (isRecordingAudio) {
                recorder = MicManager.getRecorder(applicationContext).apply {
                    prepareSafely(applicationContext) { _, what, _ ->
                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                            stopSafely()
                            sharedGlue.isRecordingAudio.value = false
                        }
                    }
                    startSafely()
                }
            } else {
                recorder?.apply { stopSafely() }
            }
//            Not working
//            binding.topStatusBar.statusBarAudioCapture.apply {
//                isVisible = isRecordingAudio
//                DrawableCompat.setTint(this.drawable, getColor(R.color.white))
//            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, grantResults) {
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged: Orientation: ${newConfig.orientation}")
    }


}