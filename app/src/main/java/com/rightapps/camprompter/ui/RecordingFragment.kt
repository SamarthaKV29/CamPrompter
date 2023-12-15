package com.rightapps.camprompter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rightapps.camprompter.databinding.FragmentRecordingBinding
import com.rightapps.camprompter.utils.views.BoundFragment

class RecordingFragment : BoundFragment<FragmentRecordingBinding>() {
    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecordingBinding {
        return FragmentRecordingBinding.inflate(layoutInflater)
    }

    override fun onViewCreate() {

    }

}