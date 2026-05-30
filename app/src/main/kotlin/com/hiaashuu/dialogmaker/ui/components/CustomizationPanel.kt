package com.hiaashuu.dialogmaker.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BorderAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiaashuu.dialogmaker.data.AnimationType
import com.hiaashuu.dialogmaker.data.ButtonArrangement
import com.hiaashuu.dialogmaker.data.DialogConfig
import com.hiaashuu.dialogmaker.data.DialogType
import com.hiaashuu.dialogmaker.data.FontWeightOption
import com.hiaashuu.dialogmaker.data.IconStyle
import com.hiaashuu.dialogmaker.data.TextAlignOption

data class ColorPickerTarget(
    val title: String,
    val currentColor: Long,
    val showAlpha: Boolean,
    val apply: (DialogConfig, Long) -> DialogConfig
)

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(
            width = 0.7.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun PanelTextField(
    label: String,
    value: String,
    minLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 12.sp) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(10.dp),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun SliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun <T> ChipRow(
    items: List<T>,
    selected: T,
    labelFn: (T) -> String,
    onSelect: (T) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            FilterChip(
                selected = selected == item,
                onClick = { onSelect(item) },
                label = { Text(text = labelFn(item), fontSize = 11.sp) },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
fun ColorRow(
    label: String,
    colorLong: Long,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(colorLong))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun SwitchRow(
    label: String,
    description: String = "",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun CustomizationPanel(
    config: DialogConfig,
    onUpdate: (DialogConfig) -> Unit
) {
    var expandedSection by remember { mutableStateOf("content") }
    var showColorPicker by remember { mutableStateOf<ColorPickerTarget?>(null) }

    val iconPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            onUpdate(config.copy(iconUri = uri.toString()))
        }
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            onUpdate(config.copy(imageUri = uri.toString()))
        }
    }

    showColorPicker?.let { target ->
        ColorPickerDialog(
            title = target.title,
            initialColor = target.currentColor,
            showAlphaSlider = target.showAlpha,
            onColorSelected = { newColor ->
                onUpdate(target.apply(config, newColor))
            },
            onDismiss = { showColorPicker = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        SectionCard(
            title = "Dialog Type",
            icon = Icons.Filled.Star,
            isExpanded = expandedSection == "type",
            onToggle = { expandedSection = if (expandedSection == "type") "" else "type" }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DialogType.entries.forEach { type ->
                    FilterChip(
                        selected = config.dialogType == type,
                        onClick = { onUpdate(config.copy(dialogType = type)) },
                        label = { Text(text = "${type.emoji} ${type.label}", fontSize = 11.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        SectionCard(
            title = "Content",
            icon = Icons.Filled.Edit,
            isExpanded = expandedSection == "content",
            onToggle = { expandedSection = if (expandedSection == "content") "" else "content" }
        ) {
            PanelTextField(label = "Title", value = config.title) {
                onUpdate(config.copy(title = it))
            }
            Spacer(modifier = Modifier.height(8.dp))
            PanelTextField(label = "Message", value = config.message, minLines = 2) {
                onUpdate(config.copy(message = it))
            }
            Spacer(modifier = Modifier.height(8.dp))
            PanelTextField(label = "Positive Button Text", value = config.positiveButtonText) {
                onUpdate(config.copy(positiveButtonText = it))
            }
            Spacer(modifier = Modifier.height(8.dp))
            PanelTextField(label = "Positive Button Link (Optional)", value = config.positiveButtonLink) {
                onUpdate(config.copy(positiveButtonLink = it))
            }
            Spacer(modifier = Modifier.height(8.dp))
            PanelTextField(label = "Negative Button Text", value = config.negativeButtonText) {
                onUpdate(config.copy(negativeButtonText = it))
            }
            Spacer(modifier = Modifier.height(8.dp))
            PanelTextField(label = "Negative Button Link (Optional)", value = config.negativeButtonLink) {
                onUpdate(config.copy(negativeButtonLink = it))
            }
            Spacer(modifier = Modifier.height(8.dp))
            PanelTextField(
                label = "Neutral Button Text (optional)",
                value = config.neutralButtonText
            ) { onUpdate(config.copy(neutralButtonText = it)) }
            Spacer(modifier = Modifier.height(8.dp))
            PanelTextField(label = "Neutral Button Link (Optional)", value = config.neutralButtonLink) {
                onUpdate(config.copy(neutralButtonLink = it))
            }

            when (config.dialogType) {
                DialogType.RATING -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelTextField(label = "Rating Label", value = config.ratingLabel) {
                        onUpdate(config.copy(ratingLabel = it))
                    }
                }
                DialogType.INPUT -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelTextField(label = "Input Label", value = config.inputLabel) {
                        onUpdate(config.copy(inputLabel = it))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelTextField(label = "Input Hint", value = config.inputHint) {
                        onUpdate(config.copy(inputHint = it))
                    }
                }
                DialogType.LOADING -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelTextField(label = "Loading Text", value = config.loadingText) {
                        onUpdate(config.copy(loadingText = it))
                    }
                }
                DialogType.TERMS -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelTextField(label = "Terms Text", value = config.termsText, minLines = 3) {
                        onUpdate(config.copy(termsText = it))
                    }
                }
                DialogType.MULTI_ACTION -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelTextField(
                        label = "Action Items (one per line)",
                        value = config.multiActionItems,
                        minLines = 3
                    ) { onUpdate(config.copy(multiActionItems = it)) }
                }
                DialogType.DONT_SHOW_AGAIN -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelTextField(label = "Don't Show Text", value = config.dontShowAgainText) {
                        onUpdate(config.copy(dontShowAgainText = it))
                    }
                }
                DialogType.SWITCH_DIALOG -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelTextField(label = "Switch Label", value = config.switchText) {
                        onUpdate(config.copy(switchText = it))
                    }
                }
                DialogType.IOS_STYLE -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    PanelTextField(
                        label = "Destructive Action Text",
                        value = config.iosDestructiveText
                    ) { onUpdate(config.copy(iosDestructiveText = it)) }
                }
                else -> {}
            }
        }

        SectionCard(
            title = "Colors",
            icon = Icons.Filled.Palette,
            isExpanded = expandedSection == "colors",
            onToggle = { expandedSection = if (expandedSection == "colors") "" else "colors" }
        ) {
            ColorRow(
                label = "Dialog Background",
                colorLong = config.dialogBgColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Dialog Background",
                        currentColor = config.dialogBgColor,
                        showAlpha = false,
                        apply = { c, v -> c.copy(dialogBgColor = v) }
                    )
                }
            )
            ColorRow(
                label = "Overlay / Dim Color",
                colorLong = config.overlayColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Overlay Color",
                        currentColor = config.overlayColor,
                        showAlpha = true,
                        apply = { c, v -> c.copy(overlayColor = v) }
                    )
                }
            )
            ColorRow(
                label = "Title Text",
                colorLong = config.titleColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Title Color",
                        currentColor = config.titleColor,
                        showAlpha = false,
                        apply = { c, v -> c.copy(titleColor = v) }
                    )
                }
            )
            ColorRow(
                label = "Message Text",
                colorLong = config.messageColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Message Color",
                        currentColor = config.messageColor,
                        showAlpha = false,
                        apply = { c, v -> c.copy(messageColor = v) }
                    )
                }
            )
            ColorRow(
                label = "Positive Button Background",
                colorLong = config.posButtonBgColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Positive Btn BG",
                        currentColor = config.posButtonBgColor,
                        showAlpha = true,
                        apply = { c, v -> c.copy(posButtonBgColor = v) }
                    )
                }
            )
            ColorRow(
                label = "Positive Button Text",
                colorLong = config.posButtonTextColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Positive Btn Text",
                        currentColor = config.posButtonTextColor,
                        showAlpha = false,
                        apply = { c, v -> c.copy(posButtonTextColor = v) }
                    )
                }
            )
            ColorRow(
                label = "Negative Button Background",
                colorLong = config.negButtonBgColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Negative Btn BG",
                        currentColor = config.negButtonBgColor,
                        showAlpha = true,
                        apply = { c, v -> c.copy(negButtonBgColor = v) }
                    )
                }
            )
            ColorRow(
                label = "Negative Button Text",
                colorLong = config.negButtonTextColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Negative Btn Text",
                        currentColor = config.negButtonTextColor,
                        showAlpha = false,
                        apply = { c, v -> c.copy(negButtonTextColor = v) }
                    )
                }
            )
            ColorRow(
                label = "Icon Background",
                colorLong = config.iconBgColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Icon Background",
                        currentColor = config.iconBgColor,
                        showAlpha = true,
                        apply = { c, v -> c.copy(iconBgColor = v) }
                    )
                }
            )
            ColorRow(
                label = "Icon Tint",
                colorLong = config.iconColor,
                onClick = {
                    showColorPicker = ColorPickerTarget(
                        title = "Icon Color",
                        currentColor = config.iconColor,
                        showAlpha = false,
                        apply = { c, v -> c.copy(iconColor = v) }
                    )
                }
            )
        }

        SectionCard(
            title = "Sizes",
            icon = Icons.Filled.TextFields,
            isExpanded = expandedSection == "sizes",
            onToggle = { expandedSection = if (expandedSection == "sizes") "" else "sizes" }
        ) {
            SliderRow(
                label = "Dialog Width",
                value = config.dialogWidthFraction,
                valueRange = 0.5f..1.0f,
                displayValue = "${(config.dialogWidthFraction * 100).toInt()}%"
            ) { onUpdate(config.copy(dialogWidthFraction = it)) }

            SliderRow(
                label = "Corner Radius",
                value = config.cornerRadius,
                valueRange = 0f..40f,
                displayValue = "${config.cornerRadius.toInt()}dp"
            ) { onUpdate(config.copy(cornerRadius = it)) }

            SliderRow(
                label = "Title Font Size",
                value = config.titleFontSize,
                valueRange = 10f..32f,
                displayValue = "${config.titleFontSize.toInt()}sp"
            ) { onUpdate(config.copy(titleFontSize = it)) }

            SliderRow(
                label = "Message Font Size",
                value = config.messageFontSize,
                valueRange = 9f..24f,
                displayValue = "${config.messageFontSize.toInt()}sp"
            ) { onUpdate(config.copy(messageFontSize = it)) }

            SliderRow(
                label = "Button Font Size",
                value = config.buttonFontSize,
                valueRange = 10f..20f,
                displayValue = "${config.buttonFontSize.toInt()}sp"
            ) { onUpdate(config.copy(buttonFontSize = it)) }

            SliderRow(
                label = "Icon Size",
                value = config.iconSize,
                valueRange = 16f..72f,
                displayValue = "${config.iconSize.toInt()}dp"
            ) { onUpdate(config.copy(iconSize = it)) }

            SliderRow(
                label = "Icon BG Size",
                value = config.iconBgSize,
                valueRange = 32f..100f,
                displayValue = "${config.iconBgSize.toInt()}dp"
            ) { onUpdate(config.copy(iconBgSize = it)) }

            SliderRow(
                label = "Image Height",
                value = config.imageHeight,
                valueRange = 80f..280f,
                displayValue = "${config.imageHeight.toInt()}dp"
            ) { onUpdate(config.copy(imageHeight = it)) }

            SliderRow(
                label = "Image Corner Radius",
                value = config.imageCornerRadius,
                valueRange = 0f..32f,
                displayValue = "${config.imageCornerRadius.toInt()}dp"
            ) { onUpdate(config.copy(imageCornerRadius = it)) }

            SliderRow(
                label = "Button Height",
                value = config.buttonHeight,
                valueRange = 32f..64f,
                displayValue = "${config.buttonHeight.toInt()}dp"
            ) { onUpdate(config.copy(buttonHeight = it)) }

            SliderRow(
                label = "Button Corner Radius",
                value = config.buttonCornerRadius,
                valueRange = 0f..32f,
                displayValue = "${config.buttonCornerRadius.toInt()}dp"
            ) { onUpdate(config.copy(buttonCornerRadius = it)) }
        }

        SectionCard(
            title = "Spacing & Padding",
            icon = Icons.Filled.Tune,
            isExpanded = expandedSection == "spacing",
            onToggle = { expandedSection = if (expandedSection == "spacing") "" else "spacing" }
        ) {
            SliderRow(
                label = "Horizontal Padding",
                value = config.dialogPaddingH,
                valueRange = 8f..40f,
                displayValue = "${config.dialogPaddingH.toInt()}dp"
            ) { onUpdate(config.copy(dialogPaddingH = it)) }

            SliderRow(
                label = "Vertical Padding",
                value = config.dialogPaddingV,
                valueRange = 8f..48f,
                displayValue = "${config.dialogPaddingV.toInt()}dp"
            ) { onUpdate(config.copy(dialogPaddingV = it)) }

            SliderRow(
                label = "Content Spacing",
                value = config.contentSpacing,
                valueRange = 0f..24f,
                displayValue = "${config.contentSpacing.toInt()}dp"
            ) { onUpdate(config.copy(contentSpacing = it)) }

            SliderRow(
                label = "Button Spacing",
                value = config.buttonSpacing,
                valueRange = 0f..24f,
                displayValue = "${config.buttonSpacing.toInt()}dp"
            ) { onUpdate(config.copy(buttonSpacing = it)) }
        }

        SectionCard(
            title = "Typography",
            icon = Icons.Filled.TextFields,
            isExpanded = expandedSection == "typo",
            onToggle = { expandedSection = if (expandedSection == "typo") "" else "typo" }
        ) {
            Text(
                text = "Title Font Weight",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            ChipRow(
                items = FontWeightOption.entries,
                selected = config.titleFontWeight,
                labelFn = { it.label }
            ) { onUpdate(config.copy(titleFontWeight = it)) }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Message Font Weight",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            ChipRow(
                items = FontWeightOption.entries,
                selected = config.messageFontWeight,
                labelFn = { it.label }
            ) { onUpdate(config.copy(messageFontWeight = it)) }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Button Font Weight",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            ChipRow(
                items = FontWeightOption.entries,
                selected = config.buttonFontWeight,
                labelFn = { it.label }
            ) { onUpdate(config.copy(buttonFontWeight = it)) }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Title Alignment",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            ChipRow(
                items = TextAlignOption.entries,
                selected = config.titleAlignment,
                labelFn = { it.label }
            ) { onUpdate(config.copy(titleAlignment = it)) }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Message Alignment",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            ChipRow(
                items = TextAlignOption.entries,
                selected = config.messageAlignment,
                labelFn = { it.label }
            ) { onUpdate(config.copy(messageAlignment = it)) }

            Spacer(modifier = Modifier.height(8.dp))
            SwitchRow(
                label = "Title Italic",
                checked = config.titleItalic
            ) { onUpdate(config.copy(titleItalic = it)) }
            SwitchRow(
                label = "Message Italic",
                checked = config.messageItalic
            ) { onUpdate(config.copy(messageItalic = it)) }
        }

        SectionCard(
            title = "Strokes & Borders",
            icon = Icons.Filled.BorderAll,
            isExpanded = expandedSection == "strokes",
            onToggle = { expandedSection = if (expandedSection == "strokes") "" else "strokes" }
        ) {
            SliderRow(
                label = "Dialog Stroke Width",
                value = config.dialogStrokeWidth,
                valueRange = 0f..8f,
                displayValue = String.format("%.1fdp", config.dialogStrokeWidth)
            ) { onUpdate(config.copy(dialogStrokeWidth = it)) }

            if (config.dialogStrokeWidth > 0f) {
                ColorRow(
                    label = "Dialog Stroke Color",
                    colorLong = config.dialogStrokeColor,
                    onClick = {
                        showColorPicker = ColorPickerTarget(
                            title = "Dialog Stroke Color",
                            currentColor = config.dialogStrokeColor,
                            showAlpha = false,
                            apply = { c, v -> c.copy(dialogStrokeColor = v) }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            SliderRow(
                label = "Positive Button Stroke Width",
                value = config.posButtonStrokeWidth,
                valueRange = 0f..6f,
                displayValue = String.format("%.1fdp", config.posButtonStrokeWidth)
            ) { onUpdate(config.copy(posButtonStrokeWidth = it)) }

            if (config.posButtonStrokeWidth > 0f) {
                ColorRow(
                    label = "Positive Stroke Color",
                    colorLong = config.posButtonStrokeColor,
                    onClick = {
                        showColorPicker = ColorPickerTarget(
                            title = "Positive Button Stroke",
                            currentColor = config.posButtonStrokeColor,
                            showAlpha = false,
                            apply = { c, v -> c.copy(posButtonStrokeColor = v) }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            SliderRow(
                label = "Negative Button Stroke Width",
                value = config.negButtonStrokeWidth,
                valueRange = 0f..6f,
                displayValue = String.format("%.1fdp", config.negButtonStrokeWidth)
            ) { onUpdate(config.copy(negButtonStrokeWidth = it)) }

            if (config.negButtonStrokeWidth > 0f) {
                ColorRow(
                    label = "Negative Stroke Color",
                    colorLong = config.negButtonStrokeColor,
                    onClick = {
                        showColorPicker = ColorPickerTarget(
                            title = "Negative Button Stroke",
                            currentColor = config.negButtonStrokeColor,
                            showAlpha = false,
                            apply = { c, v -> c.copy(negButtonStrokeColor = v) }
                        )
                    }
                )
            }
        }

        SectionCard(
            title = "Buttons",
            icon = Icons.Filled.SmartButton,
            isExpanded = expandedSection == "buttons",
            onToggle = { expandedSection = if (expandedSection == "buttons") "" else "buttons" }
        ) {
            SwitchRow(
                label = "Show Positive Button",
                checked = config.showPositiveButton
            ) { onUpdate(config.copy(showPositiveButton = it)) }
            SwitchRow(
                label = "Show Negative Button",
                checked = config.showNegativeButton
            ) { onUpdate(config.copy(showNegativeButton = it)) }
            SwitchRow(
                label = "Show Neutral Button",
                checked = config.showNeutralButton
            ) { onUpdate(config.copy(showNeutralButton = it)) }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Button Arrangement",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            ChipRow(
                items = ButtonArrangement.entries,
                selected = config.buttonArrangement,
                labelFn = { it.label }
            ) { onUpdate(config.copy(buttonArrangement = it)) }

            if (config.showNeutralButton) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Neutral Button Colors",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ColorRow(
                    label = "Neutral Button Background",
                    colorLong = config.neuButtonBgColor,
                    onClick = {
                        showColorPicker = ColorPickerTarget(
                            title = "Neutral Btn BG",
                            currentColor = config.neuButtonBgColor,
                            showAlpha = true,
                            apply = { c, v -> c.copy(neuButtonBgColor = v) }
                        )
                    }
                )
                ColorRow(
                    label = "Neutral Button Text Color",
                    colorLong = config.neuButtonTextColor,
                    onClick = {
                        showColorPicker = ColorPickerTarget(
                            title = "Neutral Btn Text",
                            currentColor = config.neuButtonTextColor,
                            showAlpha = false,
                            apply = { c, v -> c.copy(neuButtonTextColor = v) }
                        )
                    }
                )
            }
        }

        SectionCard(
            title = "Icon & Image",
            icon = Icons.Filled.Image,
            isExpanded = expandedSection == "icon",
            onToggle = { expandedSection = if (expandedSection == "icon") "" else "icon" }
        ) {
            SwitchRow(
                label = "Show Icon",
                description = "Shows icon above title",
                checked = config.showIcon
            ) { onUpdate(config.copy(showIcon = it)) }

            if (config.showIcon) {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = { iconPicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick Custom Icon")
                }
                if (config.iconUri.isNotEmpty()) {
                    TextButton(onClick = { onUpdate(config.copy(iconUri = "")) }) { Text("Clear Custom Icon") }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Icon Style",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                ChipRow(
                    items = IconStyle.entries,
                    selected = config.iconStyle,
                    labelFn = { it.label }
                ) { onUpdate(config.copy(iconStyle = it)) }
            }

            Spacer(modifier = Modifier.height(4.dp))
            SwitchRow(
                label = "Show Image",
                description = "Shows a placeholder image banner",
                checked = config.showImage
            ) { onUpdate(config.copy(showImage = it)) }

            if (config.showImage) {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick Custom Image")
                }
                if (config.imageUri.isNotEmpty()) {
                    TextButton(onClick = { onUpdate(config.copy(imageUri = "")) }) { Text("Clear Custom Image") }
                }
            }
        }

        SectionCard(
            title = "Extra Features",
            icon = Icons.Filled.Info,
            isExpanded = expandedSection == "extra",
            onToggle = { expandedSection = if (expandedSection == "extra") "" else "extra" }
        ) {
            SwitchRow(
                label = "Show 'Don't Show Again'",
                description = "Adds a checkbox to skip showing again",
                checked = config.showDontShowAgain
            ) { onUpdate(config.copy(showDontShowAgain = it)) }
            SwitchRow(
                label = "Show Toggle Switch",
                description = "Adds an on/off switch inside the dialog",
                checked = config.showSwitch
            ) { onUpdate(config.copy(showSwitch = it)) }
        }

        SectionCard(
            title = "Behavior & Animation",
            icon = Icons.Filled.Tune,
            isExpanded = expandedSection == "behavior",
            onToggle = { expandedSection = if (expandedSection == "behavior") "" else "behavior" }
        ) {
            SwitchRow(
                label = "Cancelable",
                description = "Dismiss by tapping outside or pressing back",
                checked = config.isCancelable
            ) { onUpdate(config.copy(isCancelable = it)) }

            SliderRow(
                label = "Dim Amount",
                value = config.dimAmount,
                valueRange = 0f..1f,
                displayValue = "${(config.dimAmount * 100).toInt()}%"
            ) { onUpdate(config.copy(dimAmount = it)) }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Animation Type",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            ChipRow(
                items = AnimationType.entries,
                selected = config.animationType,
                labelFn = { it.label }
            ) { onUpdate(config.copy(animationType = it)) }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}