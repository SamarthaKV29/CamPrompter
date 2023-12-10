package com.rightapps.camprompter.ui.gallery.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.rightapps.camprompter.R
import com.rightapps.camprompter.ui.gallery.GalleryActivity
import com.rightapps.camprompter.utils.UISharedGlue

class GalleryVideoViewFragment(private val uri: Uri) :
    Fragment(R.layout.fragment_gallery_video_view) {
    companion object {
        const val TAG: String = "GalleryVideoViewFragment"
    }

    private val sharedGlue: UISharedGlue by activityViewModels()
    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedGlue.galleryFragmentType.value =
            GalleryActivity.Companion.GalleryFragmentType.GalleryVideoView
        playerView = view.findViewById(R.id.playerView)
        player = ExoPlayer.Builder(requireContext()).build()
        playerView.player = player

        player.addMediaItem(MediaItem.Builder().setUri(uri).build())
        player.prepare()
        player.play()

    }

    override fun onStop() {
        player.release()
        super.onStop()
    }


}