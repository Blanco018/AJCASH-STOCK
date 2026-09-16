package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.StockItem
import com.example.data.model.Vehicle
import com.example.data.model.VehicleStockSummary
import com.example.ui.theme.AjCashGreen
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMedium
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberBg
import com.example.ui.theme.StatusAmberText
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusGreenText
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.StatusRedText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getVehicleDrawable(drawableName: String): Int {
    return when (drawableName) {
        "vehicle_car_1" -> R.drawable.vehicle_car_1
        "vehicle_car_2" -> R.drawable.vehicle_car_2
        "vehicle_van_1" -> R.drawable.vehicle_van_1
        "vehicle_van_2" -> R.drawable.vehicle_van_2
        else -> R.drawable.vehicle_car_1
    }
}

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val now = System.currentTimeMillis()
    val diffHours = (now - timestamp) / (1000 * 60 * 60)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    return when {
        diffHours < 24 -> "Hoy a las $timeFormat h"
        diffHours < 48 -> "Ayer a las $timeFormat h"
        else -> SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
    }
}

@Composable
fun VehicleCard(
    summary: VehicleStockSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicle = summary.vehicle
    val isReady = summary.isReadyForGuard
    val imageRes = getVehicleDrawable(vehicle.imageDrawableName)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vehicle_card_${vehicle.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isReady) 1.dp else 1.5.dp,
            color = if (isReady) SlateBorder else StatusRed.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Space reserved for vehicle real photography
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Fotografía de ${vehicle.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Vehicle Type Badge (Coche / Furgoneta)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (vehicle.type.contains("Furgoneta", ignoreCase = true))
                                Icons.Default.LocalShipping else Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = vehicle.type,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Status Badge overlay
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isReady) StatusGreen else StatusRed
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isReady) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isReady) "GUARDIA OK" else "FALTA STOCK",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Card Body Info
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (vehicle.model.isNotBlank()) "${vehicle.name} · ${vehicle.model}" else vehicle.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${vehicle.type} · Matrícula: ${vehicle.plate}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateLight
                        )
                    }

                    // Coverage percentage pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isReady) StatusGreenBg else StatusRedBg,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isReady) StatusGreen.copy(alpha = 0.3f) else StatusRed.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = "${summary.coveragePercentage}%",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = if (isReady) StatusGreenText else StatusRedText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress indicator
                val coverageFloat = (summary.coveragePercentage.toFloat() / 100f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { coverageFloat },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (isReady) StatusGreen else StatusRed,
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Deficit Alert or Ready status description
                if (!isReady) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = StatusRedBg
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = StatusRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${summary.underMinimumCount} artículo(s) por debajo del mínimo de guardia",
                                color = StatusRedText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = StatusGreenBg
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Inventario cubre el 100% de los mínimos para guardia",
                                color = StatusGreenText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Footer with Revision Timestamp & Reviewer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Última rev: ${formatTimestamp(vehicle.lastRevisionTimestamp)}",
                        fontSize = 11.sp,
                        color = SlateLight
                    )
                    Text(
                        text = "Ver inventario →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AjCashGreen
                    )
                }
            }
        }
    }
}

@Composable
fun StockItemCard(
    item: StockItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onSetQuantity: (Int) -> Unit,
    onUpdateMinimum: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showEditMinimumDialog by remember { mutableStateOf(false) }
    val isUnder = item.isUnderMinimum
    val cardBorderColor by animateColorAsState(
        targetValue = if (isUnder) StatusRed.copy(alpha = 0.5f) else SlateBorder,
        label = "borderColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("stock_item_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(width = if (isUnder) 1.5.dp else 1.dp, color = cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Category small tag
                    val isDarkCategory = isSystemInDarkTheme()
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isDarkCategory) Color(0xFF1E3825) else Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = item.category.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkCategory) Color(0xFF86EFAC) else SlateMedium,
                            letterSpacing = 0.4.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = if (onUpdateMinimum != null) {
                            Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showEditMinimumDialog = true }
                                .padding(vertical = 2.dp)
                        } else {
                            Modifier
                        }
                    ) {
                        Text(
                            text = "Mínimo de guardia: ",
                            fontSize = 12.sp,
                            color = SlateLight
                        )
                        Text(
                            text = "${item.minimumQuantity} ${item.unit}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (onUpdateMinimum != null) AjCashGreen else SlateMedium
                        )
                        if (onUpdateMinimum != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Modificar mínimo",
                                modifier = Modifier.size(11.dp),
                                tint = AjCashGreen
                            )
                        }
                    }
                }

                // Status Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isUnder) StatusRedBg else StatusGreenBg,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isUnder) StatusRed.copy(alpha = 0.3f) else StatusGreen.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isUnder) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isUnder) StatusRed else StatusGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isUnder) "Falta ${item.deficit} ${item.unit}" else "OK",
                            color = if (isUnder) StatusRedText else StatusGreenText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Counter Stepper Controls
            val isDark = isSystemInDarkTheme()
            val stepperBgColor = if (isDark) Color(0xFF122216) else Color(0xFFF1F5F9)
            val stepperBorderColor = if (isDark) Color(0xFF1E3825) else SlateBorder

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(stepperBgColor, shape = RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, stepperBorderColor), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stock Actual:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xFFE2E8F0) else SlateMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decrement Button (-) clearly visible in high-contrast red
                    IconButton(
                        onClick = onDecrement,
                        enabled = item.currentQuantity > 0,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("decrement_${item.id}"),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White,
                            disabledContainerColor = if (isDark) Color(0xFF1E2D22) else Color(0xFFE2E8F0),
                            disabledContentColor = if (isDark) Color(0xFF4B6350) else Color(0xFF94A3B8)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Restar una unidad (-)",
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Current Quantity Number (Clickable to edit directly)
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { showEditDialog = true }
                            .testTag("quantity_display_${item.id}"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isUnder) {
                            if (isDark) Color(0xFF3F1313) else StatusRedBg
                        } else {
                            if (isDark) Color(0xFF16291C) else Color.White
                        },
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = if (isUnder) StatusRed else AjCashGreen
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.currentQuantity}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isUnder) StatusRed else AjCashGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.unit,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFF94A3B8) else SlateLight
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar directamente",
                                modifier = Modifier.size(12.dp),
                                tint = if (isDark) Color(0xFF94A3B8) else SlateLight
                            )
                        }
                    }

                    // Increment Button (+) clearly visible in corporate green
                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("increment_${item.id}"),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = AjCashGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir una unidad (+)",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        NumberInputDialog(
            title = "Ajustar Stock",
            subtitle = item.name,
            currentValue = item.currentQuantity,
            unit = item.unit,
            onDismiss = { showEditDialog = false },
            onConfirm = { newQty ->
                onSetQuantity(newQty)
                showEditDialog = false
            }
        )
    }

    if (showEditMinimumDialog && onUpdateMinimum != null) {
        NumberInputDialog(
            title = "Ajustar Mínimo de Guardia",
            subtitle = item.name,
            prompt = "Introduce el stock mínimo requerido para este vehículo (${item.unit}):",
            currentValue = item.minimumQuantity,
            unit = item.unit,
            onDismiss = { showEditMinimumDialog = false },
            onConfirm = { newMin ->
                onUpdateMinimum(newMin)
                showEditMinimumDialog = false
            }
        )
    }
}

@Composable
fun NumberInputDialog(
    title: String,
    subtitle: String,
    currentValue: Int,
    unit: String,
    prompt: String = "Introduce la cantidad exacta de existencias ($unit):",
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var textValue by remember(currentValue) { mutableStateOf(currentValue.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SlateLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column {
                Text(
                    text = prompt,
                    fontSize = 13.sp,
                    color = SlateMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 5) {
                            textValue = input
                            isError = false
                        }
                    },
                    isError = isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val parsed = textValue.toIntOrNull()
                            if (parsed != null && parsed >= 0) {
                                onConfirm(parsed)
                            } else {
                                isError = true
                            }
                        }
                    ),
                    trailingIcon = {
                        Text(
                            text = unit,
                            color = SlateLight,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exact_quantity_input")
                )
                if (isError) {
                    Text(
                        text = "Introduce un número válido mayor o igual a 0",
                        color = StatusRed,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = textValue.toIntOrNull()
                    if (parsed != null && parsed >= 0) {
                        onConfirm(parsed)
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.testTag("confirm_exact_quantity")
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleMinimumsConfigSheet(
    vehicle: Vehicle,
    items: List<StockItem>,
    onDismiss: () -> Unit,
    onUpdateMinimum: (StockItem, Int) -> Unit,
    onAddNewItem: (name: String, category: String, minimumQuantity: Int, initialQuantity: Int, unit: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("Todas", "TPVs", "Impresoras", "Cables", "Consumibles", "Periféricos", "Red")

    val filteredItems = remember(items, searchQuery, selectedCategory) {
        items.filter { item ->
            val matchCat = selectedCategory == "Todas" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
            matchCat && matchQuery
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("vehicle_minimums_config_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(AjCashGreen.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = AjCashGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ajustar Mínimos de Stock",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${vehicle.name} · ${vehicle.model} (${vehicle.plate})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AjCashGreen
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_config_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar configuración"
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Define las cantidades mínimas requeridas para este vehículo en guardias y servicios (ej. mínimo de 2 impresoras 450).",
                fontSize = 12.sp,
                color = SlateLight,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("config_search_input"),
                placeholder = { Text("Buscar material (ej. 450, TPV, cable...)", fontSize = 13.sp) },
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
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar",
                                tint = SlateLight,
                                modifier = Modifier.size(16.dp)
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

            // Categories horizontal scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AjCashGreen.copy(alpha = 0.15f),
                            selectedLabelColor = AjCashGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prominent Action Card: CREAR NUEVO OBJETO PARA LA GUARDIA
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAddDialog = true }
                    .testTag("create_new_guard_item_banner"),
                shape = RoundedCornerShape(12.dp),
                color = AjCashGreen.copy(alpha = 0.12f),
                border = BorderStroke(1.5.dp, AjCashGreen)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AjCashGreen,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CREAR NUEVO OBJETO PARA LA GUARDIA",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.5.sp,
                                letterSpacing = 0.4.sp,
                                color = AjCashGreen
                            )
                            Text(
                                text = "Añade material específico no catalogado para este vehículo",
                                fontSize = 11.sp,
                                color = SlateLight
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Crear nuevo objeto",
                        tint = AjCashGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Items list
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron materiales para configurar.",
                                fontSize = 13.sp,
                                color = SlateLight
                            )
                        }
                    }
                } else {
                    items(filteredItems, key = { it.id }) { item ->
                        ConfigMinimumItemRow(
                            item = item,
                            onUpdateMinimum = { newMin -> onUpdateMinimum(item, newMin) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_custom_item_to_vehicle_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AjCashGreen),
                        border = BorderStroke(1.dp, AjCashGreen.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ CREAR OTRO OBJETO PARA LA GUARDIA", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_and_close_config_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AjCashGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Listo · Guardar Mínimos", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    if (showAddDialog) {
        AddNewStockItemDialog(
            vehicle = vehicle,
            categories = categories.filter { it != "Todas" },
            onDismiss = { showAddDialog = false },
            onConfirm = { name, cat, min, current, unit ->
                onAddNewItem(name, cat, min, current, unit)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ConfigMinimumItemRow(
    item: StockItem,
    onUpdateMinimum: (Int) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isDark) Color(0xFF142217) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF233B27) else SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("config_row_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isDark) Color(0xFF1E3825) else Color(0xFFE2E8F0)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF86EFAC) else SlateMedium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Stock en vehículo: ${item.currentQuantity} ${item.unit}",
                    fontSize = 11.sp,
                    color = if (item.isUnderMinimum) StatusRed else SlateLight
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Minimum stepper
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrement minimum (-)
                IconButton(
                    onClick = {
                        if (item.minimumQuantity > 0) {
                            onUpdateMinimum(item.minimumQuantity - 1)
                        }
                    },
                    enabled = item.minimumQuantity > 0,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("config_min_dec_${item.id}"),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White,
                        disabledContainerColor = if (isDark) Color(0xFF1E2D22) else Color(0xFFE2E8F0),
                        disabledContentColor = if (isDark) Color(0xFF4B6350) else Color(0xFF94A3B8)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Reducir mínimo (-)",
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clickable { showEditDialog = true }
                        .testTag("config_min_display_${item.id}"),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF1B3320) else Color.White,
                    border = BorderStroke(1.dp, AjCashGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mín: ",
                            fontSize = 11.sp,
                            color = SlateLight
                        )
                        Text(
                            text = "${item.minimumQuantity}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AjCashGreen
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = item.unit,
                            fontSize = 11.sp,
                            color = SlateLight
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar directamente",
                            modifier = Modifier.size(10.dp),
                            tint = AjCashGreen
                        )
                    }
                }

                // Increment minimum (+)
                IconButton(
                    onClick = { onUpdateMinimum(item.minimumQuantity + 1) },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("config_min_inc_${item.id}"),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = AjCashGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar mínimo (+)",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        NumberInputDialog(
            title = "Mínimo de Guardia",
            subtitle = item.name,
            prompt = "Establece el stock mínimo obligatorio para este vehículo (${item.unit}):",
            currentValue = item.minimumQuantity,
            unit = item.unit,
            onDismiss = { showEditDialog = false },
            onConfirm = { newMin ->
                onUpdateMinimum(newMin)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun AddNewStockItemDialog(
    vehicle: Vehicle,
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, minQuantity: Int, currentQuantity: Int, unit: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val fullCategories = remember(categories) {
        val base = categories.toMutableList()
        listOf("TPVs", "Impresoras", "Cables", "Consumibles", "Periféricos", "Redes", "Herramientas", "Otros").forEach {
            if (!base.contains(it)) base.add(it)
        }
        base
    }
    var selectedCategory by remember { mutableStateOf(fullCategories.firstOrNull() ?: "TPVs") }
    var minQtyText by remember { mutableStateOf("2") }
    var currentQtyText by remember { mutableStateOf("2") }
    var unit by remember { mutableStateOf("uds") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "CREAR NUEVO OBJETO PARA LA GUARDIA",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = AjCashGreen
                )
                Text(
                    text = "Material para ${vehicle.name} · ${vehicle.plate}",
                    fontSize = 12.sp,
                    color = SlateLight
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; hasError = false },
                    label = { Text("Nombre del material u objeto *") },
                    placeholder = { Text("Ej: Impresora 450, Switch 8p, Lector...") },
                    singleLine = true,
                    isError = hasError && name.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("new_item_name_input")
                )

                // Category selection
                Text("Categoría técnica:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateLight)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    fullCategories.forEach { cat ->
                        FilterChip(
                            selected = cat == selectedCategory,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AjCashGreen.copy(alpha = 0.2f),
                                selectedLabelColor = AjCashGreen
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minQtyText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) minQtyText = it },
                        label = { Text("Mínimo Guardia") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("new_item_min_input")
                    )
                    OutlinedTextField(
                        value = currentQtyText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) currentQtyText = it },
                        label = { Text("Stock Actual") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("new_item_stock_input")
                    )
                }

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unidad (ej. uds, rollos, metros)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (hasError) {
                    Text(
                        text = "Por favor introduce el nombre del material a registrar",
                        color = StatusRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        hasError = true
                    } else {
                        val min = (minQtyText.toIntOrNull() ?: 1).coerceAtLeast(0)
                        val cur = (currentQtyText.toIntOrNull() ?: min).coerceAtLeast(0)
                        onConfirm(name.trim(), selectedCategory, min, cur, unit.trim().ifEmpty { "uds" })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AjCashGreen),
                modifier = Modifier.testTag("confirm_add_item_button")
            ) {
                Text("Crear Objeto y Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
