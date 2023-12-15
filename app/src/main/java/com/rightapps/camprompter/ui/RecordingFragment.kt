package com.rightapps.camprompter.ui

import android.media.MediaRecorder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.rightapps.camprompter.databinding.FragmentRecordingBinding
import com.rightapps.camprompter.utils.audio.MicManager
import com.rightapps.camprompter.utils.audio.MicManager.prepareSafely
import com.rightapps.camprompter.utils.audio.MicManager.releaseSafely
import com.rightapps.camprompter.utils.audio.MicManager.startSafely
import com.rightapps.camprompter.utils.audio.MicManager.stopSafely
import com.rightapps.camprompter.utils.views.BoundFragment
import com.rightapps.camprompter.utils.views.UISharedGlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecordingFragment : BoundFragment<FragmentRecordingBinding>() {
    companion object {
        const val TAG = "RecordingFragment"
    }

    private val sharedGlue: UISharedGlue by activityViewModels()
    private var recorder: MediaRecorder? = null

    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecordingBinding {
        return FragmentRecordingBinding.inflate(layoutInflater)
    }

    private var recodringJob: Job? = null
    override fun onViewCreate() {
        sharedGlue.isRecordingAudio.observe(this) { isRecordingAudio ->
            if (isRecordingAudio) {
                recorder = MicManager.getRecorder(requireContext()).apply {
                    prepareSafely(requireContext()) { _, what, _ ->
                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                            stopSafely()
                            sharedGlue.isRecordingAudio.value = false
                        }
                    }
                }
                recorder?.startSafely()
                binding.waveform.start()
                recodringJob = CoroutineScope(Dispatchers.Main).launch {
                    while (isRecordingAudio) {
                        delay(500)
                        try {
                            binding.waveform.updateAmplitude(recorder?.maxAmplitude ?: 0)
                        } catch (e: Exception) {
                            Log.e(TAG, "onViewCreate: ${e.localizedMessage}", e)
                        }
                    }
                    binding.waveform.stop()
                }
            } else {
                recodringJob?.cancel()
                recorder?.apply { stopSafely() }
                recorder?.releaseSafely()
                recorder = null
            }
        }
    }

}