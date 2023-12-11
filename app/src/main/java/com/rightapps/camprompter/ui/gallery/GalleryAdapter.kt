package com.rightapps.camprompter.ui.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rightapps.camprompter.R
import com.rightapps.camprompter.utils.EmptyRecyclerView
import com.rightapps.camprompter.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GalleryAdapter(
    mediaFiles: List<MediaFile>,
    private val galleryType: FileUtils.FileType,
    private val isSelecting: Boolean,
    private val selectedItems: MutableLiveData<List<MediaFile>?>,
    private val onItemClickListener: EmptyRecyclerView.OnItemClickListener? = null
) :
    RecyclerView.Adapter<GalleryAdapter.MediaViewHolder>() {

    companion object {
        const val TAG: String = "GalleryAdapter"
    }

    private val galleryGridAdapter = this
    val mediaData = mediaFiles.toMutableList()

    inner class MediaViewHolder(itemView: View) : ViewHolder(itemView) {
        val title: TextView? = itemView.findViewById(R.id.trackTitle)
        val videoThumbnail: ImageView? = itemView.findViewById(R.id.videoThumbnail)
        val mediaIsSelected: ImageView = itemView.findViewById(R.id.isSelected)
        // Other views

        fun getContext(): Context = itemView.context

        fun bind(item: MediaFile, listener: EmptyRecyclerView.OnItemClickListener? = null) {
            if (isSelecting) {
                itemView.setOnClickListener {
                    val idx = getMediaIndex(item)
                    if (idx >= 0) {
                        mediaData[idx].isSelected =
                            mediaData[idx].isSelected.not()
                        selectedItems.postValue(mediaData.filter { it.isSelected })
                        notifySafely(idx)
                    }
                }
            } else {
                mediaData.filter { it.isSelected }.forEachIndexed { idx, video ->
                    video.isSelected = false
                    notifySafely(idx)
                    selectedItems.postValue(null)
                }
                listener?.let {
                    itemView.setOnClickListener { _ ->
                        it.onItemClick(galleryGridAdapter, item)
                    }
                }
            }
        }
    }

    fun getMediaIndex(item: MediaFile) = mediaData.indexOf(item)

    fun notifySafely(idx: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(150)
            notifyItemChanged(idx)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = if (galleryType == FileUtils.FileType.VIDEO_FILE) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_video_item, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_audio_item, parent, false)

        }
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
//        Log.d(TAG, "onBindViewHolder: ${video.uri.path}")
        mediaData[position].let { video ->
            // Load video thumbnail into videoThumbnail view
            video.thumbnail?.let {
                holder.videoThumbnail?.setImageBitmap(it)
            }
            video.title.let {
                holder.title?.text = it
            }
            // Set other views
            if (isSelecting) {
                holder.mediaIsSelected.visibility = View.VISIBLE
                holder.mediaIsSelected.setImageDrawable(
                    if (video.isSelected)
                        AppCompatResources.getDrawable(
                            holder.getContext(),
                            android.R.drawable.checkbox_on_background
                        )
                    else
                        AppCompatResources.getDrawable(
                            holder.getContext(),
                            android.R.drawable.checkbox_off_background
                        )
                )
            } else holder.mediaIsSelected.visibility = View.GONE

            // Bind Item click listener
            holder.bind(video, onItemClickListener)
        }
    }

    override fun getItemCount() = mediaData.size

    @SuppressLint("NotifyDataSetChanged")
    fun removeItems() {
        selectedItems.value?.forEach { mediaFile ->
            if (FileUtils.deleteMedia(mediaFile.uri)) {
                Log.d(TAG, "removeItems: Delete ${mediaFile.title} success")
            } else {
                Log.d(TAG, "removeItems: Delete ${mediaFile.title} failed")
            }
        }
        selectedItems.value = listOf()
    }

    data class MediaFile(
        val uri: Uri,
        val title: String,
        var isSelected: Boolean,
        val thumbnail: Bitmap?,
    )

}


