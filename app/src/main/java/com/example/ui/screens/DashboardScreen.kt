package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone

import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import com.example.data.model.Technician
import com.example.data.remote.CloudSyncState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.AjCashGreen
import com.example.ui.theme.AjCashGreenDark
import com.example.ui.theme.AjCashGreenDarker
import com.example.ui.theme.AjCashGreenLight
import com.example.ui.theme.AjCashGreenSubtle
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMedium
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusGreenText
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.StatusRedText
import com.example.ui.viewmodel.StockViewModel

@Composable
fun DashboardScreen(
    viewModel: StockViewModel,
    onVehicleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vehiclesSummaries by viewModel.vehiclesSummary.collectAsStateWithLifecycle()

    val totalVehicles = vehiclesSummaries.size
    val readyVehicles = vehiclesSummaries.count { it.isReadyForGuard }
    val pendingVehicles = vehiclesSummaries.count { !it.isReadyForGuard }

    var pendingVehicle by remember { mutableStateOf<com.example.data.model.Vehicle?>(null) }
    var showTechnicianSelectionModal by remember { mutableStateOf(false) }
    var showTechniciansManagerModal by remember { mutableStateOf(false) }
    var showRestockSummaryModal by remember { mutableStateOf(false) }

    val allTechnicians by viewModel.allTechnicians.collectAsStateWithLifecycle()
    val selectedTechnician by viewModel.selectedTechnician.collectAsStateWithLifecycle()
    val cloudSyncState by viewModel.cloudSyncState.collectAsStateWithLifecycle()

    // Diálogo emergente interactivo: Resumen de Materiales a Reponer
    if (showRestockSummaryModal) {
        RestockSummaryDialog(
            vehiclesSummaries = vehiclesSummaries,
            onDismiss = { showRestockSummaryModal = false }
        )
    }

    // Gestor de Técnicos: creación y borrado con confirmación
    if (showTechniciansManagerModal) {
        TechniciansManagerDialog(
            technicians = allTechnicians,
            onAddTechnician = { name, number ->
                viewModel.createTechnician(name, number)
            },
            onDeleteTechnician = { tech ->
                viewModel.deleteTechnician(tech)
            },
            onDismiss = {
                showTechniciansManagerModal = false
            }
        )
    }

    // Selector ágil de Técnico al seleccionar un vehículo
    if (showTechnicianSelectionModal && pendingVehicle != null) {
        val targetVehicle = pendingVehicle!!
        TechnicianSelectionDialog(
            vehicle = targetVehicle,
            technicians = allTechnicians,
            selectedTechnician = selectedTechnician,
            onTechnicianSelected = { tech ->
                viewModel.selectTechnician(tech)
            },
            onConfirmSelection = { tech ->
                viewModel.startVehicleInspectionWithTechnician(targetVehicle.id, tech)
                showTechnicianSelectionModal = false
                onVehicleSelected(targetVehicle.id)
            },
            onOpenManager = {
                showTechniciansManagerModal = true
            },
            onDismiss = {
                showTechnicianSelectionModal = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Corporate Header
        item {
            CorporateHeader(
                totalVehicles = totalVehicles,
                readyVehicles = readyVehicles,
                pendingVehicles = pendingVehicles,
                onOpenTechniciansManager = { showTechniciansManagerModal = true },
                onOpenRestockSummary = { showRestockSummaryModal = true }
            )
        }

        // Section Title
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Flota Técnica de Guardia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Cloud real-time sync pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (cloudSyncState) {
                            CloudSyncState.ONLINE_SYNCED -> Color(0xFFDCFCE7)
                            CloudSyncState.SYNCING -> Color(0xFFFEF3C7)
                            CloudSyncState.OFFLINE_LOCAL, CloudSyncState.UNCONFIGURED -> Color(0xFFF1F5F9)
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            0.8.dp,
                            when (cloudSyncState) {
                                CloudSyncState.ONLINE_SYNCED -> Color(0xFF86EFAC)
                                CloudSyncState.SYNCING -> Color(0xFFFCD34D)
                                CloudSyncState.OFFLINE_LOCAL, CloudSyncState.UNCONFIGURED -> Color(0xFFCBD5E1)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (cloudSyncState) {
                                    CloudSyncState.ONLINE_SYNCED -> Icons.Default.CloudDone
                                    CloudSyncState.SYNCING -> Icons.Default.Sync
                                    CloudSyncState.OFFLINE_LOCAL, CloudSyncState.UNCONFIGURED -> Icons.Default.CloudOff
                                },
                                contentDescription = "Estado de sincronización",
                                tint = when (cloudSyncState) {
                                    CloudSyncState.ONLINE_SYNCED -> Color(0xFF166534)
                                    CloudSyncState.SYNCING -> Color(0xFF92400E)
                                    CloudSyncState.OFFLINE_LOCAL, CloudSyncState.UNCONFIGURED -> Color(0xFF64748B)
                                },
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (cloudSyncState) {
                                    CloudSyncState.ONLINE_SYNCED -> "En tiempo real"
                                    CloudSyncState.SYNCING -> "Sincronizando..."
                                    CloudSyncState.OFFLINE_LOCAL, CloudSyncState.UNCONFIGURED -> "Modo local"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when (cloudSyncState) {
                                    CloudSyncState.ONLINE_SYNCED -> Color(0xFF166534)
                                    CloudSyncState.SYNCING -> Color(0xFF92400E)
                                    CloudSyncState.OFFLINE_LOCAL, CloudSyncState.UNCONFIGURED -> Color(0xFF64748B)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Selecciona un vehículo para inspeccionar y cuadrar stock",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateLight
                )
            }
        }

        // Vehicle Cards
        if (vehiclesSummaries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AjCashGreen)
                }
            }
        } else {
            items(vehiclesSummaries, key = { it.vehicle.id }) { summary ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    VehicleCard(
                        summary = summary,
                        onClick = {
                            pendingVehicle = summary.vehicle
                            showTechnicianSelectionModal = true
                        }
                    )
                }
            }
        }

        // Informational Technical Guard Protocol & Company Footer
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF4F9F1)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, AjCashGreenLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AjCashGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Protocolo de Guardia AJ CA\$H",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Antes de iniciar el turno de guardia, cada técnico debe cotejar las existencias físicas en el vehículo y registrar la revisión.",
                        fontSize = 12.sp,
                        color = SlateMedium,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "AJ CASH · Avda. Gómez de Avellaneda, 67 · 50018 Zaragoza · 976 29 88 50",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateLight
                    )
                }
            }
        }
    }
}

@Composable
fun CorporateHeader(
    totalVehicles: Int,
    readyVehicles: Int,
    pendingVehicles: Int,
    modifier: Modifier = Modifier,
    onOpenTechniciansManager: () -> Unit = {},
    onOpenRestockSummary: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AjCashGreenDarker,
                        AjCashGreen
                    )
                )
            )
            .padding(top = 20.dp, bottom = 24.dp, start = 18.dp, end = 18.dp)
    ) {
        Column {
            // Brand row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Logo AJ CASH",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AJ CA\$H",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                        Text(
                            text = "SOLUCIONES PUNTO DE VENTA",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón Gestor de Técnicos en la cabecera (chip pill redondeado con indicador verde brillante)
                Surface(
                    onClick = onOpenTechniciansManager,
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.28f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC).copy(alpha = 0.75f)),
                    modifier = Modifier.testTag("header_technicians_manager_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(Color(0xFF4ADE80), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Gestor de Técnicos",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "GESTOR TÉCNICOS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.4.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Title
            Text(
                text = "Control de Stock de Vehículos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Garantía de existencias mínimas para incidencias técnicas",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fleet Summary KPI Cards (Actionable stock status, click to view restock summary modal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FleetKpiPill(
                    label = "Stock Completo",
                    value = "$readyVehicles",
                    color = Color(0xFF86EFAC),
                    bg = Color(0xFF14532D).copy(alpha = 0.5f),
                    icon = Icons.Default.CheckCircle,
                    onClick = onOpenRestockSummary,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stock_completo_kpi_pill")
                )
                FleetKpiPill(
                    label = "Requieren Reponer",
                    value = "$pendingVehicles",
                    color = Color(0xFFFCA5A5),
                    bg = Color(0xFF7F1D1D).copy(alpha = 0.5f),
                    icon = Icons.Default.Warning,
                    onClick = onOpenRestockSummary,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("requieren_reponer_kpi_pill")
                )
            }
        }
    }
}

@Composable
fun FleetKpiPill(
    label: String,
    value: String,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = if (onClick != null) BorderStroke(1.dp, color.copy(alpha = 0.4f)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = color
                    )
                    if (onClick != null) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Ver detalles",
                            tint = color.copy(alpha = 0.6f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
