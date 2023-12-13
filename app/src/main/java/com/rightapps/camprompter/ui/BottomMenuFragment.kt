package com.rightapps.camprompter.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rightapps.camprompter.R
import com.rightapps.camprompter.databinding.FragmentBottomMenuBinding
import com.rightapps.camprompter.ui.gallery.GalleryActivity
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.Utility

class BottomMenuFragment : Fragment(R.layout.fragment_bottom_menu) {
    companion object {
        const val TAG = "BottomMenuFragment"
    }

    private var _binding: FragmentBottomMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedGlue: UISharedGlue by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.captureBtn.setOnClickListener {
            sharedGlue.isRecordingVideo.value = !(sharedGlue.isRecordingVideo.value ?: false)
        }

        binding.captureAudioBtn.setOnClickListener {
            sharedGlue.isRecordingAudio.value = !(sharedGlue.isRecordingAudio.value ?: false)
        }

        binding.previewBtn.setOnClickListener {
            Utility.showGallery(requireContext())
        }

        binding.settingsBtn.setOnClickListener {
            Utility.showSettings(requireContext())
        }

        sharedGlue.isRecordingVideo.observe(viewLifecycleOwner) { isRecording ->
            binding.captureBtn.setupRecordingBtn(isRecording, R.drawable.capture)
            binding.previewBtn.isEnabled = !isRecording
        }
        sharedGlue.isRecordingAudio.observe(viewLifecycleOwner) { isRecordingAudio ->
            binding.captureAudioBtn.setupRecordingBtn(isRecordingAudio, R.drawable.capture_audio)
            binding.previewBtn.isEnabled = !isRecordingAudio
        }
    }

    private fun AppCompatImageButton.setupRecordingBtn(isActive: Boolean, icon: Int) =
        setBackgroundResource(
            if (isActive) R.drawable.stop_capture else icon
        )
}