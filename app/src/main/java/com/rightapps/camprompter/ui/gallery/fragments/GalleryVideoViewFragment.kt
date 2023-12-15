package com.rightapps.camprompter.ui.gallery.fragments

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.rightapps.camprompter.databinding.FragmentGalleryVideoViewBinding
import com.rightapps.camprompter.ui.gallery.GalleryActivity
import com.rightapps.camprompter.utils.views.BoundFragment
import com.rightapps.camprompter.utils.views.UISharedGlue

class GalleryVideoViewFragment(private val uri: Uri) :
    BoundFragment<FragmentGalleryVideoViewBinding>() {
    companion object {
        const val TAG: String = "GalleryVideoViewFragment"
    }

    private val sharedGlue: UISharedGlue by activityViewModels()
    private lateinit var player: ExoPlayer


    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGalleryVideoViewBinding {
        return FragmentGalleryVideoViewBinding.inflate(inflater, container, false)
    }

    override fun onViewCreate() {
        sharedGlue.galleryFragmentType.value =
            GalleryActivity.Companion.GalleryFragmentType.GalleryVideoView

        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player
//        val mimeType =
//            if (uri.path?.endsWith(".mp4") == true) MimeTypes.VIDEO_MP4 else MimeTypes.AUDIO_AAC
        player.addMediaItem(
            MediaItem.Builder()
                .setUri(uri)
//                .setMimeType(mimeType)
                .build()
        )
        player.tryPlay()
    }


    override fun onStop() {
        super.onStop()
        player.tryStop()
    }

    private fun ExoPlayer.tryStop() = try {
        player.stop()
    } catch (e: Exception) {
        Toast.makeText(requireContext(), "Failed to stop media", Toast.LENGTH_SHORT).show()
        Log.w(TAG, "tryPlay: Failed to stop media: ${e.localizedMessage}")
    } finally {
        player.release()
    }

    private fun ExoPlayer.tryPlay() = try {
        prepare()
        play()
    } catch (e: Exception) {
        Toast.makeText(requireContext(), "Failed to play media", Toast.LENGTH_SHORT).show()
        Log.w(TAG, "tryPlay: Failed to play media: ${e.localizedMessage}")

    }
}