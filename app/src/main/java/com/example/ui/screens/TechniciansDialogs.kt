package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.border
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Technician
import com.example.data.model.Vehicle
import com.example.ui.theme.AjCashGreen
import com.example.ui.theme.AjCashGreenDark
import com.example.ui.theme.AjCashGreenLight
import com.example.ui.theme.AjCashGreenSubtle
import com.example.ui.theme.GreenSlate700
import com.example.ui.theme.GreenSlate800
import com.example.ui.theme.GreenSlate900
import com.example.ui.theme.GreenSlateAccent
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMedium
import com.example.ui.theme.StatusRed

/**
 * Diálogo interactivo para el Gestor de Técnicos de Guardia.
 * Permite registrar nuevos técnicos y eliminar los existentes, requiriendo confirmación explícita.
 */
@OptIn(ExperimentalComposeUiApi::class)
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
            containerColor = GreenSlate900,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(StatusRed.copy(alpha = 0.18f), CircleShape)
                        .border(1.dp, StatusRed.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta de eliminación",
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Confirmar Eliminación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar al técnico ${target.name} (Nº ${target.number})?",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "El técnico será retirado del listado oficial de guardia y no aparecerá en los selectores de vehículos.",
                        fontSize = 12.sp,
                        color = GreenSlateAccent.copy(alpha = 0.8f)
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
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_delete_tech_button")
                ) {
                    Text("Eliminar Técnico", fontWeight = FontWeight.Bold, color = Color.White)
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
                    Text("Cancelar", color = Color.White.copy(alpha = 0.75f))
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GreenSlate900,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .semantics { testTagsAsResourceId = true },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(GreenSlate800, CircleShape)
                        .border(1.dp, GreenSlate700, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = GreenSlateAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "GESTOR DE TÉCNICOS",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = GreenSlateAccent,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Plantilla de Guardia AJ CA\$H (${technicians.size} activos)",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Formulario de Alta de Nuevo Técnico - Recuadro Verde Pizarra Oscuro
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenSlate800),
                    border = BorderStroke(1.dp, GreenSlate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = GreenSlateAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Añadir Nuevo Técnico",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GreenSlateAccent
                            )
                        }

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
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = GreenSlate900,
                                unfocusedContainerColor = GreenSlate900,
                                focusedBorderColor = GreenSlateAccent,
                                unfocusedBorderColor = GreenSlate700,
                                focusedLabelColor = GreenSlateAccent,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                                focusedPlaceholderColor = Color.White.copy(alpha = 0.4f),
                                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                                cursorColor = GreenSlateAccent
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_tech_name_input")
                        )

                        OutlinedTextField(
                            value = newTechNumber,
                            onValueChange = {
                                newTechNumber = it.filter { char -> char.isDigit() }.take(4)
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
                                    val nameToAdd = newTechName.trim().ifBlank { "PABLO BLANCO" }
                                    val numToAdd = newTechNumber.trim().ifBlank { "16" }
                                    onAddTechnician(nameToAdd, numToAdd)
                                    newTechName = ""
                                    newTechNumber = ""
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = GreenSlate900,
                                unfocusedContainerColor = GreenSlate900,
                                focusedBorderColor = GreenSlateAccent,
                                unfocusedBorderColor = GreenSlate700,
                                focusedLabelColor = GreenSlateAccent,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                                focusedPlaceholderColor = Color.White.copy(alpha = 0.4f),
                                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                                cursorColor = GreenSlateAccent
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_tech_number_input")
                        )

                        Button(
                            onClick = {
                                val nameToAdd = newTechName.trim().ifBlank { "PABLO BLANCO" }
                                val numToAdd = newTechNumber.trim().ifBlank { "16" }
                                onAddTechnician(nameToAdd, numToAdd)
                                newTechName = ""
                                newTechNumber = ""
                            },
                            enabled = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AjCashGreen,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_add_tech_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Añadir", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                HorizontalDivider(color = GreenSlate700.copy(alpha = 0.8f))

                // Listado de Técnicos Existentes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TÉCNICOS REGISTRADOS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp,
                        color = GreenSlateAccent
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenSlate700,
                        border = BorderStroke(1.dp, GreenSlateAccent.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "${technicians.size} activos",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (technicians.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenSlate800,
                        border = BorderStroke(1.dp, GreenSlate700),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(GreenSlate700, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = GreenSlateAccent.copy(alpha = 0.6f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No hay técnicos registrados",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Añade técnicos de guardia usando el formulario superior.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.65f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(technicians, key = { it.id }) { tech ->
                            // Recuadros de técnicos en Verde Oscuro Elegante (¡Cero blanco!)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GreenSlate800,
                                border = BorderStroke(1.dp, GreenSlate700),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Badge con número en verde brillante
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(GreenSlate700, CircleShape)
                                                .border(1.dp, GreenSlateAccent.copy(alpha = 0.5f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "#${tech.number}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                color = GreenSlateAccent
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = tech.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Técnico de Guardia",
                                                fontSize = 11.sp,
                                                color = GreenSlateAccent.copy(alpha = 0.7f)
                                            )
                                        }
                                    }

                                    // Botón de eliminar con confirmación obligatoria
                                    Surface(
                                        shape = CircleShape,
                                        color = StatusRed.copy(alpha = 0.14f),
                                        border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.35f)),
                                        onClick = {
                                            techToDelete = tech
                                            showDeleteConfirmDialog = true
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("delete_tech_button_${tech.id}")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Eliminar a ${tech.name}",
                                                tint = Color(0xFFF87171),
                                                modifier = Modifier.size(18.dp)
                                            )
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
                colors = ButtonDefaults.buttonColors(containerColor = AjCashGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("close_technicians_manager_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Listo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            }
        }
    )
}

/**
 * Diálogo de selección ágil de Técnico al abrir un vehículo.
 * Contiene un menú desplegable (ExposedDropdownMenuBox) con los técnicos registrados
 * y un acceso directo para abrir el Gestor si se requiere dar de alta a otro técnico.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        containerColor = GreenSlate900,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GreenSlate800, CircleShape)
                            .border(1.dp, GreenSlate700, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = null,
                            tint = GreenSlateAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "TÉCNICO DE GUARDIA",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = GreenSlateAccent,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Asignación para ${vehicle.name} · Matrícula: ${vehicle.plate.replace("-", "")}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Selecciona el técnico responsable que registrará las revisiones y movimientos de stock en este turno:",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )

                if (technicians.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GreenSlate800),
                        border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "No hay técnicos disponibles en la lista.",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF87171),
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pulsa en el botón inferior para registrar al primer técnico de guardia.",
                                color = Color.White.copy(alpha = 0.7f),
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
                                    tint = GreenSlateAccent
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = GreenSlate800,
                                unfocusedContainerColor = GreenSlate800,
                                focusedBorderColor = GreenSlateAccent,
                                unfocusedBorderColor = GreenSlate700,
                                focusedLabelColor = GreenSlateAccent,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                .fillMaxWidth()
                                .testTag("technician_dropdown_selector")
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = GreenSlate800
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
                                                        .size(28.dp)
                                                        .background(
                                                            if (isSelected) AjCashGreen else GreenSlate700,
                                                            CircleShape
                                                        )
                                                        .border(
                                                            1.dp,
                                                            if (isSelected) GreenSlateAccent else GreenSlate700,
                                                            CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "#${tech.number}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else GreenSlateAccent
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = tech.name,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) GreenSlateAccent else Color.White
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Seleccionado",
                                                    tint = GreenSlateAccent,
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
                    border = BorderStroke(1.dp, GreenSlateAccent.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenSlateAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_technicians_manager_from_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
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
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("submit_tech_auth_button")
            ) {
                Text("Acceder al Inventario", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_tech_auth_button")
            ) {
                Text("Cancelar", color = Color.White.copy(alpha = 0.75f))
            }
        }
    )
}
