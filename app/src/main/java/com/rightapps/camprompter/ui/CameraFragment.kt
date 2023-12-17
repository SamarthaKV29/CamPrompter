package com.rightapps.camprompter.ui


import android.content.Context
import android.media.MediaScannerConnection
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.otaliastudios.cameraview.size.AspectRatio
import com.otaliastudios.cameraview.size.SizeSelector
import com.otaliastudios.cameraview.size.SizeSelectors
import com.rightapps.camprompter.databinding.FragmentCameraViewBinding
import com.rightapps.camprompter.utils.FileUtils
import com.rightapps.camprompter.utils.PrefUtils
import com.rightapps.camprompter.utils.views.BoundFragment
import com.rightapps.camprompter.utils.views.UISharedGlue
import com.rightapps.camprompter.utils.views.ViewUtils.blink
import com.rightapps.camprompter.utils.views.ViewUtils.hide
import com.rightapps.camprompter.utils.views.ViewUtils.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CameraFragment : BoundFragment<FragmentCameraViewBinding>() {
    companion object {
        const val TAG: String = "CameraFragment"
    }

    private val sharedGlue: UISharedGlue by activityViewModels()

    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCameraViewBinding {
        return FragmentCameraViewBinding.inflate(inflater, container, false)
    }

    override fun onViewCreate() {
//        sharedGlue.settingOptionChanged.observe(viewLifecycleOwner) { key ->
//            if (key == PrefUtils.VideoResolution.key) {
//
//            }
//        }
        binding.recordingIndicator.hide()
        binding.mainCameraView.apply {
            setLifecycleOwner(viewLifecycleOwner)
            mapGesture(Gesture.PINCH, GestureAction.ZOOM)
            mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)

            val previewSizes = getPreviewSizes(requireContext())
            setPreviewStreamSize(previewSizes)

            Log.d(TAG, "onViewCreated: is 4K pref: ${PrefUtils.is4KPreferred(context)}")
//            val pictureSizes = getPictureSizes(requireContext())
            setPictureSize { videoSizes ->
                Log.d(TAG, "onViewCreated: $videoSizes")
                videoSizes.filter { it.height == 2560 }
            }
            setVideoSize { videoSizes ->
                videoSizes.filter { it.height == 2560 }
            }

            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                Log.d(TAG, "onViewCreated: Video Size: ${binding.mainCameraView.videoSize}")
            }

            addCameraListener(object : CameraListener() {
                private var oldJob: Job? = null

                override fun onVideoTaken(result: VideoResult) {
                    super.onVideoTaken(result)
                    Snackbar.make(binding.root, "Video saved successfully!", Snackbar.LENGTH_SHORT)
                        .show()
                }

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
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getPreviewSizes(context: Context): SizeSelector = SizeSelectors.or(
        SizeSelectors.and(
            SizeSelectors.or(
                SizeSelectors.aspectRatio(AspectRatio.of(1, 1), 100F),
                SizeSelectors.aspectRatio(AspectRatio.of(4, 5), 100F),
                SizeSelectors.aspectRatio(AspectRatio.of(9, 16), 100F),
            ),
            SizeSelectors.minHeight(720)
        ),
        SizeSelectors.minHeight(720),
        if (PrefUtils.is4KPreferred(context)) SizeSelectors.biggest() else SizeSelectors.minHeight(
            1080
        )
    )

    private fun getPictureSizes(context: Context): SizeSelector = SizeSelectors.or(
        SizeSelectors.and(
            SizeSelectors.or(
                SizeSelectors.aspectRatio(AspectRatio.of(1, 1), 100F),
                SizeSelectors.aspectRatio(AspectRatio.of(4, 5), 100F),
                SizeSelectors.aspectRatio(AspectRatio.of(9, 16), 100F),
            ),
            if (PrefUtils.is4KPreferred(context))
                SizeSelectors.biggest()
            else SizeSelectors.or(
                SizeSelectors.minHeight(1920),
                SizeSelectors.minHeight(1080),
            )
        ),
        if (PrefUtils.is4KPreferred(context)) SizeSelectors.biggest()
        else SizeSelectors.or(
            SizeSelectors.minHeight(1920),
            SizeSelectors.minHeight(1080),
        )
    )


}