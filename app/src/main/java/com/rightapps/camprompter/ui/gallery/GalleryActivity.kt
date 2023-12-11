package com.rightapps.camprompter.ui.gallery

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.commit
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.ui.gallery.fragments.GalleryViewFragment
import com.rightapps.camprompter.utils.FileUtils
import com.rightapps.camprompter.utils.KAlertDialogType
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.Utility

class GalleryActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "GalleryActivity"

        enum class GalleryFragmentType(value: Int) {
            GalleryView(0),
            GalleryVideoView(0)
        }
    }

    private val sharedGlue: UISharedGlue by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setUpTopbar()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            disallowAddToBackStack()
        }
        loadGalleryView(FileUtils.FileType.VIDEO_FILE)
    }

    private fun loadGalleryView(type: FileUtils.FileType) {
        supportFragmentManager.commit {
            sharedGlue.galleryViewType.value = type
            replace(R.id.galleryFragmentHolder, GalleryViewFragment(type))
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
            toolbar.inflateMenu(R.menu.gallery_menu)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menuGalleryType -> {
                        if (sharedGlue.galleryViewType.value == FileUtils.FileType.VIDEO_FILE) {
                            loadGalleryView(FileUtils.FileType.AUDIO_FILE)
                        } else {
                            loadGalleryView(FileUtils.FileType.VIDEO_FILE)
                        }
                    }

                    R.id.menuMultiSelect -> {
                        sharedGlue.isSelectingGalleryItems.value =
                            !(sharedGlue.isSelectingGalleryItems.value ?: false)
                        toolbar.menu.findItem(R.id.menuGalleryType).isVisible =
                            (sharedGlue.isSelectingGalleryItems.value ?: false).not()
                    }

                    R.id.menuSelectAll -> {
                        supportFragmentManager.setFragmentResult(
                            GalleryViewFragment.ACTION_SELECT_ALL,
                            Bundle()
                        )
                    }

                    R.id.menuDeleteSelected -> {
                        KAlertDialog(this).apply {
                            setTitle("Confirm")
                            contentText = "Are you sure you want to delete these videos?!"
                            setCancelable(false)
                            changeAlertType(KAlertDialogType.WARNING_TYPE.ordinal)
                            setConfirmClickListener("Yes") {
                                supportFragmentManager.setFragmentResult(
                                    GalleryViewFragment.ACTION_DELETE_SELECTION,
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
                onBackPressed()
            }
            sharedGlue.isSelectingGalleryItems.observe(this) { isSelecting ->
                toolbar.menu.findItem(R.id.menuMultiSelect).icon =
                    (if (isSelecting) AppCompatResources.getDrawable(
                        this,
                        R.drawable.checkbox_off_icon
                    ) else AppCompatResources.getDrawable(
                        this,
                        R.drawable.checkbox_on_icon
                    ))?.apply {
                        DrawableCompat.setTint(this, getColor(R.color.white))
                    }
                toolbar.menu.findItem(R.id.menuGalleryType).isVisible =
                    (sharedGlue.isSelectingGalleryItems.value ?: false).not()
                toolbar.menu.findItem(R.id.menuSelectAll).apply {
                    isVisible = isSelecting
                    icon = AppCompatResources.getDrawable(
                        this@GalleryActivity,
                        R.drawable.select_all_icon
                    )?.apply {
                        DrawableCompat.setTint(this, getColor(R.color.white))
                    }
                }
            }
            sharedGlue.showDeleteButton.observe(this) {
                toolbar.menu.findItem(R.id.menuDeleteSelected).isVisible = it
            }
            sharedGlue.galleryViewType.observe(this) { type ->
                toolbar.menu.findItem(R.id.menuGalleryType).apply {
                    icon =
                        (if (type == FileUtils.FileType.VIDEO_FILE) AppCompatResources.getDrawable(
                            this@GalleryActivity,
                            R.drawable.tone_icon
                        ) else AppCompatResources.getDrawable(
                            this@GalleryActivity,
                            R.drawable.video_icon
                        ))?.apply { DrawableCompat.setTint(this, getColor(R.color.white)) }
                }

            }
            sharedGlue.galleryFragmentType.observe(this) {
                when (it) {
                    GalleryFragmentType.GalleryVideoView -> toolbar.menu.clear()
                    GalleryFragmentType.GalleryView ->
                        if (!toolbar.menu.hasVisibleItems()) toolbar.inflateMenu(R.menu.gallery_menu)

                    else -> toolbar.menu.clear()
                }
            }
        }
    }

}