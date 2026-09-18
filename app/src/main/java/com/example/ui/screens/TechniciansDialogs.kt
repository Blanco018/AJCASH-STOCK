package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Technician
import com.example.data.model.Vehicle
import com.example.ui.theme.AjCashGreen
import com.example.ui.theme.AjCashGreenDark
import com.example.ui.theme.AjCashGreenLight
import com.example.ui.theme.AjCashGreenSubtle
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMedium
import com.example.ui.theme.StatusRed

/**
 * Diálogo interactivo para el Gestor de Técnicos de Guardia.
 * Permite registrar nuevos técnicos y eliminar los existentes, requiriendo confirmación explícita.
 */
@Composable
fun TechniciansManagerDialog(
    technicians: List<Technician>,
    onAddTechnician: (name: String, number: String) -> Unit,
    onDeleteTechnician: (Technician) -> Unit,
    onDismiss: () -> Unit
) {
    var newTechName by remember { mutableStateOf("") }
    var newTechNumber by remember { mutableStateOf("") }
    var techToDelete by remember { mutableStateOf<Technician?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Diálogo emergente de confirmación obligatoria para eliminar
    if (showDeleteConfirmDialog && techToDelete != null) {
        val target = techToDelete!!
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmDialog = false
                techToDelete = null
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFFEE2E2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta de eliminación",
                        tint = StatusRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Confirmar Eliminación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SlateDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar al técnico ${target.name} (Nº ${target.number})?",
                        fontSize = 14.sp,
                        color = SlateMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "El técnico será retirado del listado oficial de guardia y no aparecerá en los selectores de vehículos.",
                        fontSize = 12.sp,
                        color = SlateLight
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTechnician(target)
                        showDeleteConfirmDialog = false
                        techToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                    modifier = Modifier.testTag("confirm_delete_tech_button")
                ) {
                    Text("Eliminar Técnico", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        techToDelete = null
                    },
                    modifier = Modifier.testTag("cancel_delete_tech_button")
                ) {
                    Text("Cancelar", color = SlateMedium)
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(AjCashGreenLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = AjCashGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "GESTOR DE TÉCNICOS",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = AjCashGreen
                    )
                    Text(
                        text = "Plantilla de Guardia AJ CA\$H (${technicians.size} activos)",
                        fontSize = 12.sp,
                        color = SlateLight
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Formulario de Alta de Nuevo Técnico
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AjCashGreenSubtle),
                    border = BorderStroke(1.dp, AjCashGreenLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Añadir Nuevo Técnico",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AjCashGreenDark
                        )

                        OutlinedTextField(
                            value = newTechName,
                            onValueChange = { newTechName = it.uppercase() },
                            label = { Text("Nombre y Apellidos *") },
                            placeholder = { Text("Ej: PABLO BLANCO") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AjCashGreen,
                                focusedLabelColor = AjCashGreen
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_tech_name_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newTechNumber,
                                onValueChange = {
                                    if (it.length <= 4) newTechNumber = it.filter { char -> char.isDigit() }
                                },
                                label = { Text("Nº Técnico *") },
                                placeholder = { Text("Ej: 16") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (newTechName.isNotBlank() && newTechNumber.isNotBlank()) {
                                            onAddTechnician(newTechName, newTechNumber)
                                            newTechName = ""
                                            newTechNumber = ""
                                        }
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AjCashGreen,
                                    focusedLabelColor = AjCashGreen
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("new_tech_number_input")
                            )

                            Button(
                                onClick = {
                                    if (newTechName.isNotBlank() && newTechNumber.isNotBlank()) {
                                        onAddTechnician(newTechName, newTechNumber)
                                        newTechName = ""
                                        newTechNumber = ""
                                    }
                                },
                                enabled = newTechName.isNotBlank() && newTechNumber.isNotBlank(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AjCashGreen),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("submit_add_tech_button")
                            ) {
                                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Añadir", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                HorizontalDivider(color = SlateBorder)

                // Listado de Técnicos Existentes
                Text(
                    text = "Técnicos Registrados",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SlateDark
                )

                if (technicians.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay técnicos registrados. Añade uno arriba.",
                            color = SlateLight,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(technicians, key = { it.id }) { tech ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, SlateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Badge con número
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(AjCashGreenLight, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "#${tech.number}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                color = AjCashGreenDark
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = tech.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = SlateDark,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Técnico de Guardia",
                                                fontSize = 11.sp,
                                                color = SlateLight
                                            )
                                        }
                                    }

                                    // Botón de eliminar con confirmación obligatoria
                                    IconButton(
                                        onClick = {
                                            techToDelete = tech
                                            showDeleteConfirmDialog = true
                                        },
                                        modifier = Modifier.testTag("delete_tech_button_${tech.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Eliminar a ${tech.name}",
                                            tint = StatusRed.copy(alpha = 0.8f),
                                            modifier = Modifier.size(20.dp)
                                        )
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
                colors = ButtonDefaults.buttonColors(containerColor = AjCashGreen),
                modifier = Modifier.testTag("close_technicians_manager_button")
            ) {
                Text("Listo", fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Diálogo de selección ágil de Técnico al abrir un vehículo.
 * Contiene un menú desplegable (ExposedDropdownMenuBox) con los técnicos registrados
 * y un acceso directo para abrir el Gestor si se requiere dar de alta a otro técnico.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicianSelectionDialog(
    vehicle: Vehicle,
    technicians: List<Technician>,
    selectedTechnician: Technician?,
    onTechnicianSelected: (Technician) -> Unit,
    onConfirmSelection: (Technician) -> Unit,
    onOpenManager: () -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentChoice by remember(selectedTechnician, technicians) {
        mutableStateOf(selectedTechnician ?: technicians.firstOrNull())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(AjCashGreenLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = null,
                            tint = AjCashGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TÉCNICO DE GUARDIA",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = AjCashGreen
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Asignación para ${vehicle.name} · Matrícula: ${vehicle.plate.replace("-", "")}",
                    fontSize = 12.sp,
                    color = SlateLight
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Selecciona el técnico responsable que registrará las revisiones y movimientos de stock en este turno:",
                    fontSize = 13.sp,
                    color = SlateDark
                )

                if (technicians.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "No hay técnicos disponibles en la lista.",
                                fontWeight = FontWeight.Bold,
                                color = StatusRed,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pulsa en el botón inferior para registrar al primer técnico de guardia.",
                                color = SlateMedium,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    // Selector Desplegable (ExposedDropdownMenuBox)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = currentChoice?.let { "${it.name} (Nº ${it.number})" } ?: "Seleccionar técnico...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Técnico Asignado") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = AjCashGreen
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AjCashGreen,
                                focusedLabelColor = AjCashGreen
                            ),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                .fillMaxWidth()
                                .testTag("technician_dropdown_selector")
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            technicians.forEach { tech ->
                                val isSelected = currentChoice?.id == tech.id
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(26.dp)
                                                        .background(
                                                            if (isSelected) AjCashGreen else AjCashGreenLight,
                                                            CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "#${tech.number}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else AjCashGreenDark
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = tech.name,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) AjCashGreen else SlateDark
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Seleccionado",
                                                    tint = AjCashGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        currentChoice = tech
                                        onTechnicianSelected(tech)
                                        expanded = false
                                    },
                                    modifier = Modifier.testTag("technician_option_${tech.id}")
                                )
                            }
                        }
                    }
                }

                // Botón de acceso directo al Gestor de Técnicos
                OutlinedButton(
                    onClick = onOpenManager,
                    border = BorderStroke(1.dp, AjCashGreen),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AjCashGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_technicians_manager_from_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Gestor de Técnicos (Añadir / Eliminar)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    currentChoice?.let {
                        onConfirmSelection(it)
                    }
                },
                enabled = currentChoice != null,
                colors = ButtonDefaults.buttonColors(containerColor = AjCashGreen),
                modifier = Modifier.testTag("submit_tech_auth_button")
            ) {
                Text("Acceder al Inventario", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_tech_auth_button")
            ) {
                Text("Cancelar", color = SlateMedium)
            }
        }
    )
}
