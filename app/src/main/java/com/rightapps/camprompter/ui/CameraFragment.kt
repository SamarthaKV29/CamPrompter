package com.rightapps.camprompter.ui


import android.media.MediaScannerConnection
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.rightapps.camprompter.R
import com.rightapps.camprompter.databinding.FragmentCameraViewBinding
import com.rightapps.camprompter.utils.FileUtils
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.ViewUtils.blink
import com.rightapps.camprompter.utils.ViewUtils.hide
import com.rightapps.camprompter.utils.ViewUtils.show
import kotlinx.coroutines.Job


class CameraFragment : Fragment(R.layout.fragment_camera_view) {
    companion object {
        const val TAG: String = "CameraFragment"
    }

    private var _binding: FragmentCameraViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedGlue: UISharedGlue by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recordingIndicator.hide()
        binding.mainCameraView.apply {
            setLifecycleOwner(viewLifecycleOwner)
            mode = Mode.VIDEO
            mapGesture(Gesture.PINCH, GestureAction.ZOOM)
            mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)

            setPreviewStreamSize { sizes ->
                sizes.forEach { size -> Log.d(TAG, "onViewCreated: PreviewSize: $size") }
                sizes.filter {
                    it.width >= 720
                }
            }
            setPictureSize { sizes ->
                sizes.forEach { size -> Log.d(TAG, "onViewCreated: PictureSize: $size") }
                sizes.filter {
                    it.width >= 1920
                }
            }
            addCameraListener(object : CameraListener() {
                private var oldJob: Job? = null

                override fun onVideoRecordingStart() {
                    super.onVideoRecordingStart()
                    binding.recordingIndicator.show()
                    oldJob = binding.recordingIndicator.blink(true)
                }

                override fun onVideoRecordingEnd() {
                    super.onVideoRecordingEnd()
                    binding.recordingIndicator.blink(false, job = oldJob)
                    binding.recordingIndicator.hide()
                }
            })
            sharedGlue.isRecordingVideo.observe(viewLifecycleOwner) { startRecording ->
                FileUtils.getOutputFile(context, FileUtils.FileType.VIDEO_FILE)?.let { outputFile ->
                    if (startRecording) {
                        binding.mainCameraView.takeVideo(outputFile)
                    } else {
                        if (binding.mainCameraView.isTakingVideo) {
                            binding.mainCameraView.stopVideo()
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(outputFile.toString()),
                                null, null
                            );
                        }
                    }
                }
            }
        }
    }


}