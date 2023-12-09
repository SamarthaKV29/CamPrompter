package com.rightapps.camprompter.ui

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rightapps.camprompter.R
import java.io.File

class GalleryActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "GalleryActivity"
    }

    private lateinit var galleryRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        setContentView(R.layout.activity_gallery)
//        findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
//            setSupportActionBar(toolbar)
//            supportActionBar?.apply {
//                setDisplayHomeAsUpEnabled(true)
//                setDisplayShowHomeEnabled(true)
//            }
//            toolbar.setNavigationOnClickListener {
//                onBackPressed()
//            }
//        }
        galleryRV = findViewById(R.id.galleryRV)
    }

    override fun onStart() {
        super.onStart()
        val appFolder = File(
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path}/${
                getString(R.string.app_name)
            }"
        )
        Log.d(TAG, "onStart: appFolder: ${appFolder.path}")
        MediaScannerConnection.scanFile(
            applicationContext,
            arrayOf(
                appFolder.toString()
            ),
            null, null
        );
        val videos = fetchVideos(applicationContext)
        val gridLayoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        galleryRV.layoutManager = gridLayoutManager
        galleryRV.adapter = VideoAdapter(videos)
    }

    private fun fetchVideos(context: Context): List<VideoAdapter.Video> {
        Log.d(TAG, "fetchVideos: ")
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

        Log.d(TAG, "fetchVideos: ${videos.size}")

        return videos
    }

    private fun getVideoThumbnail(videoPath: String?): Bitmap? = try {
        Log.d(TAG, "getVideoThumbnail: $videoPath")
        videoPath?.let {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(it)
            val frame = retriever.frameAtTime
            retriever.release()
            frame
        }
    } catch (e: Exception) {
        Log.w(TAG, "getVideoThumbnail: Failed to get thumb: ${e.localizedMessage}")
        null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(this, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            )
        )
        finish()
    }

}