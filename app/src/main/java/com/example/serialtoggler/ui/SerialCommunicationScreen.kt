package com.example.serialtoggler.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

// Composable to send brief commands to the serial device
@Composable
fun SerialCommunicationScreen(reconnectScreen: () -> Unit, modifier: Modifier = Modifier) {
	Column(modifier = modifier) {
		Button(onClick = { /*TODO*/ }) {
			Text(text = "Search for Devices")
		}
		Text(text = "Devices")
		Button(onClick = reconnectScreen) {
			Text(text = "Back")
		}
	}
}

@Preview(showBackground = true)
@Composable
fun SerialCommunicationScreenPreview() {
	SerialCommunicationScreen({})
}