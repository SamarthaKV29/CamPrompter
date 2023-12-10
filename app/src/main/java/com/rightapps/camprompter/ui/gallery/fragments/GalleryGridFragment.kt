package com.rightapps.camprompter.ui.gallery.fragments

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.rightapps.camprompter.R
import com.rightapps.camprompter.ui.gallery.GalleryActivity
import com.rightapps.camprompter.ui.gallery.GalleryGridAdapter
import com.rightapps.camprompter.utils.EmptyRecyclerView
import com.rightapps.camprompter.utils.FileUtils
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.Utility

class GalleryGridFragment : Fragment(R.layout.fragment_gallery_grid) {
    companion object {
        const val TAG: String = "GalleryGridFragment"
        const val ACTION_DELETE_SELECTION = "ACTION_DELETE_SELECTION"
    }

    private lateinit var galleryRV: EmptyRecyclerView
    private val sharedGlue: UISharedGlue by activityViewModels()
    val selectedItems = MutableLiveData<List<GalleryGridAdapter.Video>?>()
    private lateinit var galleryLoadingBar: CircularProgressIndicator
    val isLoading = MutableLiveData<Boolean>()

    private lateinit var gridAdapter: GalleryGridAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedGlue.galleryFragmentType.value =
            GalleryActivity.Companion.GalleryFragmentType.GalleryGrid
        galleryRV = view.findViewById(R.id.galleryRV)
        galleryRV.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        galleryRV.setEmptyView(layoutInflater.inflate(R.layout.empty_view, null))

        galleryLoadingBar = view.findViewById(R.id.galleryLoadingBar)

        isLoading.observe(viewLifecycleOwner) {
            galleryLoadingBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        isLoading.postValue(true)

        setFragmentResultListener(ACTION_DELETE_SELECTION) { _, _ ->
            isLoading.postValue(true)
            selectedItems.value?.forEach { video ->
                if (FileUtils.deleteDataFile(video.uri) && gridAdapter.videosData.remove(video)) {
                    Log.d(TAG, "onViewCreated: Delete success")
                } else
                    Log.w(TAG, "onViewCreated: Delete failed")
            }
            selectedItems.value = listOf()
            sharedGlue.isSelectingGalleryItems.value = false
            isLoading.postValue(false)
        }

        loadGallery()

        // Setup Observers
        sharedGlue.isSelectingGalleryItems.observe(viewLifecycleOwner) {
            loadGallery(it)
        }
        selectedItems.observe(viewLifecycleOwner) { selectedVideos ->
            Log.d(TAG, "onViewCreated: selected: ${selectedVideos?.size}")
            sharedGlue.showDeleteButton.value = selectedVideos?.isNotEmpty() == true
        }
    }

    private fun loadGallery(isSelecting: Boolean = false) =
        FileUtils.rescanMedia(requireContext()) { _, _ ->
            requireActivity().runOnUiThread {
                gridAdapter =
                    GalleryGridAdapter(
                        fetchVideos(requireContext()),
                        isSelecting,
                        selectedItems,
                        getItemClickListener()
                    )
                galleryRV.adapter = gridAdapter
                isLoading.postValue(false)
            }
        }

    private fun getItemClickListener() = object : EmptyRecyclerView.OnItemClickListener {
        override fun onItemClick(adapter: GalleryGridAdapter, item: GalleryGridAdapter.Video) {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.galleryFragmentHolder, GalleryVideoViewFragment(item.uri))
                addToBackStack("grid")
            }
        }
    }

    private fun fetchVideos(context: Context): List<GalleryGridAdapter.Video> {
        val videos = mutableListOf<GalleryGridAdapter.Video>()

        val cursor: Cursor? = FileUtils.getMediaDataDirCursor(context)

        cursor?.use {
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)

            while (cursor.moveToNext()) {
                val data = cursor.getString(dataIndex)
                val title = cursor.getString(titleIndex)
                val pathUri = Uri.parse(data)
                videos.add(
                    GalleryGridAdapter.Video(
                        pathUri,
                        title,
                        false,
                        Utility.getThumbnail(pathUri.path)
                    )
                )
            }
        }
        cursor?.close()
        Log.d(GalleryActivity.TAG, "fetchVideos: ${videos.size}")

        return videos
    }


}