package com.rightapps.camprompter.ui.gallery

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.rightapps.camprompter.R
import com.rightapps.camprompter.utils.EmptyRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GalleryGridAdapter(
    videos: List<Video>,
    private val isSelecting: Boolean,
    private val selectedItems: MutableLiveData<List<Video>?>,
    private val onItemClickListener: EmptyRecyclerView.OnItemClickListener? = null
) :
    RecyclerView.Adapter<GalleryGridAdapter.ViewHolder>() {

    companion object {
        const val TAG: String = "GalleryGridAdapter"
    }

    private val galleryGridAdapter = this
    val videosData = videos.toMutableList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoThumbnail: ImageView = itemView.findViewById(R.id.videoThumbnail)
        val videoIsSelected: ImageView = itemView.findViewById(R.id.isSelected)
        // Other views

        fun getContext(): Context = itemView.context

        fun bind(item: Video, listener: EmptyRecyclerView.OnItemClickListener? = null) {
            if (isSelecting) {
                itemView.setOnClickListener {
                    val idx = getVideoIndex(item)
                    if (idx >= 0) {
                        videosData[idx].isSelected =
                            videosData[idx].isSelected.not()
                        selectedItems.postValue(videosData.filter { it.isSelected })
                        notifySafely(idx)
                    }
                }
            } else {
                videosData.filter { it.isSelected }.forEachIndexed { idx, video ->
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

    fun getVideoIndex(item: Video) = videosData.indexOf(item)

    fun notifySafely(idx: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(150)
            notifyItemChanged(idx)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.gallery_grid_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Log.d(TAG, "onBindViewHolder: ${video.uri.path}")
        videosData[position].let { video ->
            // Load video thumbnail into videoThumbnail view
            video.thumbnail?.let {
                holder.videoThumbnail.setImageBitmap(it)
            }
            // Set other views
            if (isSelecting) {
                holder.videoIsSelected.visibility = View.VISIBLE
                holder.videoIsSelected.setImageDrawable(
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
            } else holder.videoIsSelected.visibility = View.GONE

            // Bind Item click listener
            holder.bind(video, onItemClickListener)
        }
    }

    override fun getItemCount() = videosData.size


    data class Video(
        val uri: Uri,
        val title: String,
        var isSelected: Boolean,
        val thumbnail: Bitmap?,
    )

}


