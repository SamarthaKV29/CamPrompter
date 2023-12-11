package com.rightapps.camprompter.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rightapps.camprompter.R
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.Utility

class BottomMenuFragment : Fragment(R.layout.fragment_bottom_menu) {
    companion object {
        const val TAG = "BottomMenuFragment"
    }

    private lateinit var captureBtn: AppCompatImageButton
    private lateinit var captureAudioBtn: AppCompatImageButton
    private lateinit var previewBtn: AppCompatImageButton

    private val sharedGlue: UISharedGlue by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        captureBtn = view.findViewById(R.id.captureBtn)
        captureAudioBtn = view.findViewById(R.id.captureAudioBtn)
        previewBtn = view.findViewById(R.id.previewBtn)

        captureBtn.setOnClickListener {
            sharedGlue.isRecordingVideo.value = !(sharedGlue.isRecordingVideo.value ?: false)
        }

        captureAudioBtn.setOnClickListener {
            sharedGlue.isRecordingAudio.value = !(sharedGlue.isRecordingAudio.value ?: false)

        }

        previewBtn.setOnClickListener {
            Utility.showPreview(requireContext())
        }

        sharedGlue.isRecordingVideo.observe(viewLifecycleOwner) { isRecording ->
            captureBtn.setupRecordingBtn(isRecording, R.drawable.capture)
            previewBtn.isEnabled = !isRecording
        }
        sharedGlue.isRecordingAudio.observe(viewLifecycleOwner) { isRecordingAudio ->
            captureAudioBtn.setupRecordingBtn(isRecordingAudio, R.drawable.capture_audio)
            previewBtn.isEnabled = !isRecordingAudio
        }
    }

    private fun AppCompatImageButton.setupRecordingBtn(isActive: Boolean, icon: Int) =
        setBackgroundResource(
            if (isActive) R.drawable.stop_capture else icon
        )
}