package com.hiaashuu.dialogmaker.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiaashuu.dialogmaker.data.ButtonArrangement
import com.hiaashuu.dialogmaker.data.DialogConfig
import com.hiaashuu.dialogmaker.data.DialogType
import com.hiaashuu.dialogmaker.data.IconStyle
import com.hiaashuu.dialogmaker.data.dialogBgComposeColor
import com.hiaashuu.dialogmaker.data.dialogStrokeComposeColor
import com.hiaashuu.dialogmaker.data.iconBgComposeColor
import com.hiaashuu.dialogmaker.data.iconComposeColor
import com.hiaashuu.dialogmaker.data.messageComposeColor
import com.hiaashuu.dialogmaker.data.negButtonBgComposeColor
import com.hiaashuu.dialogmaker.data.negButtonTextComposeColor
import com.hiaashuu.dialogmaker.data.neuButtonBgComposeColor
import com.hiaashuu.dialogmaker.data.neuButtonTextComposeColor
import com.hiaashuu.dialogmaker.data.overlayComposeColor
import com.hiaashuu.dialogmaker.data.posButtonBgComposeColor
import com.hiaashuu.dialogmaker.data.posButtonStrokeComposeColor
import com.hiaashuu.dialogmaker.data.negButtonStrokeComposeColor
import com.hiaashuu.dialogmaker.data.posButtonTextComposeColor
import com.hiaashuu.dialogmaker.data.titleComposeColor
import com.hiaashuu.dialogmaker.data.toFontWeight
import com.hiaashuu.dialogmaker.data.toTextAlign

@Composable
fun LiveDialogPreview(
    config: DialogConfig,
    isDarkBackground: Boolean = false,
    modifier: Modifier = Modifier
) {
    val overlayBg = if (isDarkBackground) Color(0xFF121524) else config.overlayComposeColor()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(overlayBg),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(270.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.03f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        when (config.dialogType) {
            DialogType.IOS_STYLE -> IosStylePreview(config)
            DialogType.LOADING -> LoadingPreview(config)
            DialogType.MULTI_ACTION -> MultiActionPreview(config)
            DialogType.TERMS -> TermsPreview(config)
            else -> StandardDialogPreview(config)
        }

        Text(
            text = "LIVE PREVIEW",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.4f),
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
        )
    }
}

@Composable
private fun StandardDialogPreview(config: DialogConfig) {
    val hasIcon = config.showIcon || config.dialogType in listOf(
        DialogType.INFO, DialogType.WARNING, DialogType.ERROR, DialogType.SUCCESS
    )
    val hasImage = config.showImage || config.dialogType == DialogType.IMAGE
    val hasRating = config.dialogType == DialogType.RATING
    val hasInput = config.dialogType == DialogType.INPUT
    val hasDontShow = config.showDontShowAgain || config.dialogType == DialogType.DONT_SHOW_AGAIN
    val hasSwitch = config.showSwitch || config.dialogType == DialogType.SWITCH_DIALOG

    var ratingVal by remember { mutableIntStateOf(3) }
    var checked by remember { mutableStateOf(false) }
    var switchOn by remember { mutableStateOf(false) }
    var inputVal by remember { mutableStateOf("") }

    val context = LocalContext.current
    var customImage by remember(config.imageUri) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    LaunchedEffect(config.imageUri) {
        if (config.imageUri.isNotEmpty()) {
            try {
                val uri = android.net.Uri.parse(config.imageUri)
                val stream = context.contentResolver.openInputStream(uri)
                customImage = BitmapFactory.decodeStream(stream)?.asImageBitmap()
            } catch (e: Exception) {}
        } else {
            customImage = null
        }
    }

    var customIcon by remember(config.iconUri) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    LaunchedEffect(config.iconUri) {
        if (config.iconUri.isNotEmpty()) {
            try {
                val uri = android.net.Uri.parse(config.iconUri)
                val stream = context.contentResolver.openInputStream(uri)
                customIcon = BitmapFactory.decodeStream(stream)?.asImageBitmap()
            } catch (e: Exception) {}
        } else {
            customIcon = null
        }
    }

    val strokeBorder = if (config.dialogStrokeWidth > 0f) {
        androidx.compose.foundation.BorderStroke(
            width = config.dialogStrokeWidth.dp,
            color = config.dialogStrokeComposeColor()
        )
    } else null

    Card(
        modifier = Modifier
            .fillMaxWidth(config.dialogWidthFraction)
            .wrapContentSize()
            .then(
                if (strokeBorder != null) Modifier.border(
                    strokeBorder,
                    RoundedCornerShape(config.cornerRadius.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(config.cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = config.dialogBgComposeColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = config.dialogPaddingH.dp,
                    vertical = config.dialogPaddingV.dp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (hasImage) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(config.imageHeight.coerceAtMost(130f).dp)
                        .clip(RoundedCornerShape(config.imageCornerRadius.dp))
                        .background(Color(0xFFDDE3F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (customImage != null) {
                        Image(bitmap = customImage!!, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth())
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = null,
                            tint = Color(0xFF8A9CC8),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (hasIcon && !hasImage) {
                val iconVec = when (config.dialogType) {
                    DialogType.INFO -> Icons.Filled.Info
                    DialogType.WARNING -> Icons.Filled.Warning
                    DialogType.ERROR -> Icons.Filled.Error
                    DialogType.SUCCESS -> Icons.Filled.CheckCircle
                    else -> Icons.Filled.Info
                }
                val iconShape = when (config.iconStyle) {
                    IconStyle.CIRCLE -> CircleShape
                    IconStyle.ROUNDED_SQUARE -> RoundedCornerShape(16.dp)
                    IconStyle.PLAIN -> RoundedCornerShape(0.dp)
                }
                Box(
                    modifier = Modifier
                        .size(config.iconBgSize.coerceAtMost(64f).dp)
                        .clip(iconShape)
                        .background(config.iconBgComposeColor()),
                    contentAlignment = Alignment.Center
                ) {
                    if (customIcon != null) {
                        Image(bitmap = customIcon!!, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(config.iconSize.coerceAtMost(64f).dp))
                    } else {
                        Icon(
                            imageVector = iconVec,
                            contentDescription = null,
                            tint = config.iconComposeColor(),
                            modifier = Modifier.size(config.iconSize.coerceAtMost(40f).dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (config.title.isNotEmpty()) {
                Text(
                    text = config.title,
                    fontSize = config.titleFontSize.coerceIn(10f, 26f).sp,
                    fontWeight = config.titleFontWeight.toFontWeight(),
                    color = config.titleComposeColor(),
                    textAlign = config.titleAlignment.toTextAlign(),
                    fontStyle = if (config.titleItalic) FontStyle.Italic else FontStyle.Normal,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(config.contentSpacing.dp))
            }

            if (config.message.isNotEmpty()) {
                Text(
                    text = config.message,
                    fontSize = config.messageFontSize.coerceIn(10f, 20f).sp,
                    fontWeight = config.messageFontWeight.toFontWeight(),
                    color = config.messageComposeColor(),
                    textAlign = config.messageAlignment.toTextAlign(),
                    fontStyle = if (config.messageItalic) FontStyle.Italic else FontStyle.Normal,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (hasRating) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = config.ratingLabel,
                    fontSize = 12.sp,
                    color = config.messageComposeColor().copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < ratingVal) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = null,
                            tint = if (i < ratingVal) Color(0xFFFFC107) else Color(0xFFCCCCCC),
                            modifier = Modifier
                                .size(26.dp)
                                .clickable { ratingVal = i + 1 }
                        )
                    }
                }
            }

            if (hasInput) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { inputVal = it },
                    label = { Text(config.inputLabel, fontSize = 11.sp) },
                    placeholder = { Text(config.inputHint, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                )
            }

            if (hasDontShow) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { checked = !checked }
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = config.dontShowAgainText,
                        fontSize = 12.sp,
                        color = config.messageComposeColor()
                    )
                }
            }

            if (hasSwitch) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = config.switchText,
                        fontSize = 12.sp,
                        color = config.messageComposeColor()
                    )
                    Switch(
                        checked = switchOn,
                        onCheckedChange = { switchOn = it },
                        modifier = Modifier.size(width = 40.dp, height = 24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height((config.contentSpacing + 8f).dp))

            PreviewButtons(config)
        }
    }
}

@Composable
private fun PreviewButtons(config: DialogConfig) {
    val posBg = config.posButtonBgComposeColor()
    val posTxt = config.posButtonTextComposeColor()
    val negBg = config.negButtonBgComposeColor()
    val negTxt = config.negButtonTextComposeColor()
    val posStroke = if (config.posButtonStrokeWidth > 0f) {
        androidx.compose.foundation.BorderStroke(config.posButtonStrokeWidth.dp, config.posButtonStrokeComposeColor())
    } else androidx.compose.foundation.BorderStroke(0.dp, Color.Transparent)
    val negStroke = if (config.negButtonStrokeWidth > 0f) {
        androidx.compose.foundation.BorderStroke(config.negButtonStrokeWidth.dp, config.negButtonStrokeComposeColor())
    } else androidx.compose.foundation.BorderStroke(0.dp, Color.Transparent)

    val isHoriz = config.buttonArrangement == ButtonArrangement.HORIZONTAL
    val uriHandler = LocalUriHandler.current

    val posButton: @Composable (Modifier) -> Unit = { mod ->
        if (config.showPositiveButton) {
            Box(
                modifier = mod
                    .height(config.buttonHeight.coerceIn(32f, 56f).dp)
                    .clip(RoundedCornerShape(config.buttonCornerRadius.dp))
                    .background(posBg)
                    .border(posStroke, RoundedCornerShape(config.buttonCornerRadius.dp))
                    .clickable {
                        if (config.positiveButtonLink.isNotEmpty()) {
                            try { uriHandler.openUri(config.positiveButtonLink) } catch(e: Exception){}
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = config.positiveButtonText,
                    fontSize = config.buttonFontSize.coerceIn(10f, 18f).sp,
                    fontWeight = config.buttonFontWeight.toFontWeight(),
                    color = posTxt
                )
            }
        }
    }

    val negButton: @Composable (Modifier) -> Unit = { mod ->
        if (config.showNegativeButton) {
            Box(
                modifier = mod
                    .height(config.buttonHeight.coerceIn(32f, 56f).dp)
                    .clip(RoundedCornerShape(config.buttonCornerRadius.dp))
                    .background(negBg)
                    .border(negStroke, RoundedCornerShape(config.buttonCornerRadius.dp))
                    .clickable {
                        if (config.negativeButtonLink.isNotEmpty()) {
                            try { uriHandler.openUri(config.negativeButtonLink) } catch(e: Exception){}
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = config.negativeButtonText,
                    fontSize = config.buttonFontSize.coerceIn(10f, 18f).sp,
                    fontWeight = config.buttonFontWeight.toFontWeight(),
                    color = negTxt
                )
            }
        }
    }

    if (isHoriz) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(config.buttonSpacing.dp)
        ) {
            val weight = if (config.showPositiveButton && config.showNegativeButton) 1f else 1f
            negButton(Modifier.weight(weight))
            posButton(Modifier.weight(weight))
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(config.buttonSpacing.dp)
        ) {
            posButton(Modifier.fillMaxWidth())
            negButton(Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun IosStylePreview(config: DialogConfig) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(config.dialogWidthFraction),
        shape = RoundedCornerShape(config.cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = config.dialogBgComposeColor()),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 20.dp, bottom = 0.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = config.title,
                fontSize = config.titleFontSize.coerceIn(12f, 24f).sp,
                fontWeight = config.titleFontWeight.toFontWeight(),
                color = config.titleComposeColor(),
                textAlign = config.titleAlignment.toTextAlign(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = config.message,
                fontSize = config.messageFontSize.coerceIn(10f, 20f).sp,
                color = config.messageComposeColor(),
                textAlign = config.messageAlignment.toTextAlign(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
        HorizontalDivider(color = Color(0x30000000), thickness = 0.5.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            if (config.showNegativeButton) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(config.buttonHeight.coerceIn(32f, 60f).dp)
                        .clickable {
                            if (config.negativeButtonLink.isNotEmpty()) {
                                try { uriHandler.openUri(config.negativeButtonLink) } catch(e: Exception){}
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = config.negativeButtonText,
                        fontSize = config.buttonFontSize.coerceIn(12f, 20f).sp,
                        fontWeight = config.buttonFontWeight.toFontWeight(),
                        color = config.negButtonTextComposeColor()
                    )
                }
                Box(
                    modifier = Modifier
                        .width(0.5.dp)
                        .height(config.buttonHeight.coerceIn(32f, 60f).dp)
                        .background(Color(0x30000000))
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(config.buttonHeight.coerceIn(32f, 60f).dp)
                    .clickable {
                        if (config.positiveButtonLink.isNotEmpty()) {
                            try { uriHandler.openUri(config.positiveButtonLink) } catch(e: Exception){}
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = config.positiveButtonText,
                    fontSize = config.buttonFontSize.coerceIn(12f, 20f).sp,
                    fontWeight = FontWeight.Bold,
                    color = config.posButtonTextComposeColor()
                )
            }
        }
    }
}

@Composable
private fun LoadingPreview(config: DialogConfig) {
    Card(
        modifier = Modifier.fillMaxWidth(config.dialogWidthFraction),
        shape = RoundedCornerShape(config.cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = config.dialogBgComposeColor()),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = config.dialogPaddingH.dp, vertical = config.dialogPaddingV.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(44.dp),
                strokeWidth = 3.5.dp,
                color = config.posButtonBgComposeColor()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = config.loadingText,
                fontSize = config.messageFontSize.sp,
                fontWeight = config.messageFontWeight.toFontWeight(),
                color = config.messageComposeColor(),
                textAlign = config.messageAlignment.toTextAlign(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MultiActionPreview(config: DialogConfig) {
    val items = config.multiActionItems.split("\n").take(4)
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(config.dialogWidthFraction),
        shape = RoundedCornerShape(config.cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = config.dialogBgComposeColor()),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = config.dialogPaddingH.dp, vertical = config.dialogPaddingV.dp)) {
                Text(
                    text = config.title,
                    fontSize = config.titleFontSize.sp,
                    fontWeight = config.titleFontWeight.toFontWeight(),
                    color = config.titleComposeColor(),
                    textAlign = config.titleAlignment.toTextAlign(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = config.message,
                    fontSize = config.messageFontSize.sp,
                    color = config.messageComposeColor(),
                    textAlign = config.messageAlignment.toTextAlign(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            HorizontalDivider(color = Color(0x1A000000), thickness = 0.5.dp)
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(config.buttonHeight.dp)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = config.buttonFontSize.sp,
                        color = config.posButtonBgComposeColor(),
                        fontWeight = config.buttonFontWeight.toFontWeight()
                    )
                }
                HorizontalDivider(color = Color(0x1A000000), thickness = 0.5.dp)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(config.buttonHeight.dp)
                    .clickable {
                        if (config.negativeButtonLink.isNotEmpty()) {
                            try { uriHandler.openUri(config.negativeButtonLink) } catch(e: Exception){}
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = config.negativeButtonText,
                    fontSize = config.buttonFontSize.sp,
                    color = config.negButtonTextComposeColor(),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun TermsPreview(config: DialogConfig) {
    Card(
        modifier = Modifier.fillMaxWidth(config.dialogWidthFraction),
        shape = RoundedCornerShape(config.cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = config.dialogBgComposeColor()),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = config.dialogPaddingH.dp, vertical = config.dialogPaddingV.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = config.title,
                fontSize = config.titleFontSize.sp,
                fontWeight = config.titleFontWeight.toFontWeight(),
                color = config.titleComposeColor(),
                textAlign = config.titleAlignment.toTextAlign(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x0F000000))
            ) {
                Text(
                    text = config.termsText,
                    fontSize = config.messageFontSize.sp,
                    fontWeight = config.messageFontWeight.toFontWeight(),
                    color = config.messageComposeColor(),
                    textAlign = config.messageAlignment.toTextAlign(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            PreviewButtons(config)
        }
    }
}