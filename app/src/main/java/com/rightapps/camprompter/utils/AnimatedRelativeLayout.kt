package com.rightapps.camprompter.utils

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.core.graphics.minus
import androidx.core.view.isVisible
import com.rightapps.camprompter.R
import kotlin.math.abs


class AnimatingRelativeLayout : RelativeLayout {
    companion object {
        private const val TAG: String = "AnimatingRelativeLayout"
    }

    private var inAnimation: Animation? = null
    private var outAnimation: Animation? = null

    private var oldTouch = Point(0, 0)
    private var initialH = 0
    private var initialY = 0f
    private var isAnimating = false

    constructor(context: Context) : super(context) {
        initAnimations()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAnimations()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initAnimations()
    }

    private fun initAnimations() {
        inAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in)
        outAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    fun show() {
        if (isVisible) return
        show(true)
    }

    private fun show(withAnimation: Boolean) {
        if (withAnimation) startAnimation(inAnimation)
        isVisible = true
    }

    fun hide() {
        if (!isVisible) return
        hide(true)
    }

    private fun hide(withAnimation: Boolean) {
        if (withAnimation) startAnimation(outAnimation)
        isVisible = false
    }


    fun overrideDefaultInAnimation(inAnimation: Animation?) {
        this.inAnimation = inAnimation
    }

    fun overrideDefaultOutAnimation(outAnimation: Animation?) {
        this.outAnimation = outAnimation
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val newTouch = Point(event.x.toInt(), event.y.toInt())
        return when (event.action) {
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "onTouchEvent: Action: UP, old: $oldTouch, new: $newTouch ")
                oldTouch = Point(0, 0)
                isAnimating = true
                // Bounce back animation
                val bounceBackAnimator =
                    ObjectAnimator.ofFloat(this, "translationY", 0f)
                bounceBackAnimator.duration = 300
                bounceBackAnimator.addUpdateListener {
                    // Reset initialY after the animation is complete
                    if (it.animatedFraction >= 1.0f) {
                        isAnimating = false
                        initialY = this.y
                    }
                }
                bounceBackAnimator.start()
                performClick()
                true
            }

            MotionEvent.ACTION_MOVE -> {
                val delta = newTouch.minus(oldTouch)
                Log.d(
                    TAG,
                    "onTouchEvent: Action: MOVE, old: $oldTouch, new: $newTouch, delta: $delta"
                )
                if (delta.y in (initialH / 3)..(initialH / 2)) {
                    translationY = delta.y.toFloat()
                }

                true
            }

            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "onTouchEvent: Action: DOWN, old: $oldTouch, new: $newTouch ")
                oldTouch.set(event.x.toInt(), event.y.toInt())
                initialH = layoutParams.height
                initialY = this.y
                true
            }

            else -> false
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}