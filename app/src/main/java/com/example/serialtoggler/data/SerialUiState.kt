package com.example.serialtoggler.data

class SerialUiState {
	var serialStatusMessage: String = ""
	var serialDeviceConnected: String = "" //probably a different type and not sure if needed here
	var serialMessageReceived: String = ""
	var serialResponseTime: Long = 0
}
