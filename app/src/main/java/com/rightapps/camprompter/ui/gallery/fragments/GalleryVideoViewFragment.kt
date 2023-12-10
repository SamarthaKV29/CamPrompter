package com.rightapps.camprompter.ui.gallery.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rightapps.camprompter.R
import com.rightapps.camprompter.ui.gallery.GalleryActivity
import com.rightapps.camprompter.utils.UISharedGlue

class GalleryVideoViewFragment : Fragment(R.layout.fragment_gallery_video_view) {
    companion object {
        const val TAG: String = "GalleryVideoViewFragment"
    }

    private val sharedGlue: UISharedGlue by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedGlue.galleryFragmentType.value =
            GalleryActivity.Companion.GalleryFragmentType.GalleryVideoView
    }


}