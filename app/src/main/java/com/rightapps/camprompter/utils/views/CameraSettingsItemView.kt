package com.rightapps.camprompter.utils.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.R
import com.google.android.material.button.MaterialButton
import com.rightapps.camprompter.databinding.CamSettingsItemBinding
import com.rightapps.camprompter.utils.PrefUtils

class CameraSettingsItemView<E> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    val title: String,
    private val key: String,
    private val options: Array<E>?,
    private val onSettingChanged: SettingOptionChangedListener?
) : LinearLayoutCompat(context, attrs, defStyle) where E : Enum<E>, E : PrefUtils.SettingOption<E> {
    companion object {
        const val TAG = "CameraSettingsItemView"
    }

    var binding: CamSettingsItemBinding

    init {
        binding = CamSettingsItemBinding.inflate(LayoutInflater.from(context))
    }

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        0,
        "",
        "",
        null,
        null
    )

    init {
        binding.title.text = title
        val selected = PrefUtils.getSelected(context, key, options?.get(0)?.ordinal ?: 0)
        binding.state.text = options?.find { it.ordinal == selected }?.text ?: ""
        binding.settingsBtnGrp.removeAllViews()
        val tmpBtns = mutableSetOf<MaterialButton>()
        options?.forEach { option ->
            val button = MaterialButton(
                context,
                null,
                R.attr.materialIconButtonFilledStyle
            ).apply {
                icon = AppCompatResources.getDrawable(context, option.resource)
                tag = option.ordinal
                isCheckable = true
                isChecked = option.ordinal == selected
            }

            button.addOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    tmpBtns.forEach { it.isChecked = false }
                    button.isChecked = true
                    binding.state.text = options.find { it.ordinal == button.tag }?.text ?: ""
                    PrefUtils.setSelected(context, key, button.tag as Int)
                    onSettingChanged?.onSettingOptionChanged(key)
                }
            }
            tmpBtns.add(button)

            binding.settingsBtnGrp.addView(button)
        }

    }

    fun getRoot(): View = binding.root

    interface SettingOptionChangedListener {
        fun onSettingOptionChanged(key: String)
    }
}