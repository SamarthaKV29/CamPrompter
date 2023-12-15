package com.rightapps.camprompter.utils.audio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.common.collect.EvictingQueue
import com.rightapps.camprompter.R
import java.util.concurrent.atomic.AtomicBoolean


class AmplitudeWaveFormView : View {
    companion object {
        private const val TAG = "AmplitudeWaveFormView"
        private const val MAX_AMPLITUDE = 32767
        private const val SAMPLES_PER_SCREEN = 100

        private fun getPaint(context: Context, colorResourceId: Int) =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                this.strokeWidth = 5F
                color = context.getColor(colorResourceId)
            }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val recordingPaint: Paint = getPaint(context, R.color.md_red_500)
    private val notRecordingPaint: Paint = getPaint(context, R.color.md_amber_500)

    private var vWidth = -1
    private var vHeight = -1
    private var oldWidth = -1
    private var oldHeight = -1

    private var lx = 0F
    private var ly = 0F
    private var deltaX = 0F

    private var mCurrentSample = 0
    private var mAmplitude = 0
    private var amplitudeDivisor = 1F
    private val mPointQueue: EvictingQueue<Float> = EvictingQueue.create(SAMPLES_PER_SCREEN * 4)
    private val lastPoints = mutableListOf<Float>()

    private val mIsStarted = AtomicBoolean(false)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        vHeight = h
        vWidth = w
        ly = h.toFloat()
        lx = w.toFloat()
        deltaX = vWidth.toFloat() / SAMPLES_PER_SCREEN
        amplitudeDivisor = MAX_AMPLITUDE.toFloat() / vHeight

        if (lastPoints.isNotEmpty()) {
            val xScale = vWidth.toFloat() / oldWidth
            val yScale = vHeight.toFloat() / oldHeight
            Matrix().let {
                it.setScale(xScale, yScale)
                it.mapPoints(lastPoints.toFloatArray())
                mPointQueue.addAll(lastPoints)
                ly = lastPoints.last()
                lx = lastPoints[lastPoints.size - 2]
                lastPoints.clear()
            }

        }
    }

    fun updateAmplitude(amplitude: Int) {
        mAmplitude = amplitude
        postInvalidate()
    }

    override fun onSaveInstanceState(): Parcelable = Bundle().apply {
        putFloatArray("lines", mPointQueue.toFloatArray())
        putInt("sample", mCurrentSample)
        putParcelable("parent", super.onSaveInstanceState())
        putInt("oldWidth", vWidth)
        putInt("oldHeight", vHeight)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        bundle.apply {
            mCurrentSample = getInt("sample")
            lastPoints.apply {
                clear()
                getFloatArray("lines")?.let { lines ->
                    lastPoints.addAll(lines.toMutableList())
                }
            }
            oldWidth = getInt("oldWidth")
            oldHeight = getInt("oldHeight")
        }

        super.onRestoreInstanceState(state)
    }

    fun start() {
        Log.d(TAG, "start: ")
        mIsStarted.set(true)
    }

    fun stop() {
        Log.d(TAG, "stop: ")
        mIsStarted.set(false)
    }

    override fun onDraw(canvas: Canvas?) {
        if (mIsStarted.get()) {
            val x = lx + deltaX
            val y = vHeight - (mAmplitude / amplitudeDivisor)
            mPointQueue.addAll(
                listOf(
                    lx, ly, x, y
                )
            )
            lastPoints.apply {
                clear()
                addAll(mPointQueue.toMutableList())
            }
            lx = x
            ly = y
        }
        if (lastPoints.isNotEmpty()) {
            val len =
                if (mPointQueue.size / 4 >= SAMPLES_PER_SCREEN) SAMPLES_PER_SCREEN * 4 else mPointQueue.size
            val translateX = vWidth - lastPoints[lastPoints.size - 2]
            canvas?.translate(translateX, 0F)
            canvas?.drawLines(lastPoints.toFloatArray(), 0, len, recordingPaint)
        }

        if (mCurrentSample <= SAMPLES_PER_SCREEN) drawNotRecordingLine(canvas)
        mCurrentSample++
    }

    private fun drawNotRecordingLine(canvas: Canvas?) {
        canvas?.drawLine(
            0F,
            vHeight.toFloat(),
            vWidth.toFloat(),
            vHeight.toFloat(),
            notRecordingPaint
        )
    }
}