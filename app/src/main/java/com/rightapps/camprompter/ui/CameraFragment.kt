package com.rightapps.camprompter.ui


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.otaliastudios.cameraview.CameraView
import com.rightapps.camprompter.R

class CameraFragment : Fragment(R.layout.camera_fragment) {
    companion object {
        const val TAG: String = "CameraFragment"
    }

    lateinit var mainCameraView: CameraView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainCameraView = view.findViewById(R.id.mainCameraView)
        mainCameraView.setLifecycleOwner(viewLifecycleOwner)
    }


}