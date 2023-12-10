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

    }

    lateinit var captureBtn: AppCompatImageButton
    lateinit var previewBtn: AppCompatImageButton
    val sharedGlue: UISharedGlue by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        captureBtn = view.findViewById(R.id.captureBtn)
        previewBtn = view.findViewById(R.id.previewBtn)

        sharedGlue.isRecording.observe(viewLifecycleOwner) { isRecording ->
            captureBtn.setBackgroundResource(
                if (isRecording) R.drawable.stop_capture else R.drawable.capture
            )
        }
        captureBtn.setOnClickListener {
            sharedGlue.isRecording.value = !(sharedGlue.isRecording.value ?: false)
        }
        previewBtn.setOnClickListener {
            Utility.showPreview(requireContext())
        }
    }
}