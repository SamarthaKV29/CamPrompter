package com.rightapps.camprompter.ui.gallery

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
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
    private val isSelectingItems: MutableLiveData<Boolean>,
    private val onItemClickListener: EmptyRecyclerView.OnItemClickListener? = null
) :
    RecyclerView.Adapter<GalleryAdapter.MediaViewHolder>() {

    companion object {
        const val TAG: String = "GalleryAdapter"
        const val NOTIFY_QUICK = 25
        const val NOTIFY_SLOW = 125

    }

    private val galleryGridAdapter = this
    val mediaData = mediaFiles.toMutableList()

    override fun getItemCount() = mediaData.size
    fun getSelectionCount(): Int = mediaData.filter { it.isSelected }.size

    private fun getMediaIndex(item: MediaFile) = mediaData.indexOf(item)

    private fun removeItem(item: MediaFile) {
        val indx = getMediaIndex(item)
        if (indx >= 0) {
            mediaData.removeAt(indx)
            notifySafely { notifyItemRemoved(indx) }
        }
    }

    fun removeSelectedItems() {
        mediaData.filter { it.isSelected }.forEach { mediaFile ->
            if (FileUtils.deleteMedia(mediaFile.uri)) {
                Log.d(TAG, "removeItems: Delete ${mediaFile.title} success")
                removeItem(mediaFile)
            } else {
                Log.d(TAG, "removeItems: Delete ${mediaFile.title} failed")
            }
        }
    }


    fun setSelected(mediaFile: MediaFile, selected: Boolean) {
        val indx = getMediaIndex(mediaFile)
        if (indx >= 0) {
            mediaData[indx].isSelected = selected
            notifySafely {
                Log.d(TAG, "setSelected: Notify item changed")
                notifyItemChanged(indx)
            }
        }
    }

    fun setSelectedAll(selected: Boolean) = mediaData.forEach { setSelected(it, selected) }

    inner class MediaViewHolder(itemView: View) : ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.card)
        val title: MaterialTextView? =
            itemView.findViewById(R.id.trackTitle) // Optional (?) because
        val videoThumbnail: ImageView? =
            itemView.findViewById(R.id.videoThumbnail) // depends on file type
//        val mediaIsSelected: MaterialCheckBox = itemView.findViewById(R.id.isSelected)
        // Other views

        fun getContext(): Context = itemView.context

        fun bind(
            item: MediaFile,
            isSelecting: Boolean,
            listener: EmptyRecyclerView.OnItemClickListener? = null
        ) {
            if (isSelecting) {
                itemView.setOnClickListener {
                    Log.d(TAG, "bind: Short click")
                    setSelected(item, !item.isSelected)

                    val doneSelecting = mediaData.none { it.isSelected }
                    isSelectingItems.postValue(!doneSelecting)
                }
                itemView.setOnLongClickListener(null)
            } else {
                itemView.setOnLongClickListener {
                    Log.d(TAG, "bind: Long click")
                    setSelected(item, true)
                    isSelectingItems.postValue(true)
                    true
                }
                itemView.setOnClickListener {
                    listener?.onItemClick(galleryGridAdapter, item)
                }
            }
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
        mediaData[position].let { mediaFile ->
//        Log.d(TAG, "onBindViewHolder: ${mediaFile.uri.path}")
            // Load mediaFile thumbnail into videoThumbnail view
            mediaFile.thumbnail?.let {
                holder.videoThumbnail?.setImageBitmap(it)
            }
            mediaFile.title.let {
                holder.title?.text = it
            }
            // Set other views
            val isSelecting = getSelectionCount() > 0
            holder.card.isChecked = isSelecting && mediaFile.isSelected

            Log.d(
                TAG,
                "onBindViewHolder: isSelecting: $isSelecting, card.isChecked: ${holder.card.isChecked}"
            )

            // Bind Item click listener
            holder.bind(mediaFile, isSelecting, onItemClickListener)
        }

    }

    internal fun notifySafely(delay: Int = NOTIFY_SLOW, notify: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(150)
            notify()
        }
    }

    data class MediaFile(
        val uri: Uri,
        val title: String,
        var isSelected: Boolean,
        val thumbnail: Bitmap?,
    )

}


