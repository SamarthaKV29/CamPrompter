package com.rightapps.camprompter.ui

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.rightapps.camprompter.R

class VideoAdapter(private val videos: List<Video>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    companion object {
        const val TAG: String = "VideoAdapter"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoThumbnail: ImageView = itemView.findViewById(R.id.videoThumbnail)
        // Other views
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.gallery_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        video.thumbnail?.let {
            holder.videoThumbnail.setImageBitmap(it)
        }
//        Log.d(TAG, "onBindViewHolder: ${video.uri.path}")
        // Load video thumbnail into videoThumbnail view
        // Set other views
    }

    override fun getItemCount() = videos.size

    data class Video(val uri: Uri, val title: String, val thumbnail: Bitmap?)

}


