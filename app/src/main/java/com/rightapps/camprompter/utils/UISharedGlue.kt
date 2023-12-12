package com.rightapps.camprompter.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rightapps.camprompter.ui.gallery.GalleryActivity

class UISharedGlue : ViewModel() {
    val isRecordingVideo = MutableLiveData<Boolean>()
    val galleryFragmentType = MutableLiveData<GalleryActivity.Companion.GalleryFragmentType>()
    val isSelectingGalleryItems = MutableLiveData<Boolean>()
    val showDeleteButton = MutableLiveData<Boolean>()
    val isRecordingAudio = MutableLiveData<Boolean>()
    val galleryViewType = MutableLiveData<FileUtils.FileType>()


}