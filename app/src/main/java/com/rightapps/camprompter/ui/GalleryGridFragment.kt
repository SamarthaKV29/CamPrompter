package com.rightapps.camprompter.ui

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.rightapps.camprompter.R
import com.rightapps.camprompter.utils.EmptyRecyclerView
import java.io.File

class GalleryGridFragment : Fragment(R.layout.fragment_gallery_grid) {
    companion object {
        const val TAG: String = "GalleryGridFragment"
    }

    private lateinit var galleryRV: EmptyRecyclerView
    val sharedGlue: UISharedGlue by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedGlue.galleryFragmentType.value =
            GalleryActivity.Companion.GalleryFragmentType.GalleryGrid
        galleryRV = view.findViewById(R.id.galleryRV)
        loadGallery()
    }

    private fun loadGallery() {
        val appFolder = File(
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path}/${
                getString(R.string.app_name)
            }"
        )
        Log.d(GalleryActivity.TAG, "onStart: appFolder: ${appFolder.path}")
        MediaScannerConnection.scanFile(
            context,
            arrayOf(
                appFolder.toString()
            ),
            null, null
        );

        val videos = context?.let { fetchVideos(it) } ?: listOf()
        val gridLayoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        galleryRV.apply {
            layoutManager = gridLayoutManager
            adapter = VideoAdapter(videos)
        }

        galleryRV.setEmptyView(layoutInflater.inflate(R.layout.empty_view, null))
    }

    private fun fetchVideos(context: Context): List<VideoAdapter.Video> {
        Log.d(GalleryActivity.TAG, "fetchVideos: ")
        val videos = mutableListOf<VideoAdapter.Video>()
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE)
        val selection = MediaStore.Video.Media.DATA + " like ? "
        val selectionArgs = arrayOf("%/DCIM/${getString(R.string.app_name)}/%") //

        val cursor: Cursor? =
            context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)

            while (cursor.moveToNext()) {
                val data = cursor.getString(dataIndex)
                val title = cursor.getString(titleIndex)
                val pathUri = Uri.parse(data)
                videos.add(VideoAdapter.Video(pathUri, title, getVideoThumbnail(pathUri.path)))
            }
        }

        Log.d(GalleryActivity.TAG, "fetchVideos: ${videos.size}")

        return videos
    }

    private fun getVideoThumbnail(videoPath: String?): Bitmap? = try {
        Log.d(GalleryActivity.TAG, "getVideoThumbnail: $videoPath")
        videoPath?.let {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(it)
            val frame = retriever.frameAtTime
            retriever.release()
            frame
        }
    } catch (e: Exception) {
        Log.w(GalleryActivity.TAG, "getVideoThumbnail: Failed to get thumb: ${e.localizedMessage}")
        null
    }
}