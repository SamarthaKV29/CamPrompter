package com.rightapps.camprompter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.activityViewModels
import com.rightapps.camprompter.R
import com.rightapps.camprompter.databinding.FragmentBottomMenuBinding
import com.rightapps.camprompter.utils.Utility
import com.rightapps.camprompter.utils.views.BoundFragment
import com.rightapps.camprompter.utils.views.UISharedGlue

class BottomMenuFragment : BoundFragment<FragmentBottomMenuBinding>() {
    companion object {
        const val TAG = "BottomMenuFragment"
    }

    private val sharedGlue: UISharedGlue by activityViewModels()

    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBottomMenuBinding {
        return FragmentBottomMenuBinding.inflate(inflater, container, false)
    }

    override fun onViewCreate() {
        binding.captureBtn.setOnClickListener {
            val isVideo = binding.toggleAudioVideo.isChecked
            if (isVideo) sharedGlue.isRecordingVideo.postValue(
                !(sharedGlue.isRecordingVideo.value ?: false)
            )
            else sharedGlue.isRecordingAudio.postValue(
                !(sharedGlue.isRecordingAudio.value ?: false)
            )
        }

        binding.toggleAudioVideo.setOnCheckedChangeListener { _, isChecked ->
            binding.captureBtn.setupRecordingBtn(
                false,
                if (isChecked) R.drawable.capture else R.drawable.capture_audio
            )
            sharedGlue.avSwitch.postValue(isChecked)
        }

        binding.previewBtn.setOnClickListener {
            Utility.showGallery(requireContext())
        }

        binding.settingsBtn.setOnClickListener {
            (requireActivity() as MainActivity).showBottomDrawer()
        }

        sharedGlue.isRecordingVideo.observe(viewLifecycleOwner) { isRecording ->
            binding.captureBtn.setupRecordingBtn(isRecording, R.drawable.capture)
            binding.previewBtn.isEnabled = !isRecording
            binding.settingsBtn.isEnabled = !isRecording
        }
        sharedGlue.isRecordingAudio.observe(viewLifecycleOwner) { isRecording ->
            binding.captureBtn.setupRecordingBtn(isRecording, R.drawable.capture_audio)
            binding.previewBtn.isEnabled = !isRecording
            binding.settingsBtn.isEnabled = !isRecording
        }
    }

    private fun AppCompatImageButton.setupRecordingBtn(isActive: Boolean, icon: Int) =
        setBackgroundResource(
            if (isActive) R.drawable.stop_capture else icon
        )
}