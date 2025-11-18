package com.example.serialtoggler.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


// Composable to show the list of devices available for connection and choose one
@Composable
fun SerialDeviceSelectScreen(nextScreen: () -> Unit, modifier: Modifier = Modifier) {
	Column(modifier = modifier) {
		Button(onClick = { /*TODO*/ }) {
			Text(text = "Search for Devices")
		}
		Text(text = "Devices")
		Button(onClick = nextScreen ) {
			Text(text = "Go to next screen")
		}
	}
}

@Preview(showBackground = true)
@Composable
fun SerialDeviceSelectScreenPreview() {
	SerialDeviceSelectScreen({})
}