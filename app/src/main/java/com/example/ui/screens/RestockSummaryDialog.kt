package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VehicleStockSummary

@Composable
fun RestockSummaryDialog(
    vehiclesSummaries: List<VehicleStockSummary>,
    onDismiss: () -> Unit
) {
    val vehiclesWithDeficits = vehiclesSummaries.filter { it.deficientItems.isNotEmpty() }
    val totalMissingItemsCount = vehiclesWithDeficits.sumOf { it.deficientItems.size }
    val totalMissingUnits = vehiclesWithDeficits.sumOf { summary ->
        summary.deficientItems.sumOf { it.missingQuantity }
    }

    val containerBg = Color(0xFF1E1E1E)
    val cardItemBg = Color(0xFF262626)
    val brightGreen = Color(0xFF4CAF50)
    val alertRed = Color(0xFFEF4444)
    val alertOrange = Color(0xFFF97316)
    val borderDark = Color(0xFF383838)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = containerBg,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .testTag("restock_summary_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (vehiclesWithDeficits.isNotEmpty()) alertRed.copy(alpha = 0.16f)
                                else brightGreen.copy(alpha = 0.16f),
                                CircleShape
                            )
                            .border(
                                1.dp,
                                if (vehiclesWithDeficits.isNotEmpty()) alertRed.copy(alpha = 0.45f)
                                else brightGreen.copy(alpha = 0.45f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (vehiclesWithDeficits.isNotEmpty()) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (vehiclesWithDeficits.isNotEmpty()) alertRed else brightGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Materiales a Reponer",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 0.2.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (vehiclesWithDeficits.isNotEmpty()) {
                                "$totalMissingUnits uds faltantes en ${vehiclesWithDeficits.size} vehículo${if (vehiclesWithDeficits.size > 1) "s" else ""}"
                            } else {
                                "Flota completa al 100%"
                            },
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Badge contador total
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (vehiclesWithDeficits.isNotEmpty()) alertRed.copy(alpha = 0.2f) else brightGreen.copy(alpha = 0.2f),
                    border = BorderStroke(
                        1.dp,
                        if (vehiclesWithDeficits.isNotEmpty()) alertRed.copy(alpha = 0.5f) else brightGreen.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = if (vehiclesWithDeficits.isNotEmpty()) "$totalMissingItemsCount ítems" else "0 faltas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (vehiclesWithDeficits.isNotEmpty()) Color(0xFFFCA5A5) else Color(0xFF86EFAC),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (vehiclesWithDeficits.isEmpty()) {
                    // Estado de éxito cuando no hay faltas
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardItemBg),
                        border = BorderStroke(1.dp, brightGreen.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 28.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(brightGreen.copy(alpha = 0.15f), CircleShape)
                                    .border(1.dp, brightGreen.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Inventario completo",
                                    tint = brightGreen,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Todo el inventario está completo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Todos los vehículos de la flota cuentan con el 100% de las existencias mínimas para guardia técnica.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else {
                    // Listado agrupado por vehículo
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(vehiclesWithDeficits, key = { it.vehicle.id }) { summary ->
                            val isVan = summary.vehicle.type.contains("Furgoneta", ignoreCase = true)
                            val cleanPlate = summary.vehicle.plate.replace("-", "")
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardItemBg),
                                border = BorderStroke(1.dp, borderDark),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    // Cabecera del vehículo
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = if (isVan) Icons.Default.LocalShipping else Icons.Default.DirectionsCar,
                                                contentDescription = null,
                                                tint = brightGreen,
                                                modifier = Modifier.size(17.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${summary.vehicle.name} · ${summary.vehicle.model} ($cleanPlate)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = brightGreen
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = alertRed.copy(alpha = 0.15f),
                                            border = BorderStroke(1.dp, alertRed.copy(alpha = 0.35f))
                                        ) {
                                            Text(
                                                text = "${summary.deficientItems.size} déficit",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFCA5A5),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = borderDark.copy(alpha = 0.7f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Lista de ítems faltantes para este vehículo
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        summary.deficientItems.forEach { item ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .background(alertOrange, CircleShape)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = item.name,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = Color.White
                                                    )
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = alertRed.copy(alpha = 0.2f),
                                                    border = BorderStroke(1.dp, alertRed.copy(alpha = 0.35f))
                                                ) {
                                                    Text(
                                                        text = "Faltan: ${item.missingQuantity} ${item.unit}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.5.sp,
                                                        color = Color(0xFFFCA5A5),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = brightGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("close_restock_summary_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Entendido",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    )
}
