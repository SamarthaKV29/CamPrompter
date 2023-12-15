package com.rightapps.camprompter.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rightapps.camprompter.R
import com.rightapps.camprompter.databinding.FragmentCameraSettingsBinding
import com.rightapps.camprompter.utils.PrefUtils
import com.rightapps.camprompter.utils.views.UISharedGlue
import com.rightapps.camprompter.utils.audio.MicManager
import com.rightapps.camprompter.utils.views.CameraSettingsItemView

class CameraSettingsFragment : Fragment() {
    companion object {
        private const val TAG = "CameraSettingsFragment"
    }

    val sharedGlue: UISharedGlue by activityViewModels()
    private var _binding: FragmentCameraSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.parentLL.addView(
            CameraSettingsItemView(
                requireContext(),
                title = getString(R.string.settings_title_resolution),
                key = PrefUtils.VideoResolution.key,
                options = PrefUtils.VideoResolution.values(),
                onSettingChanged = handleSettingChanged
            ).getRoot()
        )
        val mics = MicManager.getAvailable(requireContext()).toTypedArray()
        if (mics.size == 1) {
            PrefUtils.setSelected(requireContext(), PrefUtils.InputType.key, mics[0].ordinal)
        }
        binding.parentLL.addView(
            CameraSettingsItemView(
                requireContext(),
                title = getString(R.string.settings_title_microphone),
                key = PrefUtils.InputType.key,
                options = mics,
                onSettingChanged = handleSettingChanged
            ).getRoot()
        )
    }

    private val handleSettingChanged =
        object : CameraSettingsItemView.SettingOptionChangedListener {
            override fun onSettingOptionChanged(key: String) {
                sharedGlue.settingOptionChanged.postValue(key)
            }
        }
}