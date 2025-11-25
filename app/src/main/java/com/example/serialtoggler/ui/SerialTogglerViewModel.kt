package com.example.serialtoggler.ui

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.example.serialtoggler.data.DataSource
import com.example.serialtoggler.data.DataSource.ACTION_USB_PERMISSION
import com.example.serialtoggler.data.DataSource.WRITE_WAIT_MILLIS
import com.example.serialtoggler.data.SerialUiState
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException
import kotlin.system.measureNanoTime


class SerialTogglerViewModel(context: Context) {
	private val _serialUiState = MutableStateFlow(SerialUiState())
	val serialUiState: StateFlow<SerialUiState> = _serialUiState.asStateFlow()

	var start: Long = 0
	var stop: Long = 0

	private val serialManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

	var availableDrivers: List<UsbSerialDriver> = emptyList()

	var port: UsbSerialPort? = null

	lateinit var usbIoManager: SerialInputOutputManager


	private val usbReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			if (ACTION_USB_PERMISSION == intent.action) {
				synchronized(this) {
					val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)

					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							updateSerialStatusMessage("USB Permission Granted for ${device.deviceName}") //this doesn't work and the AI doesn't know why either
							// Automatically try to connect now that you have permission.
							// val driver = UsbSerialProber.getDefaultProber().probeDevice(device)
							// if (driver != null) {
							// 	connectToDevice(driver)
							// }
						}
					} else {
						//Log.d(TAG, "permission denied for accessory $accessory")
						updateSerialStatusMessage("USB Permission Denied")
					}
				}
			}
		}
	}

	private val permissionIntent = PendingIntent.getBroadcast(
		context,
		0,
		Intent(DataSource.ACTION_USB_PERMISSION),
		PendingIntent.FLAG_IMMUTABLE
	)

	fun registerUsbReceiver(context: Context) {
		context.registerReceiver(usbReceiver, IntentFilter(DataSource.ACTION_USB_PERMISSION), Context.RECEIVER_NOT_EXPORTED) //AI suggestion when declaring the function
	}

	fun unregisterUsbReceiver(context: Context) {
		context.unregisterReceiver(usbReceiver)
	}

	fun updateSerialStatusMessage(message: String) {
		_serialUiState.update { currentState ->
			currentState.copy(
				serialStatusMessage = message
			)
		}
	}

	fun updateSerialMessageReceived(message: String) {
		_serialUiState.update { currentState ->
			currentState.copy(
				serialMessageReceived = message
			)
		}
	}

	fun updateSerialDeviceConnected(connected: Boolean){
		_serialUiState.update { currentState ->
			currentState.copy(
				serialDeviceConnected = connected
			)
		}
	}

	fun updateSerialResponseTime(responseTime: Long) {
		_serialUiState.update { currentState ->
			currentState.copy(
				serialResponseTime = responseTime
			)
		}
	}

	fun findDevices() {
		// use this to get just compatible devices instead of enumerating all USB devices
		availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(serialManager)

		when (availableDrivers.size) {
			0-> updateSerialStatusMessage("No Serial Devices Found")
			1-> updateSerialStatusMessage("Found ${availableDrivers.size} Serial Device")
			else -> updateSerialStatusMessage("Found ${availableDrivers.size} Serial Devices")
		}
	}

	fun connectToDevice(serialDevice: UsbSerialDriver) {
		if (!serialManager.hasPermission(serialDevice.device)) {
			updateSerialStatusMessage("USB permission requested. Tap again to connect")
			serialManager.requestPermission(serialDevice.device, permissionIntent)
			return // Stop here. The user needs to interact with the dialog.
		}
		val connection = serialManager.openDevice(serialDevice.device)
		if (connection == null) {
			updateSerialStatusMessage("Could Not Connect")
			return
		}
		port = serialDevice.ports.first()
		try {
			port?.open(connection)
			port?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
			updateSerialStatusMessage("Connected to ${serialDevice.device.deviceName}")
			updateSerialDeviceConnected(true)
			usbIoManager = SerialInputOutputManager(port, object : SerialInputOutputManager.Listener {
				override fun onNewData(data: ByteArray) {
					updateSerialMessageReceived(String(data))
					stop = System.nanoTime()
					updateSerialResponseTime(stop - start)
				}
				override fun onRunError(e: Exception) { //this is probably way too generous but I don't know what I'm catching with this - catches serial port being closed
					updateSerialStatusMessage("Error: ${e.message}")
				}
			})
			usbIoManager.start()
		} catch (e: IOException) {
			updateSerialStatusMessage("Error opening port: ${e.message}")
			updateSerialDeviceConnected(false)
			try {
				port?.close()
			} catch (ignored: IOException) {
				// Ignore any errors during closing
			}
		}
	}

	fun sendCommand(command: String) {
		start = System.nanoTime()
		port?.write(command.toByteArray(),WRITE_WAIT_MILLIS)
	}

	fun disconnectDevice(){
		updateSerialDeviceConnected(false)
		try {
			usbIoManager.stop()
			port?.close()
		} catch (ignored: IOException) {
			// Ignore any errors during closing
		}
	}

}