package com.rightapps.camprompter.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UISharedGlue : ViewModel() {
    val isRecording = MutableLiveData<Boolean>()
}