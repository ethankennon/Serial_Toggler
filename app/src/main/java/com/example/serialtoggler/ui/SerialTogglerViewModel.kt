package com.example.serialtoggler.ui

import android.content.Context
import android.hardware.usb.UsbManager
import com.example.serialtoggler.data.SerialUiState
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class SerialTogglerViewModel(context: Context) {
	private val _serialUiState = MutableStateFlow(SerialUiState())
	val serialUiState: StateFlow<SerialUiState> = _serialUiState.asStateFlow()


	private val serialManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

	var availableDrivers: List<UsbSerialDriver> = emptyList()

	fun findDevices(): String {
		// use this to get just compatible devices instead of enumerating all USB devices
		availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(serialManager)

		if (availableDrivers.isEmpty()) {
			return "No Serial Devices Found"
		} else {
			return "Found ${availableDrivers.size} Serial Devices"
		}
	}

	fun connectToDevice(serialDevice: UsbSerialDriver) {

	}

}