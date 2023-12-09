package com.rightapps.camprompter.utils

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object ViewUtils {


    fun View.show() {
        alpha = 0f
        animate().alpha(1f).duration = 500
    }

    fun View.gone() {
        alpha = 1f
        animate().alpha(0f).duration = 500
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