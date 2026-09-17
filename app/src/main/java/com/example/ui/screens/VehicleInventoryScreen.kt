package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Vehicle
import com.example.ui.theme.AjCashGreen
import com.example.ui.theme.AjCashGreenDark
import com.example.ui.theme.AjCashGreenLight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleInventoryScreen(
    vehicle: Vehicle,
    viewModel: StockViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.currentVehicleItems.collectAsStateWithLifecycle()
    val allVehicleItems by viewModel.allCurrentVehicleItems.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val showOnlyAlerts by viewModel.showOnlyAlerts.collectAsStateWithLifecycle()
    val revisions by viewModel.getRevisionsForVehicle(vehicle.id).collectAsStateWithLifecycle(initialValue = emptyList<com.example.data.model.RevisionRecord>())
    var showConfigSheet by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(allVehicleItems) {
        viewModel.captureInitialStockSnapshot(allVehicleItems)
    }

    val totalItemsCount = if (allVehicleItems.isNotEmpty()) allVehicleItems.size else items.size
    val underMinimumCount = if (allVehicleItems.isNotEmpty()) allVehicleItems.count { it.isUnderMinimum } else items.count { it.isUnderMinimum }
    val isFullyReady = underMinimumCount == 0

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("vehicle_inventory_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (vehicle.model.isNotBlank()) "${vehicle.name} · ${vehicle.model}" else vehicle.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${vehicle.type} · ${vehicle.plate}",
                            fontSize = 12.sp,
                            color = SlateLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al listado de vehículos"
                        )
                    }
                },
                actions = {
                    // Status Badge in Top Bar
                    Surface(
                        modifier = Modifier.padding(end = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isFullyReady) StatusGreenBg else StatusRedBg,
                        border = BorderStroke(
                            1.dp,
                            if (isFullyReady) StatusGreen.copy(alpha = 0.4f) else StatusRed.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isFullyReady) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isFullyReady) StatusGreen else StatusRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isFullyReady) "Guardia OK" else "$underMinimumCount falta(s)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFullyReady) StatusGreenText else StatusRedText
                            )
                        }
                    }

                    // Button to open Minimum Stock Settings
                    IconButton(
                        onClick = { showConfigSheet = true },
                        modifier = Modifier.testTag("configure_vehicle_minimums_topbar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Ajustar mínimos del vehículo",
                            tint = AjCashGreen
                        )
                    }

                    // Button to open Revision History
                    IconButton(
                        onClick = { showHistoryDialog = true },
                        modifier = Modifier.testTag("vehicle_history_topbar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Historial de revisiones",
                            tint = AjCashGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Persistent Bottom Bar for Mobile Ergonomics
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Restock Button (If items are depleted)
                    if (underMinimumCount > 0) {
                        OutlinedButton(
                            onClick = { viewModel.restoreAllToMinimums(vehicle.id) },
                            modifier = Modifier.testTag("restock_minimums_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AjCashGreen
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reponer Todo", fontSize = 13.sp)
                        }
                    }

                    // Save / Confirm Revision Button
                    Button(
                        onClick = { viewModel.recordVehicleRevision(vehicle.id, allVehicleItems) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("confirm_revision_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AjCashGreen
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar / Confirmar Revisión",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Vehicle Hero Card with photo & revision details
            item {
                VehicleHeroBanner(
                    vehicle = vehicle,
                    underMinimumCount = underMinimumCount,
                    totalItems = totalItemsCount
                )
            }

            // Prominent "HISTORIAL DE REVISIONES" Action Card
            item {
                Card(
                    onClick = { showHistoryDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("vehicle_history_banner_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) Color(0xFF14241B) else Color(0xFFF2FBF4)
                    ),
                    border = BorderStroke(1.5.dp, AjCashGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(AjCashGreen.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = AjCashGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "HISTORIAL DE REVISIONES",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.5.sp,
                                        letterSpacing = 0.3.sp,
                                        color = AjCashGreen
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = AjCashGreen.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "${revisions.size}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AjCashGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Auditoría de técnicos y desglose cronológico de cambios",
                                    fontSize = 11.sp,
                                    color = SlateLight
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AjCashGreen,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Ver Historial",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Dedicated Vehicle Stock Minimums Configuration Section Card
            item {
                Card(
                    onClick = { showConfigSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("configure_vehicle_minimums_banner"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) Color(0xFF142618) else Color(0xFFF0FDF4)
                    ),
                    border = BorderStroke(
                        1.dp,
                        AjCashGreen.copy(alpha = 0.45f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(AjCashGreen.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = AjCashGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ajustar Mínimos del Vehículo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Establece el stock de guardia requerido (ej. 2 Impresoras 450)",
                                    fontSize = 11.sp,
                                    color = SlateLight
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AjCashGreen,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Ajustar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Search and Alert Toggle Filter
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_input"),
                        placeholder = { Text("Buscar producto (ej. TPV x500, cable...)", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = SlateLight,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Borrar búsqueda",
                                        tint = SlateLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AjCashGreen,
                            unfocusedBorderColor = SlateBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Category Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Alert Filter Chip
                        FilterChip(
                            selected = showOnlyAlerts,
                            onClick = { viewModel.toggleOnlyAlerts() },
                            label = {
                                Text(
                                    text = "Solo Faltas ($underMinimumCount)",
                                    fontSize = 12.sp,
                                    fontWeight = if (showOnlyAlerts) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (showOnlyAlerts) StatusRed else SlateLight
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StatusRedBg,
                                selectedLabelColor = StatusRedText
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = showOnlyAlerts,
                                borderColor = if (showOnlyAlerts) StatusRed else SlateBorder
                            )
                        )

                        // Category Chips
                        viewModel.allCategories.forEach { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setCategoryFilter(category) },
                                label = {
                                    Text(
                                        text = category,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AjCashGreenLight,
                                    selectedLabelColor = AjCashGreen
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) AjCashGreen else SlateBorder
                                )
                            )
                        }
                    }
                }
            }

            // Results count header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Materiales del Vehículo (${items.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                    Text(
                        text = "Usa + / - para actualizar",
                        fontSize = 11.sp,
                        color = SlateLight
                    )
                }
            }

            // Empty State
            if (items.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = SlateLight,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No se encontraron materiales",
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                            Text(
                                text = "Comprueba el filtro de búsqueda o categoría seleccionado.",
                                fontSize = 12.sp,
                                color = SlateLight
                            )
                        }
                    }
                }
            } else {
                // Stock Items List
                items(items, key = { it.id }) { item ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        StockItemCard(
                            item = item,
                            onIncrement = { viewModel.incrementQuantity(item) },
                            onDecrement = { viewModel.decrementQuantity(item) },
                            onSetQuantity = { qty -> viewModel.setQuantity(item, qty) },
                            onUpdateMinimum = { newMin -> viewModel.updateMinimumQuantity(item, newMin) }
                        )
                    }
                }
            }
        }
    }

    if (showConfigSheet) {
        VehicleMinimumsConfigSheet(
            vehicle = vehicle,
            items = allVehicleItems,
            onDismiss = { showConfigSheet = false },
            onUpdateMinimum = { item, newMin -> viewModel.updateMinimumQuantity(item, newMin) },
            onAddNewItem = { name, cat, min, cur, unit ->
                viewModel.addNewItem(vehicle.id, name, cat, min, cur, unit)
            }
        )
    }

    if (showHistoryDialog) {
        VehicleRevisionHistoryDialog(
            vehicle = vehicle,
            revisions = revisions,
            onDismiss = { showHistoryDialog = false }
        )
    }
}

@Composable
fun VehicleHeroBanner(
    vehicle: Vehicle,
    underMinimumCount: Int,
    totalItems: Int,
    modifier: Modifier = Modifier
) {
    val imageRes = getVehicleDrawable(vehicle.imageDrawableName)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Fotografía de ${vehicle.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Revisión de Guardia",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AjCashGreen
                        )
                        Text(
                            text = "Última confirmada: ${formatTimestamp(vehicle.lastRevisionTimestamp)}",
                            fontSize = 12.sp,
                            color = SlateMedium
                        )
                    }
                    Text(
                        text = vehicle.lastReviewedBy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateLight
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val safeTotal = totalItems.coerceAtLeast(1)
                val coveragePercent = if (totalItems <= 0) 100 else (((totalItems - underMinimumCount).toFloat() / safeTotal) * 100).toInt().coerceIn(0, 100)
                val coverageFloat = (coveragePercent.toFloat() / 100f).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (underMinimumCount == 0) "Stock Completo de Guardia (100%)" else "$underMinimumCount producto(s) bajo el umbral mínimo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (underMinimumCount == 0) StatusGreenText else StatusRedText
                    )
                    Text(
                        text = "$coveragePercent%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (underMinimumCount == 0) StatusGreenText else StatusRedText
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { coverageFloat },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (underMinimumCount == 0) StatusGreen else StatusRed,
                    trackColor = Color(0xFFE2E8F0)
                )
            }
        }
    }
}
