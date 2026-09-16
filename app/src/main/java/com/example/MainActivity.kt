package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.VehicleInventoryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.StockViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: StockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: StockViewModel) {
    val selectedVehicleId by viewModel.selectedVehicleId.collectAsStateWithLifecycle()
    val currentVehicle by viewModel.currentVehicle.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.userMessage.collectLatest { message ->
            try {
                snackbarHostState.showSnackbar(message)
            } catch (_: Exception) {
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (selectedVehicleId != null && currentVehicle != null) {
                BackHandler {
                    viewModel.selectVehicle(null)
                }
                VehicleInventoryScreen(
                    vehicle = currentVehicle!!,
                    viewModel = viewModel,
                    onBack = { viewModel.selectVehicle(null) }
                )
            } else {
                DashboardScreen(
                    viewModel = viewModel,
                    onVehicleSelected = { vehicleId ->
                        viewModel.selectVehicle(vehicleId)
                    }
                )
            }
        }
    }
}

