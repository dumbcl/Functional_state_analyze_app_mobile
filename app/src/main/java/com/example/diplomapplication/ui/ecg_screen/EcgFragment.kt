package com.example.diplomapplication.ui.ecg_screen

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.ui.ppg_screen.PPGScreen
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.example.diplomapplication.util.HEART_RATE_BUNDLE
import com.example.diplomapplication.util.PPG_FRAGMENT_REQUEST_KEY

class EcgFragment : Fragment() {

    var usbManager: UsbManager? = null
    var device: UsbDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usbManager = activity?.getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice>? = usbManager?.deviceList
        device = deviceList?.values?.firstOrNull()

        if (device != null) {
            val permissionIntent = PendingIntent.getBroadcast(
                requireContext(), 0, Intent("com.example.diplomapplication.ui.ecg_screen.USB_PERMISSION"), PendingIntent.FLAG_IMMUTABLE
            )
            usbManager?.requestPermission(device, permissionIntent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    EcgScreen(
                        usbManager = usbManager,
                        device = device,
                        finish = { finish(it) },
                    )
                }
            }
        }
    }

    fun startGetData(usbManager: UsbManager, device: UsbDevice) {
        val connection = usbManager.openDevice(device)
        val usbInterface = device.getInterface(0)
        val con = connection?.claimInterface(usbInterface, true)

        val endpointIn = (0 until usbInterface.endpointCount)
            .map { usbInterface.getEndpoint(it) }
            .firstOrNull { it.direction == UsbConstants.USB_DIR_IN }

        if (endpointIn != null && connection != null) {
            val bufferSize = endpointIn.maxPacketSize
            val buffer = ByteArray(bufferSize)

            Thread {
                while (true) {
                    val bytesRead = connection.bulkTransfer(endpointIn, buffer, buffer.size, 1000)
                    if (bytesRead > 0) {
                        val receivedData = buffer.copyOfRange(0, bytesRead)
                        // Тут можно анализировать или передавать данные в UI
                        Log.d("ECG", "Получено ${receivedData.size} байт: ${receivedData.joinToString()}")
                    }
                }
            }.start()
        }
    }

    fun finish(heartRate: Int) {
        findNavController().popBackStack()
        setFragmentResult(PPG_FRAGMENT_REQUEST_KEY, bundleOf(HEART_RATE_BUNDLE to heartRate))
    }
}
