package com.rightapps.camprompter.ui.gallery

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.developer.kalert.KAlertDialog
import com.rightapps.camprompter.R
import com.rightapps.camprompter.databinding.ActivityGalleryBinding
import com.rightapps.camprompter.ui.gallery.fragments.GalleryViewFragment
import com.rightapps.camprompter.utils.FileUtils
import com.rightapps.camprompter.utils.KAlertDialogType
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.Utility
import com.rightapps.camprompter.utils.ViewUtils.fixCheckStateOnIcon

class GalleryActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "GalleryActivity"

        enum class GalleryFragmentType(value: Int) {
            GalleryView(0),
            GalleryVideoView(1)
        }
    }

    private val sharedGlue: UISharedGlue by viewModels()
    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpTopbar()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            disallowAddToBackStack()
        }
        loadGalleryView(FileUtils.FileType.VIDEO_FILE)

        Utility.registerBackPressListener(this) {
            onBackPress()
        }
    }

    private fun loadGalleryView(type: FileUtils.FileType) {
        supportFragmentManager.commit {
            sharedGlue.galleryViewType.value = type
            replace(R.id.galleryFragmentHolder, GalleryViewFragment(type))
        }
    }

    private fun onBackPress() {
        Log.d(TAG, "onBackPress: IsTaskRoot: $isTaskRoot")
        if (isTaskRoot) {
            Utility.showHome(applicationContext)
        }
        finish()
    }

    private fun setUpTopbar() {
        binding.topbar.toolbar.let { toolbar ->
//            setSupportActionBar(toolbar) // This causes menu not be shown
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
            title = getString(R.string.title_gallery)
            toolbar.setNavigationOnClickListener {
                onBackPress()
            }
            // Setup Options Menu
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

                    else -> {
                        Log.d(TAG, "Unknown clicked: ${it.title}")
                    }
                }
                false
            }

            toolbar.menu.findItem(R.id.menuGalleryType).fixCheckStateOnIcon()
            toolbar.invalidateMenu()

            sharedGlue.isSelectingGalleryItems.observe(this) { isSelecting ->
                toolbar.menu.findItem(R.id.menuSelectAll)?.isVisible = isSelecting
                toolbar.menu.findItem(R.id.menuGalleryType)?.isVisible = !isSelecting
                toolbar.menu.findItem(R.id.menuSelectAll)?.isVisible = isSelecting

                toolbar.invalidateMenu()
            }
            sharedGlue.showDeleteButton.observe(this) {
                toolbar.menu.findItem(R.id.menuDeleteSelected).isVisible = it
                toolbar.invalidateMenu()
            }
            sharedGlue.galleryViewType.observe(this) { type ->
                toolbar.menu.findItem(R.id.menuGalleryType)?.isChecked =
                    (type != FileUtils.FileType.VIDEO_FILE)
                toolbar.invalidateMenu()
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