package com.hiaashuu.dialogmaker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val PRESET_COLORS = listOf(

    0xFFFF1744L, 0xFFE53935L, 0xFFB71C1CL, 0xFFFF5252L,

    0xFFE91E63L, 0xFFAD1457L, 0xFFF48FB1L, 0xFFFF80ABL,

    0xFF9C27B0L, 0xFF6A1B9AL, 0xFFCE93D8L, 0xFF7C4DFFL,

    0xFF673AB7L, 0xFF4527A0L, 0xFFB39DBAL, 0xFF651FFFL,

    0xFF3F51B5L, 0xFF1A237EL, 0xFF4359A9L, 0xFF2196F3L,
    0xFF0D47A1L, 0xFF1565C0L, 0xFF42A5F5L, 0xFF82B1FFL,

    0xFF00BCD4L, 0xFF006064L, 0xFF26C6DAL, 0xFF009688L,

    0xFF4CAF50L, 0xFF1B5E20L, 0xFF81C784L, 0xFF00E676L,

    0xFFCDDC39L, 0xFFFFEB3BL, 0xFFFFC107L, 0xFFFFD740L,

    0xFFFF9800L, 0xFFE65100L, 0xFFFFCC02L, 0xFFFF6D00L,

    0xFFFF5722L, 0xFFBF360CL, 0xFF795548L, 0xFF4E342EL,

    0xFF9E9E9EL, 0xFF616161L, 0xFF212121L, 0xFF000000L,
    0xFFFFFFFFL, 0xFFF5F5F5L, 0xFFECEFF1L, 0xFF00000000L,
)

@Composable
fun ColorPickerDialog(
    title: String,
    initialColor: Long,
    showAlphaSlider: Boolean = false,
    onColorSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedColorLong by remember { mutableStateOf(initialColor) }
    var hexInput by remember { mutableStateOf(java.lang.Long.toHexString(initialColor).uppercase().padStart(8, '0')) }
    var hexError by remember { mutableStateOf(false) }
    var alphaValue by remember { mutableFloatStateOf(((initialColor shr 24) and 0xFF).toFloat() / 255f) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(selectedColorLong))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Current",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "#${java.lang.Long.toHexString(selectedColorLong).uppercase().padStart(8, '0')}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(PRESET_COLORS) { colorLong ->
                        val isSelected = colorLong == selectedColorLong
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(colorLong))
                                .border(
                                    width = if (isSelected) 2.dp else 0.5.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedColorLong = colorLong
                                    hexInput = java.lang.Long.toHexString(colorLong).uppercase().padStart(8, '0')
                                    hexError = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = if (colorLong == 0xFFFFFFFFL) Color.Black else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (showAlphaSlider) {
                    Text(
                        text = "Opacity: ${(alphaValue * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Slider(
                        value = alphaValue,
                        onValueChange = { alpha ->
                            alphaValue = alpha
                            val rgb = selectedColorLong and 0x00FFFFFFL
                            val newAlpha = (alpha * 255).toLong()
                            selectedColorLong = (newAlpha shl 24) or rgb
                            hexInput = java.lang.Long.toHexString(selectedColorLong).uppercase().padStart(8, '0')
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { input ->
                        val cleaned = input.replace("#", "").uppercase().take(8)
                        hexInput = cleaned
                        hexError = false
                        if (cleaned.length == 6 || cleaned.length == 8) {
                            try {
                                val parsed = if (cleaned.length == 6) {
                                    "FF$cleaned".toLong(16)
                                } else {
                                    cleaned.toLong(16)
                                }
                                selectedColorLong = parsed
                            } catch (e: NumberFormatException) {
                                hexError = true
                            }
                        }
                    },
                    label = { Text("Hex Color (AARRGGBB)") },
                    placeholder = { Text("FFRRGGBB") },
                    isError = hexError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    prefix = { Text("#") },
                    supportingText = if (hexError) {
                        { Text("Invalid hex color") }
                    } else null
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onColorSelected(selectedColorLong)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}