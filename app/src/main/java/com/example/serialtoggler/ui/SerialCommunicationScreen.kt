package com.example.serialtoggler.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

// Composable to send brief commands to the serial device
@Composable
fun SerialCommunicationScreen(
	status: String,
	receivedMessage: String,
	responseTime: Long,
	sendCommand: (String) -> Unit,
	disconnectClicked: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Row (
			modifier = modifier
				.fillMaxWidth()

		){
			Button(onClick = { sendCommand("e") } ) {
				Text(text = "Enable")
			}
			Button(onClick = { sendCommand("d") }) {
				Text(text = "Disable")
			}
		}
		Button(onClick = { sendCommand("p") }) {
			Text(text = "Ping")
		}
		Text(text = status)
		Text(text = receivedMessage)
		Text(text = "Response time: $responseTime ns")
		Button(onClick = disconnectClicked) {
			Text(text = "Disconnect")
		}
	}
}

@Preview(showBackground = true)
@Composable
fun SerialCommunicationScreenPreview() {
	SerialCommunicationScreen("Status", "1", 435000, {}, {}, modifier = Modifier)
}