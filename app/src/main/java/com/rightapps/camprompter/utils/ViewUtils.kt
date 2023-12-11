package com.rightapps.camprompter.utils

import android.util.Log
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object ViewUtils {
    const val TAG = "ViewUtils"

    fun View.show(duration: Long = 500) {
        Log.d(TAG, "show: $tag")
        visibility = View.VISIBLE
        alpha = 0f
        animate().alpha(1f).duration = duration
    }

    fun View.hide(duration: Long = 500) {
        Log.d(TAG, "hide: $tag")
        alpha = 1f
        animate().alpha(0f).duration = duration
        visibility = View.GONE
    }

    fun View.blink(active: Boolean, delay: Long = 1000L, job: Job? = null): Job =
        CoroutineScope(Dispatchers.Main).launch {
            job?.cancel()
            while (active && isActive) {
                alpha = 0f
                animate().alpha(1f).duration = delay / 2
                delay(delay)
            }
        }
}