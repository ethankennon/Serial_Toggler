package com.example.serialtoggler.data

import com.hoho.android.usbserial.driver.UsbSerialDriver

data class SerialUiState (
	var serialStatusMessage: String = "",
	var serialDeviceConnected: Boolean = false,
	var serialMessageReceived: String = "",
	var serialResponseTime: Long = 0
)
