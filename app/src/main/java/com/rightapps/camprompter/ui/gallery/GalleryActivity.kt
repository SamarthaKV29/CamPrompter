package com.rightapps.camprompter.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.ui.MainActivity
import com.rightapps.camprompter.ui.gallery.fragments.GalleryGridFragment
import com.rightapps.camprompter.utils.KAlertDialogType
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.Utility

class GalleryActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "GalleryActivity"

        enum class GalleryFragmentType(value: Int) {
            GalleryGrid(0),
            GalleryVideoView(0)
        }
    }

    val sharedGlue: UISharedGlue by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setUpTopbar()
        supportFragmentManager.commit {
            replace(R.id.galleryFragmentHolder, GalleryGridFragment())
            setReorderingAllowed(true)
        }
    }

    private fun setUpTopbar() {
        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
//            setSupportActionBar(toolbar) // This causes menu not be shown
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
            title = getString(R.string.title_gallery)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menuMultiSelect -> {
                        sharedGlue.isSelectingGalleryItems.value =
                            !(sharedGlue.isSelectingGalleryItems.value ?: false)
                    }

                    R.id.menuDeleteSelected -> {
                        KAlertDialog(this).apply {
                            setTitle("Confirm")
                            contentText = "Are you sure you want to delete these videos?!"
                            setCancelable(false)
                            changeAlertType(KAlertDialogType.WARNING_TYPE.ordinal)
                            setConfirmClickListener("Yes") {
                                supportFragmentManager.setFragmentResult(
                                    GalleryGridFragment.ACTION_DELETE_SELECTION,
                                    Bundle()
                                )
                                cancel()
                            }
                            setCancelClickListener("No") {
                                cancel()
                            }
                        }.show()
                    }

                    R.id.menuAppInfo -> {
                        Utility.showAppSettingsPage(applicationContext)
                    }

                    R.id.menuPref -> {
                        Log.d(TAG, "onCreate: Prefs clicked")
                    }

                    else -> {
                        Log.d(TAG, "Unknown clicked: ${it.title}")
                    }
                }
                false
            }
            toolbar.setNavigationOnClickListener {
                if (sharedGlue.galleryFragmentType.value == GalleryFragmentType.GalleryVideoView) {
                    supportFragmentManager.commit {
                        replace(R.id.galleryFragmentHolder, GalleryGridFragment())
                        setReorderingAllowed(true)
                    }
                } else {
                    onBackPressed()
                }
            }
            sharedGlue.isSelectingGalleryItems.observe(this) { isSelecting ->
                toolbar.menu.findItem(R.id.menuMultiSelect).icon =
                    if (isSelecting) AppCompatResources.getDrawable(
                        this,
                        android.R.drawable.checkbox_off_background
                    ) else AppCompatResources.getDrawable(
                        this,
                        android.R.drawable.checkbox_on_background
                    )
            }
            sharedGlue.showDeleteButton.observe(this) {
                toolbar.menu.findItem(R.id.menuDeleteSelected).isVisible = it
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(this, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            )
        )
        finish()
    }

}