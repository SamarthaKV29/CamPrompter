package com.rightapps.camprompter.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import com.rightapps.camprompter.R
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