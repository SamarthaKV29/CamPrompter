package com.rightapps.camprompter

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : Activity() {
    companion object {
        const val TAG: String = "MainActivity"
    }

    lateinit var splashLyt: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.d(TAG, "onCreate: ")
        setContentView(R.layout.activity_main)
        loadUIComponents()
    }

    private fun loadUIComponents() {
        splashLyt = findViewById(R.id.splashLyt)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        CoroutineScope(Dispatchers.IO).launch {
            delay(5000)
            runOnUiThread {
                splashLyt.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }


}