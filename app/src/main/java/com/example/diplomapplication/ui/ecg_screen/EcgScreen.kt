package com.example.diplomapplication.ui.ecg_screen

import android.hardware.usb.*
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import com.example.diplomapplication.R
import kotlin.math.roundToInt

@Composable
fun EcgScreen(
    usbManager: UsbManager?,
    device: UsbDevice?,
    finish: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var ecgData by remember { mutableStateOf(listOf<Int>()) }
    var allEcgData by remember { mutableStateOf(listOf<Int>()) }
    var isMeasuring by remember { mutableStateOf(false) }
    var heartRate by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Button(onClick = {
            isMeasuring = true
            ecgData = emptyList()

            CoroutineScope(Dispatchers.IO).launch {
                val connection = usbManager?.openDevice(device)
                val usbInterface = device?.getInterface(0)
                connection?.claimInterface(usbInterface, true)

                val endpointIn = (0 until (usbInterface?.endpointCount ?: 0))
                    .map { usbInterface?.getEndpoint(it) }
                    .firstOrNull { it?.direction == UsbConstants.USB_DIR_IN }

                if (endpointIn != null && connection != null) {
                    val buffer = ByteArray(endpointIn.maxPacketSize)
                    val allCollected = mutableListOf<Int>()
                    var currentCollected = mutableListOf<Int>()
                    var startTime = System.currentTimeMillis()

                    while (System.currentTimeMillis() - startTime < 5_000) {
                        val bytesRead = connection.bulkTransfer(endpointIn, buffer, buffer.size, 1000)
                        if (bytesRead > 0) {
                            val copy = buffer.copyOfRange(0, bytesRead)
                            allCollected.addAll(applyMovingAverageFilter(parseEcgData(copy), 5))
                            currentCollected.addAll(applyMovingAverageFilter(parseEcgData(copy), 5))
                        }
                    }
                    withContext(Dispatchers.Main) {
                        ecgData = currentCollected
                    }

                    startTime = System.currentTimeMillis()
                    currentCollected = mutableListOf<Int>()
                    while (System.currentTimeMillis() - startTime < 5_000) {
                        val bytesRead = connection.bulkTransfer(endpointIn, buffer, buffer.size, 1000)
                        if (bytesRead > 0) {
                            val copy = buffer.copyOfRange(0, bytesRead)
                            allCollected.addAll(applyMovingAverageFilter(parseEcgData(copy), 5))
                            currentCollected.addAll(applyMovingAverageFilter(parseEcgData(copy), 5))
                        }
                    }
                    withContext(Dispatchers.Main) {
                        ecgData = currentCollected
                    }

                    startTime = System.currentTimeMillis()
                    currentCollected = mutableListOf<Int>()
                    while (System.currentTimeMillis() - startTime < 5_000) {
                        val bytesRead = connection.bulkTransfer(endpointIn, buffer, buffer.size, 1000)
                        if (bytesRead > 0) {
                            val copy = buffer.copyOfRange(0, bytesRead)
                            allCollected.addAll(applyMovingAverageFilter(parseEcgData(copy), 5))
                            currentCollected.addAll(applyMovingAverageFilter(parseEcgData(copy), 5))
                        }
                    }
                    withContext(Dispatchers.Main) {
                        ecgData = currentCollected
                        isMeasuring = false
                        allEcgData = allCollected
                    }
                }
            }
        }) {
            Text(if (isMeasuring) "Измерение..." else "Измерить ЭКГ")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (ecgData.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            EcgGraph(data = ecgData.takeLast(500))
        }
        if (allEcgData.isNotEmpty()) {
            val heartRate =
                //allEcgData.size / (15_000 / 1000.0)
                estimateHeartRate(allEcgData)
            Text(
                text = stringResource(R.string.heart_rate_ready, heartRate)
            )
            Button(
                onClick = {finish.invoke(heartRate)},
            ) {
                Text(text = stringResource(R.string.finish))
            }
        }
    }
}

@Composable
fun EcgGraph(data: List<Int>, modifier: Modifier = Modifier) {
    val graphColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(200.dp)) {
        if (data.isEmpty()) return@Canvas

        val maxVal = data.maxOrNull() ?: 1
        val minVal = data.minOrNull() ?: 0
        val range = (maxVal - minVal).takeIf { it > 0 } ?: 1

        val xStep = size.width / (data.size - 1)
        val yRatio = size.height / range.toFloat()

        for (i in 1 until data.size) {
            val x1 = (i - 1) * xStep
            val y1 = size.height - (data[i - 1] - minVal) * yRatio
            val x2 = i * xStep
            val y2 = size.height - (data[i] - minVal) * yRatio
            drawLine(graphColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 2f)
        }
    }
}

fun parseEcgData(buffer: ByteArray): List<Int> {
    val result = mutableListOf<Int>()
    var i = 0
    while (i <= buffer.size - 6) {
        if (buffer[i] == (-1).toByte() && buffer[i + 1] == 4.toByte() && buffer[i + 2] == 0.toByte()) {
            val low = buffer[i + 4].toInt() and 0xFF
            val high = buffer[i + 5].toInt() and 0xFF
            val value = (high shl 8) or low
            result.add(value)
            i += 6
        } else {
            i++
        }
    }
    return result
}

fun applyMovingAverageFilter(data: List<Int>, windowSize: Int): List<Int> {
    if (data.size < windowSize) return data
    val result = mutableListOf<Int>()
    for (i in data.indices) {
        val start = maxOf(0, i - windowSize / 2)
        val end = minOf(data.size - 1, i + windowSize / 2)
        val window = data.subList(start, end + 1)
        result.add(window.average().toInt())
    }
    return result
}

fun estimateHeartRate(data: List<Int>, samplingRateHz: Int = 500): Int {
    if (data.size < 2) return 0

    val threshold = (data.maxOrNull() ?: 0) * 0.85
    val rPeaks = mutableListOf<Int>()

    for (i in 1 until data.size - 1) {
        if (data[i] > threshold && data[i] > data[i - 1] && data[i] > data[i + 1]) {
            rPeaks.add(i)
        }
    }

    if (rPeaks.size < 2) return 0

    val rrIntervals = rPeaks.zipWithNext { a, b -> b - a }
    val avgRrSamples = rrIntervals.average()
    val avgRrMillis = avgRrSamples * (1000.0 / samplingRateHz)

    return (15000 / avgRrMillis).toInt()
}
