package com.example.serialtoggler.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoho.android.usbserial.driver.UsbSerialDriver
import java.util.Objects
import java.util.Objects.toString


// Composable to show the list of devices available for connection and choose one
@Composable
fun SerialDeviceSelectScreen(
	availableDevices: List<UsbSerialDriver>,
	status: String,
	deviceConnected: Boolean,
	searchClicked: () -> Unit,
	connectClicked: (UsbSerialDriver) -> Unit,
	nextScreen: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Button(onClick = searchClicked ) {
			Text(text = "Search for Devices")
		}
		Text(text = status)
		LazyColumn(modifier = Modifier.fillMaxWidth()) {
			items(availableDevices.size) { index ->
				SerialDeviceCard(
					availableDevices[index],
					connectClicked
				)
			}
		}
		Button(
			onClick = nextScreen,
			enabled = deviceConnected
		) {
			Text(text = "Open Connection")
		}
	}
}

@Composable
fun SerialDeviceCard(
	availableDevice: UsbSerialDriver,
	connectClicked: (UsbSerialDriver) -> Unit,
	modifier: Modifier = Modifier
){
	Card(
		onClick = {
			connectClicked(availableDevice)
		},
		modifier = modifier
			.fillMaxWidth()
			.padding(16.dp)
	) {
		Text(
			text = availableDevice.device.deviceName, //Objects.toString(availableDevice),
			modifier = Modifier.padding(16.dp)
			)
	}
}

@Preview(showBackground = true)
@Composable
fun SerialDeviceSelectScreenPreview() {
	val availableDevices: List<UsbSerialDriver> = emptyList()
	SerialDeviceSelectScreen(availableDevices, "Status", false,{}, {}, {}, modifier = Modifier)
}