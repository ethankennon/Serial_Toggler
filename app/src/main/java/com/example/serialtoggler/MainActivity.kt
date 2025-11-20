package com.example.serialtoggler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.serialtoggler.ui.SerialCommunicationScreen
import com.example.serialtoggler.ui.SerialDeviceSelectScreen
import com.example.serialtoggler.ui.SerialTogglerViewModel
import com.example.serialtoggler.ui.theme.SerialTogglerTheme

class MainActivity : ComponentActivity() {

	private lateinit var serialTogglerViewModel: SerialTogglerViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		serialTogglerViewModel = SerialTogglerViewModel(applicationContext)

		serialTogglerViewModel.registerUsbReceiver(applicationContext)

		enableEdgeToEdge()
		setContent {
			SerialTogglerTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					SerialTogglerApp(
						viewModel = serialTogglerViewModel,
						modifier = Modifier.padding(innerPadding)
					)
				}
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		serialTogglerViewModel.unregisterUsbReceiver(applicationContext)
	}
}

enum class SerialTogglerScreen() {
	Connect,
	Communicate
}


@Composable
fun SerialTogglerApp(
	viewModel: SerialTogglerViewModel,
	navController: NavHostController = rememberNavController(),
	modifier: Modifier = Modifier
) {

	val backStackEntry by navController.currentBackStackEntryAsState()

	val currentScreen = SerialTogglerScreen.valueOf(
		backStackEntry?.destination?.route ?: SerialTogglerScreen.Connect.name
	)

	val uiState by viewModel.serialUiState.collectAsState()

	NavHost(
		navController = navController,
		startDestination = SerialTogglerScreen.Connect.name,
		modifier = modifier
	){
		composable(route = SerialTogglerScreen.Connect.name) {
			SerialDeviceSelectScreen(
				availableDevices = viewModel.availableDrivers,
				status = uiState.serialStatusMessage,
				deviceConnected = uiState.serialDeviceConnected,
				searchClicked = { viewModel.findDevices() },
				connectClicked = {
					viewModel.connectToDevice(it)
					//navController.navigate(SerialTogglerScreen.Communicate.name)
				},

				nextScreen = { navController.navigate(SerialTogglerScreen.Communicate.name) },
				modifier = modifier,
			)
		}
		composable(route = SerialTogglerScreen.Communicate.name) {
			SerialCommunicationScreen(
				status = uiState.serialStatusMessage,
				receivedMessage = uiState.serialMessageReceived,
				responseTime = uiState.serialResponseTime,
				sendCommand = { viewModel.sendCommand(it) },
				disconnectClicked = {
					viewModel.disconnectDevice()
					navController.navigate(SerialTogglerScreen.Connect.name)
				},
				modifier = modifier,
			)
		}
	}
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	val serialTogglerViewModel = SerialTogglerViewModel(applicationContext)
	SerialTogglerTheme {
		SerialTogglerApp(serialTogglerViewModel)
	}
}*/