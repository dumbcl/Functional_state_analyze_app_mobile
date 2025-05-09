package com.example.diplomapplication.ui.ppg_screen

import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.ArrayDeque
import kotlin.math.roundToInt

class HeartRateAnalyzer(
    private val onBpmReady: (Int) -> Unit,
) : ImageAnalysis.Analyzer {

    private val createdAt = SystemClock.elapsedRealtime()
    private val warmUpMs: Long = 4000

    private val measureSec = 5
    private val samplingRate = 30
    private val frameWindow = measureSec * samplingRate
    private val rawValues = ArrayDeque<Int>(frameWindow)
    private val expFilterAlpha = 0.3f

    private var lastFiltered = 0f
    private var peakCount = 0
    private var lastCrossUp = false
    private var startTimeMs = 0L

    override fun analyze(image: ImageProxy) {
        if (SystemClock.elapsedRealtime() - createdAt < warmUpMs) {
            image.close()
            return
        }

        val now = SystemClock.elapsedRealtime()

        val redAvg = ImageProcessing.decodeYUV420SPtoRedAvg(
            imageProxyToNV21(image),
            image.cropRect.width(),
            image.cropRect.height()
        )
        image.close()
        lastFiltered = if (lastFiltered == 0f) redAvg.toFloat()
        else lastFiltered + expFilterAlpha * (redAvg - lastFiltered)
        rawValues.addLast(lastFiltered.toInt())
        if (rawValues.size > frameWindow) rawValues.removeFirst()
        val mean = rawValues.average()
        val crossUp = lastFiltered > mean
        if (crossUp && !lastCrossUp) {
            peakCount++
        }
        lastCrossUp = crossUp
        if (startTimeMs == 0L) {
            startTimeMs = now
            image.close(); return
        }
        val elapsedSec = (now - startTimeMs) / 1_000f
        if (elapsedSec >= measureSec) {
            val bpm = (peakCount / elapsedSec * 60).toInt()
            when {
                bpm < 56 -> onBpmReady((bpm.toFloat() * 1.5f).roundToInt())
                else -> onBpmReady(bpm)
            }
            peakCount = 0
            lastCrossUp = false
            rawValues.clear()
            lastFiltered = 0f
            startTimeMs = now
        }
    }

    private var nv21Buffer: ByteArray? = null

    private fun imageProxyToNV21(image: ImageProxy): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val total = ySize + uSize + vSize

        if (nv21Buffer == null || nv21Buffer!!.size != total) {
            nv21Buffer = ByteArray(total)
        }
        val out = nv21Buffer!!

        yBuffer.get(out, 0, ySize)
        vBuffer.get(out, ySize, vSize)
        uBuffer.get(out, ySize + vSize, uSize)
        return out
    }
}
