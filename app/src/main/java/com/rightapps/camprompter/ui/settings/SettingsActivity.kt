package com.rightapps.camprompter.ui.settings

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.rightapps.camprompter.R
import com.rightapps.camprompter.databinding.ActivitySettingsScreenBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            replace(binding.settingsScreenFrame.id, SettingsFragment())
        }
    }
}