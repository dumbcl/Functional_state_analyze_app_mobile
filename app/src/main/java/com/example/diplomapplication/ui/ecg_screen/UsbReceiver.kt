package com.example.diplomapplication.ui.ecg_screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager

class UsbReceiver: BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
            //val device = p1.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            // Обработка подключения устройства
        }
    }
}
