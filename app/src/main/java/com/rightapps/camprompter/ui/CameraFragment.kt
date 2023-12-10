package com.rightapps.camprompter.ui


import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.rightapps.camprompter.R
import com.rightapps.camprompter.utils.FileUtils
import com.rightapps.camprompter.utils.UISharedGlue
import com.rightapps.camprompter.utils.ViewUtils.blink
import com.rightapps.camprompter.utils.ViewUtils.gone
import com.rightapps.camprompter.utils.ViewUtils.show
import kotlinx.coroutines.Job


class CameraFragment : Fragment(R.layout.fragment_camera_view) {
    companion object {
        const val TAG: String = "CameraFragment"
    }

    lateinit var mainCameraView: CameraView
    lateinit var recordingIcon: ImageView
    val sharedGlue: UISharedGlue by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainCameraView = view.findViewById(R.id.mainCameraView)
        recordingIcon = view.findViewById(R.id.recordingIndicator)

        recordingIcon.gone()
        mainCameraView.apply {
            setLifecycleOwner(viewLifecycleOwner)
            mode = Mode.VIDEO
            mapGesture(Gesture.PINCH, GestureAction.ZOOM)
            mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)
            setPreviewStreamSize { sizes ->
                sizes.filter {
                    it.width >= 720
                }
            }
            setPictureSize { sizes ->
                // sizes.forEach { size -> Log.d(TAG, "onViewCreated: Size: $size") }
                sizes.filter {
                    it.width >= 1920
                }
            }
            addCameraListener(object : CameraListener() {
                private var oldJob: Job? = null

                override fun onVideoRecordingStart() {
                    super.onVideoRecordingStart()
                    recordingIcon.show()
                    oldJob = recordingIcon.blink(true)
                }

                override fun onVideoRecordingEnd() {
                    super.onVideoRecordingEnd()
                    recordingIcon.blink(false, job = oldJob)
                    recordingIcon.gone()
                }
            })
            sharedGlue.isRecording.observe(viewLifecycleOwner) { startRecording ->
                FileUtils.getOutputMediaFile(context)?.let { outputFile ->
                    if (startRecording) {
                        mainCameraView.takeVideo(outputFile)
                    } else {
                        if (mainCameraView.isTakingVideo) {
                            mainCameraView.stopVideo()
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