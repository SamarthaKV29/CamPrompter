package com.rightapps.camprompter.ui

import PermissionUtils
import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.commit
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.databinding.ActivityMainBinding
import com.rightapps.camprompter.ui.settings.CameraSettingsFragment
import com.rightapps.camprompter.utils.Utility
import com.rightapps.camprompter.utils.Utility.getPackageInfoCompat
import com.rightapps.camprompter.utils.views.BoundActivity
import com.rightapps.camprompter.utils.views.KAlertDialogType
import com.rightapps.camprompter.utils.views.UISharedGlue

class MainActivity : BoundActivity<ActivityMainBinding>() {
    companion object {
        const val TAG: String = "MainActivity"
        var permissionRequestCount = 0
    }

    private var topDialog: KAlertDialog? = null
    private val sharedGlue: UISharedGlue by viewModels()

    override fun setupViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun init() {
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

        setupBottomDrawer()

        sharedGlue.avSwitch.observe(this) { isVideo ->
            supportFragmentManager.commit {
                replace(
                    R.id.mainFragmentHolder,
                    if (isVideo) CameraFragment() else RecordingFragment()
                )
            }
        }

        sharedGlue.isRecordingAudio.observe(this) { isRecording ->
            supportFragmentManager.fragments.find { it.isVisible }?.let { visibleFragment ->
                if (isRecording && visibleFragment !is RecordingFragment) {
                    supportFragmentManager.commit {
                        replace(R.id.mainFragmentHolder, RecordingFragment())
                    }
                }
            }
        }
        sharedGlue.isRecordingVideo.observe(this) { isRecording ->
            supportFragmentManager.fragments.find { it.isVisible }?.let { visibleFragment ->
                if (isRecording && visibleFragment !is CameraFragment) {
                    supportFragmentManager.commit {
                        replace(R.id.mainFragmentHolder, CameraFragment())
                    }
                }
            }
        }
    }

    private fun setupBottomDrawer() {
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
//            Not working
//            binding.topStatusBar.statusBarAudioCapture.apply {
//                isVisible = isRecordingAudio
//                DrawableCompat.setTint(this.drawable, getColor(R.color.white))
//            }
        checkSpeechServiceAvailability()
    }

    private fun checkSpeechServiceAvailability() =
        applicationContext.packageManager.getPackageInfoCompat(
            "com.google.android.tts",
            PackageManager.GET_ACTIVITIES
        )?.let {
            Toast.makeText(this, "Speech Services available", Toast.LENGTH_SHORT).show()
        } ?: showSpeechServiceRequiredHint()

    private fun showSpeechServiceRequiredHint() = Utility.showSimpleAlertDialog(
        this,
        "Speech services required",
        "Please install Speech services from Google Play Store and download English language for best results!",
        alertType = KAlertDialogType.WARNING_TYPE,
        onConfirm = {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.google.android.tts")
                )
            )
        },
        cancelText = "Ignore",
    )

    fun showBottomDrawer() {
        supportFragmentManager.commit {
            replace(R.id.bottomDrawerFragmentHolder, CameraSettingsFragment())
        }
        binding.bottomDrawer.show()
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