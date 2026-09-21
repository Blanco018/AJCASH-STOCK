package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DemoExpiredLockScreen
import com.example.ui.screens.VehicleInventoryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.StockViewModel
import com.example.util.DemoExpirationManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: StockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa o recupera la marca de tiempo del primer inicio de la app
        DemoExpirationManager.getOrCreateFirstLaunchTime(this)

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                var isExpired by remember { mutableStateOf(DemoExpirationManager.isDemoExpired(context)) }
                var remainingMillis by remember { mutableLongStateOf(DemoExpirationManager.getRemainingMillis(context)) }

                // Verificación periódica del contador de 24 horas
                LaunchedEffect(Unit) {
                    while (true) {
                        isExpired = DemoExpirationManager.isDemoExpired(context)
                        remainingMillis = DemoExpirationManager.getRemainingMillis(context)
                        if (isExpired) break
                        delay(30000L) // Evaluación cada 30 segundos
                    }
                }

                if (isExpired) {
                    // Si expira el plazo de 24 horas, NO se carga la pantalla principal
                    DemoExpiredLockScreen(
                        expirationDateFormatted = DemoExpirationManager.formatExpirationDate(context),
                        onCloseApp = { finishAffinity() }
                    )
                } else {
                    MainContent(
                        viewModel = viewModel,
                        remainingMillis = remainingMillis
                    )
                }
            }
        }
    }
}

@Composable
fun MainContent(
    viewModel: StockViewModel,
    remainingMillis: Long
) {
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            DemoVersionBottomBar(remainingMillis = remainingMillis)
        }
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

/**
 * Barra inferior corporativa que muestra la insignia obligatoria "Versión Demo 24h"
 * junto con el tiempo restante y la versión de la compilación.
 */
@Composable
fun DemoVersionBottomBar(
    remainingMillis: Long,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF181818),
        border = BorderStroke(1.dp, Color(0xFF2B2B2B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge / Etiqueta: "Versión Demo 24h"
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2A2210),
                border = BorderStroke(1.dp, Color(0xFF92400E))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(0xFFFBBF24), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Versión Demo 24h",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFEF08A)
                    )
                }
            }

            // Información de tiempo restante y versión
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Resta: ${DemoExpirationManager.formatRemainingTime(remainingMillis)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "v2.2-DEMO1D",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}


