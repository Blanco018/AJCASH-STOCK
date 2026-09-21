package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.AjCashGreen
import com.example.ui.theme.StatusRed

/**
 * Pantalla de Bloqueo a Pantalla Completa mostrada cuando el periodo de prueba Demo de 24 horas caduca.
 *
 * Bloquea el acceso a todas las funciones del inventario, vehículos y sincronización,
 * ofreciendo un diseño táctico oscuro de alto contraste y la acción de salida obligatoria.
 */
@Composable
fun DemoExpiredLockScreen(
    expirationDateFormatted: String,
    onCloseApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    // El botón Atrás del sistema también cierra la aplicación
    BackHandler {
        onCloseApp()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Fondo oscuro absoluto
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Cabecera de Marca
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1E1E),
                border = BorderStroke(1.dp, Color(0xFF383838)),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(StatusRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AJ CA\$H · GESTIÓN DE STOCK TÉCNICO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            // Círculo de Alerta / Bloqueo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color(0xFF2A1414), CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(Color(0xFF3D1616), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Demo Expirada",
                        tint = StatusRed,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título Requerido
            Text(
                text = "DEMO DE PRUEBA EXPIRADA",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp,
                modifier = Modifier.testTag("demo_expired_title")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mensaje Requerido
            Text(
                text = "El periodo de prueba de 24 horas de AJCASH-STOCK ha finalizado. Contacta con el desarrollador.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tarjeta de Información de Soporte y Licencia
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E1E)
                ),
                border = BorderStroke(1.dp, Color(0xFF333333)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Versión Instalada",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "v2.2-DEMO1D",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AjCashGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Caducidad registrada",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = expirationDateFormatted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFF87171)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF2E2E2E))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = AjCashGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Soporte: 976 29 88 50 · AJ CASH Zaragoza",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Destacado: "Cerrar Aplicación"
            Button(
                onClick = onCloseApp,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626), // Rojo llamativo para acción obligatoria
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_close_expired_app")
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Cerrar Aplicación",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
