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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.rightapps.camprompter.R
import com.rightapps.camprompter.ui.gallery.GalleryActivity
import com.rightapps.camprompter.ui.gallery.GalleryAdapter
import com.rightapps.camprompter.utils.EmptyRecyclerView
import com.rightapps.camprompter.utils.FileUtils
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.Utility

class GalleryViewFragment(private val type: FileUtils.FileType) :
    Fragment(R.layout.fragment_gallery_grid) {
    companion object {
        const val TAG: String = "GalleryGridFragment"
        const val ACTION_SELECT_ALL = "ACTION_SELECT_ALL"
        const val ACTION_DELETE_SELECTION = "ACTION_DELETE_SELECTION"
    }

    private lateinit var galleryLoadingBar: CircularProgressIndicator
    private lateinit var galleryRV: EmptyRecyclerView
    private lateinit var gridAdapter: GalleryAdapter

    private val sharedGlue: UISharedGlue by activityViewModels()
    private val selectedItems = MutableLiveData<List<GalleryAdapter.MediaFile>?>()
    private val isLoading = MutableLiveData<Boolean>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedGlue.galleryFragmentType.value =
            GalleryActivity.Companion.GalleryFragmentType.GalleryView
        galleryRV = view.findViewById(R.id.galleryRV)
        galleryRV.layoutManager = when (type) {
            FileUtils.FileType.VIDEO_FILE -> GridLayoutManager(
                context,
                3,
                GridLayoutManager.VERTICAL,
                false
            )

            FileUtils.FileType.AUDIO_FILE -> LinearLayoutManager(context)
        }

        galleryRV.setEmptyView(layoutInflater.inflate(R.layout.empty_view, null))

        galleryLoadingBar = view.findViewById(R.id.galleryLoadingBar)

        isLoading.observe(viewLifecycleOwner) {
            galleryLoadingBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        isLoading.postValue(true)

        setFragmentResultListener(ACTION_DELETE_SELECTION) { _, _ ->
            isLoading.postValue(true)
            gridAdapter.removeItems()
            sharedGlue.isSelectingGalleryItems.value = false
            isLoading.postValue(false)
        }

        setFragmentResultListener(ACTION_SELECT_ALL) { _, _ ->
            val isAllSelected = gridAdapter.mediaData.all { it.isSelected }
            gridAdapter.mediaData.forEachIndexed { index, mediaFile ->
                mediaFile.isSelected = !isAllSelected
                gridAdapter.notifySafely(index)
            }
            selectedItems.postValue(gridAdapter.mediaData)
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
        FileUtils.rescanMedia(requireContext(), type) { _, _ ->
            requireActivity().runOnUiThread {
                gridAdapter =
                    GalleryAdapter(
                        fetchMedia(requireContext()),
                        type,
                        isSelecting,
                        selectedItems,
                        getItemClickListener()
                    )
                galleryRV.adapter = gridAdapter
                isLoading.postValue(false)
            }
        }

    private fun getItemClickListener() = object : EmptyRecyclerView.OnItemClickListener {
        override fun onItemClick(adapter: GalleryAdapter, item: GalleryAdapter.MediaFile) {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.galleryFragmentHolder, GalleryVideoViewFragment(item.uri))
                addToBackStack("grid")
            }
        }
    }

    private fun fetchMedia(context: Context): List<GalleryAdapter.MediaFile> {
        val mediaFiles = mutableListOf<GalleryAdapter.MediaFile>()

        val cursor: Cursor? = FileUtils.getMediaDataCursor(context, type)

        cursor?.use {
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)

            while (cursor.moveToNext()) {
                val data = cursor.getString(dataIndex)
                val title = cursor.getString(titleIndex)
                val pathUri = Uri.parse(data)
                mediaFiles.add(
                    GalleryAdapter.MediaFile(
                        pathUri,
                        title,
                        false,
                        if (type == FileUtils.FileType.VIDEO_FILE) Utility.getThumbnail(pathUri.path) else null
                    )
                )
            }
        }
        cursor?.close()
        Log.d(TAG, "fetchMedia: ${mediaFiles.size}")

        return mediaFiles
    }


}