package com.rightapps.camprompter.ui.gallery.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rightapps.camprompter.R
import com.rightapps.camprompter.databinding.FragmentGalleryViewBinding
import com.rightapps.camprompter.ui.gallery.GalleryActivity
import com.rightapps.camprompter.ui.gallery.GalleryAdapter
import com.rightapps.camprompter.utils.FileUtils
import com.rightapps.camprompter.utils.views.BoundFragment
import com.rightapps.camprompter.utils.views.EmptyRecyclerView
import com.rightapps.camprompter.utils.views.UISharedGlue

class GalleryViewFragment(private val type: FileUtils.FileType) :
    BoundFragment<FragmentGalleryViewBinding>() {
    companion object {
        const val TAG: String = "GalleryGridFragment"
        const val ACTION_SELECT_ALL = "ACTION_SELECT_ALL"
        const val ACTION_DELETE_SELECTION = "ACTION_DELETE_SELECTION"
    }

    private var gridAdapter: GalleryAdapter? = null

    private val sharedGlue: UISharedGlue by activityViewModels()

    //    private val selectedItems = MutableLiveData<List<GalleryAdapter.MediaFile>>()
    private val isLoading = MutableLiveData<Boolean>()

    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGalleryViewBinding {
        return FragmentGalleryViewBinding.inflate(inflater, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreate() {
        sharedGlue.galleryFragmentType.value =
            GalleryActivity.Companion.GalleryFragmentType.GalleryView

        binding.galleryRV.layoutManager = when (type) {
            FileUtils.FileType.VIDEO_FILE -> GridLayoutManager(
                context,
                3,
                GridLayoutManager.VERTICAL,
                false
            )

            FileUtils.FileType.AUDIO_FILE -> LinearLayoutManager(context)
        }

        binding.galleryRV.setEmptyView(layoutInflater.inflate(R.layout.empty_view, null))

        isLoading.observe(viewLifecycleOwner) {
            binding.galleryLoadingBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        isLoading.postValue(true)

        setFragmentResultListener(ACTION_DELETE_SELECTION) { _, _ ->
            isLoading.postValue(true)
            gridAdapter?.removeSelectedItems()
            sharedGlue.isSelectingGalleryItems.value = false
            isLoading.postValue(false)
        }

        setFragmentResultListener(ACTION_SELECT_ALL) { _, _ ->
            val isAllSelected = gridAdapter?.mediaData?.all { it.isSelected } ?: false
            Log.d(TAG, "onViewCreated: isAllSelected: $isAllSelected")
            gridAdapter?.setSelectedAll(!isAllSelected)
            sharedGlue.isSelectingGalleryItems.postValue(!isAllSelected)
        }

        loadGallery()
        binding.galleryRV.setEmptyView(layoutInflater.inflate(R.layout.empty_view, null))
        // Setup Observers
        sharedGlue.isSelectingGalleryItems.observe(viewLifecycleOwner) {
            // Reset adapter
            gridAdapter?.notifySafely {
                gridAdapter?.notifyDataSetChanged()
            }
            sharedGlue.showDeleteButton.postValue((gridAdapter?.getSelectionCount() ?: 0) > 0)
        }
    }

    private fun loadGallery() =
        FileUtils.rescanMedia(requireContext(), type) { _, _ ->
            requireActivity().runOnUiThread {
                gridAdapter =
                    GalleryAdapter(
                        fetchMedia(requireContext()),
                        type,
                        sharedGlue.isSelectingGalleryItems,
                        getItemClickListener()
                    )
                binding.galleryRV.adapter = gridAdapter
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
                        if (type == FileUtils.FileType.VIDEO_FILE) FileUtils.getThumbnail(pathUri.path) else null
                    )
                )
            }
        }
        cursor?.close()
        Log.d(TAG, "fetchMedia: ${mediaFiles.size}")

        return mediaFiles
    }


}