package com.hiaashuu.dialogmaker.utils

import android.content.Context
import android.os.Environment
import com.hiaashuu.dialogmaker.data.AnimationType
import com.hiaashuu.dialogmaker.data.AppSettings
import com.hiaashuu.dialogmaker.data.ButtonArrangement
import com.hiaashuu.dialogmaker.data.DialogConfig
import com.hiaashuu.dialogmaker.data.DialogType
import com.hiaashuu.dialogmaker.data.FontWeightOption
import com.hiaashuu.dialogmaker.data.TextAlignOption
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExportManager {

    private val HEX_CHARS = "0123456789abcdef"

    private fun randomHex(len: Int): String {
        return (1..len).map { HEX_CHARS.random() }.joinToString("")
    }

    fun buildObfuscatedPackagePath(settings: AppSettings): String {
        return if (settings.obfuscateNames) {
            "com/${randomHex(5)}r${randomHex(3)}/${randomHex(4)}_sdk"
        } else {
            "com/yourpackage/dialog"
        }
    }

    fun buildClassName(settings: AppSettings): String {
        return if (settings.obfuscateNames) {
            "X${randomHex(4)}R${randomHex(3)}_DFkt"
        } else {
            "CustomDialog"
        }
    }

    private fun copyUriToZip(context: Context, uriString: String, zos: ZipOutputStream, zipPath: String) {
        if (uriString.isEmpty()) return
        try {
            val uri = android.net.Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.use { input ->
                zos.putNextEntry(ZipEntry(zipPath))
                input.copyTo(zos)
                zos.closeEntry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun export(context: Context, config: DialogConfig, settings: AppSettings): String {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputDir = File(downloadsDir, settings.folderName)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val pkgPath = buildObfuscatedPackagePath(settings)
        val className = buildClassName(settings)
        val timestamp = System.currentTimeMillis()

        val zipFileName = if (settings.useRandomFilename) {
            "dlg_${randomHex(8)}_patch.zip"
        } else {
            "dialog_patch_${config.dialogType.name.lowercase(Locale.ROOT)}.zip"
        }

        val zipFile = File(outputDir, zipFileName)

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->

            if (settings.includeSource) {
                val kotlinSource = generateComposeKotlin(config, pkgPath.replace("/", "."), className)
                val srcBytes = if (settings.encryptOutput) xorEncode(kotlinSource.toByteArray()) else kotlinSource.toByteArray()
                zos.putNextEntry(ZipEntry("source/compose/$className.kt"))
                zos.write(srcBytes)
                zos.closeEntry()
            }

            if (settings.includeJavaView) {
                val javaSource = generateJavaViewDialog(config, pkgPath.replace("/", "."), className)
                val javaBytes = if (settings.encryptOutput) xorEncode(javaSource.toByteArray()) else javaSource.toByteArray()
                zos.putNextEntry(ZipEntry("source/java_view/${className}.java"))
                zos.write(javaBytes)
                zos.closeEntry()
            }

            if (settings.includeSmali) {
                val smaliSource = generateSmali(config, pkgPath, className)
                zos.putNextEntry(ZipEntry("smali/$pkgPath/$className.smali"))
                zos.write(smaliSource.toByteArray())
                zos.closeEntry()
            }

            if (settings.includeReadme) {
                val readme = generateReadme(config, pkgPath, className, settings)
                zos.putNextEntry(ZipEntry("README.txt"))
                zos.write(readme.toByteArray())
                zos.closeEntry()
            }

            val snippet = generateUsageSnippet(pkgPath.replace("/", "."), className)
            zos.putNextEntry(ZipEntry("HOW_TO_CALL.txt"))
            zos.write(snippet.toByteArray())
            zos.closeEntry()

            copyUriToZip(context, config.iconUri, zos, "assets/custom_icon.png")
            copyUriToZip(context, config.imageUri, zos, "assets/custom_image.png")
        }

        return zipFile.absolutePath
    }

    private fun xorEncode(data: ByteArray): ByteArray {
        val key = "DM_xR9k4_Secret".toByteArray()
        return ByteArray(data.size) { i ->
            (data[i].toInt() xor key[i % key.size].toInt()).toByte()
        }
    }

    private fun generateComposeKotlin(config: DialogConfig, pkg: String, className: String): String {
        val cornerDp = config.cornerRadius.toInt()
        val padH = config.dialogPaddingH.toInt()
        val padV = config.dialogPaddingV.toInt()
        val titleSize = config.titleFontSize.toInt()
        val msgSize = config.messageFontSize.toInt()
        val btnSize = config.buttonFontSize.toInt()
        val iconSize = config.iconSize.toInt()
        val btnCorner = config.buttonCornerRadius.toInt()
        val btnHeight = config.buttonHeight.toInt()

        val titleWeightStr = config.titleFontWeight.toKotlinFontWeight()
        val msgWeightStr = config.messageFontWeight.toKotlinFontWeight()
        val btnWeightStr = config.buttonFontWeight.toKotlinFontWeight()
        val titleAlignStr = config.titleAlignment.toKotlinAlign()
        val msgAlignStr = config.messageAlignment.toKotlinAlign()

        val bgColorHex = java.lang.Long.toHexString(config.dialogBgColor).uppercase(Locale.ROOT).padStart(8, '0')
        val titleColorHex = java.lang.Long.toHexString(config.titleColor).uppercase(Locale.ROOT).padStart(8, '0')
        val msgColorHex = java.lang.Long.toHexString(config.messageColor).uppercase(Locale.ROOT).padStart(8, '0')
        val posBgHex = java.lang.Long.toHexString(config.posButtonBgColor).uppercase(Locale.ROOT).padStart(8, '0')
        val posTxtHex = java.lang.Long.toHexString(config.posButtonTextColor).uppercase(Locale.ROOT).padStart(8, '0')
        val negBgHex = java.lang.Long.toHexString(config.negButtonBgColor).uppercase(Locale.ROOT).padStart(8, '0')
        val negTxtHex = java.lang.Long.toHexString(config.negButtonTextColor).uppercase(Locale.ROOT).padStart(8, '0')

        val hasIcon = config.showIcon || config.dialogType in listOf(
            DialogType.INFO, DialogType.WARNING, DialogType.ERROR, DialogType.SUCCESS
        )
        val hasImage = config.showImage || config.dialogType == DialogType.IMAGE
        val hasSwitch = config.showSwitch || config.dialogType == DialogType.SWITCH_DIALOG
        val hasDontShow = config.showDontShowAgain || config.dialogType == DialogType.DONT_SHOW_AGAIN
        val hasRating = config.dialogType == DialogType.RATING
        val hasInput = config.dialogType == DialogType.INPUT
        val hasLoading = config.dialogType == DialogType.LOADING
        val isIos = config.dialogType == DialogType.IOS_STYLE

        val strokeStr = if (config.dialogStrokeWidth > 0f) {
            val strokeHex = java.lang.Long.toHexString(config.dialogStrokeColor).uppercase(Locale.ROOT).padStart(8, '0')
            "border = BorderStroke(${config.dialogStrokeWidth.toInt()}.dp, Color(0x${strokeHex}L)),"
        } else {
            ""
        }

        val cancelable = config.isCancelable
        val isHoriz = config.buttonArrangement == ButtonArrangement.HORIZONTAL

        return """
// ============================================================
//  AUTO-GENERATED BY DIALOGMAKER v1.0
//  Type  : ${config.dialogType.label}
//  Obfuscated: ${java.util.UUID.randomUUID()}
// ============================================================
//
//  HOW TO USE:
//  1. Copy this file to your Compose project.
//  2. Change package below to YOUR app package.
//  3. In any Composable screen:
//
//     var showDialog by remember { mutableStateOf(false) }
//     if (showDialog) {
//         $className(
//             onDismiss = { showDialog = false },
//             onPositive = { /* your action */ }
//         )
//     }
//     Button(onClick = { showDialog = true }) { Text("Show") }
//
// ============================================================

package $pkg // <-- CHANGE THIS TO YOUR APP PACKAGE

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun $className(
    onDismiss: () -> Unit = {},
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {},
    onNeutral: () -> Unit = {}
) {
    ${if (hasRating) "var selectedRating by remember { mutableIntStateOf(0) }" else ""}
    ${if (hasInput) "var inputText by remember { mutableStateOf(\"\") }" else ""}
    ${if (hasDontShow) "var dontShow by remember { mutableStateOf(false) }" else ""}
    ${if (hasSwitch) "var switchState by remember { mutableStateOf(false) }" else ""}

    val uriHandler = LocalUriHandler.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = $cancelable,
            dismissOnClickOutside = $cancelable
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(${config.dialogWidthFraction}f)
                ${if (strokeStr.isNotEmpty()) ".border(\n                    width = ${config.dialogStrokeWidth.toInt()}.dp,\n                    color = Color(0x${java.lang.Long.toHexString(config.dialogStrokeColor).uppercase(Locale.ROOT).padStart(8, '0')}L),\n                    shape = RoundedCornerShape(${cornerDp}.dp)\n                )" else ""},
            shape = RoundedCornerShape(${cornerDp}.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x${bgColorHex}L)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = ${padH}.dp, vertical = ${padV}.dp)
                    ${if (hasLoading) "" else ".verticalScroll(rememberScrollState())"},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ${if (hasLoading) generateLoadingContent(config) else ""}
                ${if (hasImage && !hasLoading) generateImagePlaceholder(config) else ""}
                ${if (hasIcon && !hasLoading && !hasImage) generateIconContent(config, iconSize) else ""}
                ${if (!hasLoading) """

                Text(
                    text = "${config.title}",
                    fontSize = ${titleSize}.sp,
                    fontWeight = $titleWeightStr,
                    color = Color(0x${titleColorHex}L),
                    textAlign = $titleAlignStr,
                    fontStyle = ${if (config.titleItalic) "FontStyle.Italic" else "FontStyle.Normal"},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(${config.contentSpacing.toInt()}.dp))

                ${if (config.dialogType == DialogType.TERMS) """
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x0F000000))
                ) {
                    Text(
                        text = "${config.termsText}",
                        fontSize = ${msgSize}.sp,
                        fontWeight = $msgWeightStr,
                        color = Color(0x${msgColorHex}L),
                        textAlign = $msgAlignStr,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(10.dp)
                    )
                }
                """ else """
                Text(
                    text = "${config.message}",
                    fontSize = ${msgSize}.sp,
                    fontWeight = $msgWeightStr,
                    color = Color(0x${msgColorHex}L),
                    textAlign = $msgAlignStr,
                    fontStyle = ${if (config.messageItalic) "FontStyle.Italic" else "FontStyle.Normal"},
                    modifier = Modifier.fillMaxWidth()
                )
                """}
                """ else ""}

                ${if (hasRating) generateRatingContent(config) else ""}
                ${if (hasInput) generateInputContent(config, msgColorHex) else ""}
                ${if (hasDontShow) generateDontShowContent(config) else ""}
                ${if (hasSwitch) generateSwitchContent(config) else ""}

                ${if (!hasLoading) """
                Spacer(modifier = Modifier.height(${config.contentSpacing.toInt() + 8}.dp))

                ${generateButtonsCode(config, btnSize, btnHeight, btnCorner, isHoriz, posBgHex, posTxtHex, negBgHex, negTxtHex, isIos)}
                """ else ""}
            }
        }
    }
}
""".trimIndent()
    }

    private fun generateLoadingContent(config: DialogConfig): String {
        return """
                CircularProgressIndicator(
                    modifier = Modifier.size(52.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${config.loadingText}",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
        """
    }

    private fun generateImagePlaceholder(config: DialogConfig): String {
        val imgCorner = config.imageCornerRadius.toInt()
        val imgH = config.imageHeight.toInt()
        return """
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(${imgH}.dp)
                        .clip(RoundedCornerShape(${imgCorner}.dp))
                        .background(Color(0xFFDDE3F0))
                ) {
                    // Update this block if you want to load custom image from drawable
                    // Image(painter = painterResource(id = R.drawable.custom_image), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center).size(48.dp),
                        tint = Color(0xFF8A9CC8)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
        """
    }

    private fun generateIconContent(config: DialogConfig, iconSize: Int): String {
        val bgColorHex = java.lang.Long.toHexString(config.iconBgColor).uppercase(Locale.ROOT).padStart(8, '0')
        val colorHex = java.lang.Long.toHexString(config.iconColor).uppercase(Locale.ROOT).padStart(8, '0')
        val bgSize = config.iconBgSize.toInt()
        val iconVec = when (config.dialogType) {
            DialogType.INFO -> "Icons.Filled.Info"
            DialogType.WARNING -> "Icons.Filled.Warning"
            DialogType.ERROR -> "Icons.Filled.Error"
            DialogType.SUCCESS -> "Icons.Filled.CheckCircle"
            else -> "Icons.Filled.Info"
        }
        val shapeStr = when (config.iconStyle) {
            com.hiaashuu.dialogmaker.data.IconStyle.CIRCLE -> "CircleShape"
            com.hiaashuu.dialogmaker.data.IconStyle.ROUNDED_SQUARE -> "RoundedCornerShape(16.dp)"
            com.hiaashuu.dialogmaker.data.IconStyle.PLAIN -> "RoundedCornerShape(0.dp)"
        }
        return """
                Box(
                    modifier = Modifier
                        .size(${bgSize}.dp)
                        .clip($shapeStr)
                        .background(Color(0x${bgColorHex}L)),
                    contentAlignment = Alignment.Center
                ) {
                    // Update this block if you want to load custom icon from drawable
                    // Image(painter = painterResource(id = R.drawable.custom_icon), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    Icon(
                        imageVector = $iconVec,
                        contentDescription = null,
                        modifier = Modifier.size(${iconSize}.dp),
                        tint = Color(0x${colorHex}L)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
        """
    }

    private fun generateRatingContent(config: DialogConfig): String {
        return """
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${config.ratingLabel}",
                    fontSize = 13.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < selectedRating) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = null,
                            tint = if (index < selectedRating) Color(0xFFFFC107) else Color(0xFFCCCCCC),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { selectedRating = index + 1 }
                                .padding(2.dp)
                        )
                    }
                }
        """
    }

    private fun generateInputContent(config: DialogConfig, msgColorHex: String): String {
        return """
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("${config.inputLabel}") },
                    placeholder = { Text("${config.inputHint}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
        """
    }

    private fun generateDontShowContent(config: DialogConfig): String {
        return """
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { dontShow = !dontShow }
                ) {
                    Checkbox(checked = dontShow, onCheckedChange = { dontShow = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${config.dontShowAgainText}", fontSize = 13.sp)
                }
        """
    }

    private fun generateSwitchContent(config: DialogConfig): String {
        return """
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${config.switchText}", fontSize = 14.sp)
                    Switch(checked = switchState, onCheckedChange = { switchState = it })
                }
        """
    }

    private fun generateButtonsCode(
        config: DialogConfig,
        btnSize: Int,
        btnHeight: Int,
        btnCorner: Int,
        isHoriz: Boolean,
        posBgHex: String,
        posTxtHex: String,
        negBgHex: String,
        negTxtHex: String,
        isIos: Boolean
    ): String {
        val posLinkAction = if (config.positiveButtonLink.isNotEmpty()) "try { uriHandler.openUri(\"${config.positiveButtonLink}\") } catch (e: Exception) {}" else ""
        val negLinkAction = if (config.negativeButtonLink.isNotEmpty()) "try { uriHandler.openUri(\"${config.negativeButtonLink}\") } catch (e: Exception) {}" else ""

        if (config.dialogType == DialogType.MULTI_ACTION) {
            val items = config.multiActionItems.split("\n")
            val itemsCode = items.joinToString("\n") { item ->
                """
                TextButton(
                    onClick = { $posLinkAction; onPositive(); onDismiss() },
                    modifier = Modifier.fillMaxWidth().height(${btnHeight}.dp),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("$item", fontSize = ${btnSize}.sp, color = Color(0x${posBgHex}L))
                }
                HorizontalDivider(color = Color(0x1A000000), thickness = 0.5.dp)
                """
            }
            return """
                HorizontalDivider(color = Color(0x1A000000), thickness = 0.5.dp)
                $itemsCode
            """
        }

        if (isIos) {
            return """
                HorizontalDivider(color = Color(0x1A000000), thickness = 0.5.dp)
                Row(modifier = Modifier.fillMaxWidth()) {
                    ${if (config.showNegativeButton) """
                    TextButton(
                        onClick = { $negLinkAction; onNegative(); onDismiss() },
                        modifier = Modifier.weight(1f).height(${btnHeight}.dp),
                        shape = RoundedCornerShape(bottomStart = ${btnCorner}.dp)
                    ) {
                        Text("${config.negativeButtonText}", fontSize = ${btnSize}.sp, fontWeight = FontWeight.Medium, color = Color(0x${negTxtHex}L))
                    }
                    VerticalDivider(color = Color(0x1A000000))
                    """ else ""}
                    TextButton(
                        onClick = { $posLinkAction; onPositive(); onDismiss() },
                        modifier = Modifier.weight(1f).height(${btnHeight}.dp),
                        shape = RoundedCornerShape(bottomEnd = ${btnCorner}.dp)
                    ) {
                        Text("${config.positiveButtonText}", fontSize = ${btnSize}.sp, fontWeight = FontWeight.Bold, color = Color(0x${posTxtHex}L))
                    }
                }
            """
        }

        val posStroke = if (config.posButtonStrokeWidth > 0f) {
            val c = java.lang.Long.toHexString(config.posButtonStrokeColor).uppercase(Locale.ROOT).padStart(8, '0')
            "border = BorderStroke(${config.posButtonStrokeWidth.toInt()}.dp, Color(0x${c}L)),"
        } else ""

        val negStroke = if (config.negButtonStrokeWidth > 0f) {
            val c = java.lang.Long.toHexString(config.negButtonStrokeColor).uppercase(Locale.ROOT).padStart(8, '0')
            "border = BorderStroke(${config.negButtonStrokeWidth.toInt()}.dp, Color(0x${c}L)),"
        } else ""

        val positiveBtn = if (config.showPositiveButton) """
                Button(
                    onClick = { $posLinkAction; onPositive(); onDismiss() },
                    modifier = Modifier
                        ${if (isHoriz) ".weight(1f)" else ".fillMaxWidth()"}
                        .height(${btnHeight}.dp),
                    shape = RoundedCornerShape(${btnCorner}.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x${posBgHex}L)),
                    $posStroke
                ) {
                    Text(
                        text = "${config.positiveButtonText}",
                        fontSize = ${btnSize}.sp,
                        fontWeight = FontWeight.${config.buttonFontWeight.name},
                        color = Color(0x${posTxtHex}L)
                    )
                }
        """ else ""

        val negativeBtn = if (config.showNegativeButton) """
                OutlinedButton(
                    onClick = { $negLinkAction; onNegative(); onDismiss() },
                    modifier = Modifier
                        ${if (isHoriz) ".weight(1f)" else ".fillMaxWidth()"}
                        .height(${btnHeight}.dp),
                    shape = RoundedCornerShape(${btnCorner}.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0x${negBgHex}L),
                        contentColor = Color(0x${negTxtHex}L)
                    ),
                    $negStroke
                ) {
                    Text(
                        text = "${config.negativeButtonText}",
                        fontSize = ${btnSize}.sp,
                        fontWeight = FontWeight.${config.buttonFontWeight.name},
                        color = Color(0x${negTxtHex}L)
                    )
                }
        """ else ""

        return if (isHoriz) {
            """
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(${config.buttonSpacing.toInt()}.dp)
                ) {
                    $negativeBtn
                    $positiveBtn
                }
            """
        } else {
            """
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(${config.buttonSpacing.toInt()}.dp)
                ) {
                    $positiveBtn
                    $negativeBtn
                }
            """
        }
    }

    private fun generateJavaViewDialog(config: DialogConfig, pkg: String, className: String): String {
        val bgArgb = "#${java.lang.Long.toHexString(config.dialogBgColor).uppercase(Locale.ROOT).padStart(8, '0')}"
        val posArgb = "#${java.lang.Long.toHexString(config.posButtonBgColor).uppercase(Locale.ROOT).padStart(8, '0')}"

        val posLinkAction = if (config.positiveButtonLink.isNotEmpty()) """
            try {
                android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                i.setData(android.net.Uri.parse("${config.positiveButtonLink}"));
                context.startActivity(i);
            } catch (Exception e) {}
        """.trimIndent() else ""

        val negLinkAction = if (config.negativeButtonLink.isNotEmpty()) """
            try {
                android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                i.setData(android.net.Uri.parse("${config.negativeButtonLink}"));
                context.startActivity(i);
            } catch (Exception e) {}
        """.trimIndent() else ""

        return """
// ============================================================
//  AUTO-GENERATED BY DIALOGMAKER v1.0 (Java / View Style)
//  For traditional (non-Compose) Android projects.
//
//  CALL FROM ACTIVITY:
//  $className.show(this);
//  OR inside onCreate():
//  $className.show(YourActivity.this);
// ============================================================
package $pkg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class $className {

    public static void show(Context context) {
        show(context, null, null);
    }

    public static void show(Context context,
                            DialogInterface.OnClickListener onPositive,
                            DialogInterface.OnClickListener onNegative) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, 0);

        // Root layout
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        int padH = dpToPx(context, ${config.dialogPaddingH.toInt()});
        int padV = dpToPx(context, ${config.dialogPaddingV.toInt()});
        root.setPadding(padH, padV, padH, padV);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("$bgArgb"));
        bg.setCornerRadius(dpToPx(context, ${config.cornerRadius.toInt()}));
        root.setBackground(bg);

        // Title
        TextView tvTitle = new TextView(context);
        tvTitle.setText("${config.title}");
        tvTitle.setTextSize(${config.titleFontSize});
        tvTitle.setTextColor(Color.parseColor("#${java.lang.Long.toHexString(config.titleColor).uppercase(Locale.ROOT).padStart(8, '0')}"));
        tvTitle.setGravity(${config.titleAlignment.toJavaGravity()});
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(tvTitle);

        // Message
        TextView tvMsg = new TextView(context);
        tvMsg.setText("${config.message}");
        tvMsg.setTextSize(${config.messageFontSize});
        tvMsg.setTextColor(Color.parseColor("#${java.lang.Long.toHexString(config.messageColor).uppercase(Locale.ROOT).padStart(8, '0')}"));
        tvMsg.setGravity(${config.messageAlignment.toJavaGravity()});
        LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        msgParams.setMargins(0, dpToPx(context, 8), 0, dpToPx(context, 16));
        tvMsg.setLayoutParams(msgParams);
        root.addView(tvMsg);

        // Button row
        LinearLayout btnRow = new LinearLayout(context);
        btnRow.setOrientation(${if (config.buttonArrangement == ButtonArrangement.HORIZONTAL) "LinearLayout.HORIZONTAL" else "LinearLayout.VERTICAL"});
        btnRow.setGravity(Gravity.CENTER);
        btnRow.setWeightSum(${if (config.buttonArrangement == ButtonArrangement.HORIZONTAL) "2f" else "1f"});
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnRow.setLayoutParams(rowParams);

        ${if (config.showNegativeButton) """

        TextView btnNeg = new TextView(context);
        btnNeg.setText("${config.negativeButtonText}");
        btnNeg.setTextSize(${config.buttonFontSize});
        btnNeg.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams negParams = new LinearLayout.LayoutParams(0, dpToPx(context, ${config.buttonHeight.toInt()}), 1f);
        negParams.setMargins(0, 0, dpToPx(context, ${config.buttonSpacing.toInt()}), 0);
        btnNeg.setLayoutParams(negParams);
        GradientDrawable negBg = new GradientDrawable();
        negBg.setColor(Color.parseColor("#${java.lang.Long.toHexString(config.negButtonBgColor).uppercase(Locale.ROOT).padStart(8, '0')}"));
        negBg.setCornerRadius(dpToPx(context, ${config.buttonCornerRadius.toInt()}));
        btnNeg.setBackground(negBg);
        btnNeg.setTextColor(Color.parseColor("#${java.lang.Long.toHexString(config.negButtonTextColor).uppercase(Locale.ROOT).padStart(8, '0')}"));
        btnRow.addView(btnNeg);
        """ else ""}

        ${if (config.showPositiveButton) """

        TextView btnPos = new TextView(context);
        btnPos.setText("${config.positiveButtonText}");
        btnPos.setTextSize(${config.buttonFontSize});
        btnPos.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams posParams = new LinearLayout.LayoutParams(0, dpToPx(context, ${config.buttonHeight.toInt()}), 1f);
        btnPos.setLayoutParams(posParams);
        GradientDrawable posBg = new GradientDrawable();
        posBg.setColor(Color.parseColor("$posArgb"));
        posBg.setCornerRadius(dpToPx(context, ${config.buttonCornerRadius.toInt()}));
        btnPos.setBackground(posBg);
        btnPos.setTextColor(Color.parseColor("#${java.lang.Long.toHexString(config.posButtonTextColor).uppercase(Locale.ROOT).padStart(8, '0')}"));
        btnRow.addView(btnPos);
        """ else ""}

        root.addView(btnRow);
        builder.setView(root);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setDimAmount(${config.dimAmount}f);
        }
        dialog.setCancelable(${config.isCancelable});

        ${if (config.showNegativeButton) """
        btnNeg.setOnClickListener(v -> {
            $negLinkAction
            if (onNegative != null) onNegative.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
            dialog.dismiss();
        });
        """ else ""}
        ${if (config.showPositiveButton) """
        btnPos.setOnClickListener(v -> {
            $posLinkAction
            if (onPositive != null) onPositive.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
            dialog.dismiss();
        });
        """ else ""}

        dialog.show();

        // Set dialog width
        if (dialog.getWindow() != null) {
            int width = (int) (context.getResources().getDisplayMetrics().widthPixels * ${config.dialogWidthFraction}f);
            dialog.getWindow().setLayout(width, android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
""".trimIndent()
    }

    private fun generateSmali(config: DialogConfig, pkgPath: String, className: String): String {
        val smaliPkg = pkgPath.replace(".", "/")
        val title = config.title.replace("\"", "\\\"")
        val message = config.message.replace("\"", "\\\"")
        val posText = config.positiveButtonText
        val negText = config.negativeButtonText

        return """
# ============================================================
# AUTO-GENERATED SMALI by DialogMaker v1.0
# Package: $smaliPkg
# Class  : $className
#
# HOW TO USE WITH APK EDITOR:
# 1. Place this .smali file in: smali/$smaliPkg/$className.smali
# 2. Rebuild APK with APK Editor Pro (patch mode)
# 3. In your target Activity smali (e.g., MainActivity.smali),
#    inside the onCreate method, add:
#
#    invoke-static {p0}, L$smaliPkg/$className;->show(Landroid/content/Context;)V
#
#    Where p0 = 'this' (the Activity instance which IS a Context)
# ============================================================

.class public final L$smaliPkg/$className;
.super Ljava/lang/Object;
.source "$className.java"

.method public static show(Landroid/content/Context;)V
    .registers 10

    # Create AlertDialog.Builder
    new-instance v0, Landroid/app/AlertDialog${'$'}Builder;
    invoke-direct {v0, p0}, Landroid/app/AlertDialog${'$'}Builder;-><init>(Landroid/content/Context;)V

    # Set title
    const-string v1, "$title"
    invoke-virtual {v0, v1}, Landroid/app/AlertDialog${'$'}Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog${'$'}Builder;
    move-result-object v0

    # Set message
    const-string v1, "$message"
    invoke-virtual {v0, v1}, Landroid/app/AlertDialog${'$'}Builder;->setMessage(Ljava/lang/CharSequence;)Landroid/app/AlertDialog${'$'}Builder;
    move-result-object v0

    # Set cancelable
    const/4 v1, ${if (config.isCancelable) "0x1" else "0x0"}
    invoke-virtual {v0, v1}, Landroid/app/AlertDialog${'$'}Builder;->setCancelable(Z)Landroid/app/AlertDialog${'$'}Builder;
    move-result-object v0

    ${if (config.showPositiveButton) """
    # Positive button ("$posText")
    # Positive Link: ${config.positiveButtonLink} (Implement listener class to open link if needed)
    const-string v1, "$posText"
    const/4 v2, 0x0
    invoke-virtual {v0, v1, v2}, Landroid/app/AlertDialog${'$'}Builder;->setPositiveButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface${'$'}OnClickListener;)Landroid/app/AlertDialog${'$'}Builder;
    move-result-object v0
    """ else ""}

    ${if (config.showNegativeButton) """
    # Negative button ("$negText")
    # Negative Link: ${config.negativeButtonLink} (Implement listener class to open link if needed)
    const-string v1, "$negText"
    const/4 v2, 0x0
    invoke-virtual {v0, v1, v2}, Landroid/app/AlertDialog${'$'}Builder;->setNegativeButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface${'$'}OnClickListener;)Landroid/app/AlertDialog${'$'}Builder;
    move-result-object v0
    """ else ""}

    # Build and show the dialog
    invoke-virtual {v0}, Landroid/app/AlertDialog${'$'}Builder;->create()Landroid/app/AlertDialog;
    move-result-object v1

    # Set window dim amount
    invoke-virtual {v1}, Landroid/app/AlertDialog;->getWindow()Landroid/view/Window;
    move-result-object v2
    if-eqz v2, :skip_window

    const/4 v3, 0x0
    invoke-virtual {v2, v3}, Landroid/view/Window;->setBackgroundDrawableResource(I)V

    const v3, ${(config.dimAmount * 1000).toInt()}
    div-int/lit16 v3, v3, 0x3e8
    int-to-float v3, v3
    invoke-virtual {v2, v3}, Landroid/view/Window;->setDimAmount(F)V

    :skip_window
    invoke-virtual {v1}, Landroid/app/AlertDialog;->show()V

    return-void
.end method

.method private static dpToPx(Landroid/content/Context;I)I
    .registers 4
    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;
    move-result-object v0
    invoke-virtual {v0}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;
    move-result-object v0
    iget v0, v0, Landroid/util/DisplayMetrics;->density:F
    int-to-float v1, p1
    mul-float/2addr v1, v0
    float-to-int v1, v1
    return v1
.end method
""".trimIndent()
    }

    private fun generateReadme(
        config: DialogConfig,
        pkgPath: String,
        className: String,
        settings: AppSettings
    ): String {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())
        return """
╔═══════════════════════════════════════════════════════════════╗
║              DIALOGMAKER v1.0 — Export Package                ║
╚═══════════════════════════════════════════════════════════════╝

Generated   : $timestamp
Dialog Type : ${config.dialogType.label}
Title       : ${config.title}
Package     : ${pkgPath.replace("/", ".")}
Class Name  : $className

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

CONTENTS OF THIS ZIP:
  source/compose/$className.kt     → Jetpack Compose dialog
  source/java_view/$className.java → Traditional View-based dialog
  smali/$pkgPath/$className.smali  → Smali (for APK Editor Pro)
  HOW_TO_CALL.txt                  → Quick usage guide

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🖼  ASSETS & IMAGES:
If you selected custom images/icons, they have been saved
in the `assets/` folder of this ZIP.
- Place them in your app's `res/drawable/` folder.
- Update the generated code to reference `R.drawable.custom_image`
  or `R.drawable.custom_icon` accordingly.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

METHOD 1 — COMPOSE PROJECT:
  1. Copy $className.kt into your project.
  2. Change package name at top of file.
  3. In any Composable:
       var showDialog by remember { mutableStateOf(false) }
       if (showDialog) {
           $className(
               onDismiss = { showDialog = false },
               onPositive = { /* action */ }
           )
       }

METHOD 2 — TRADITIONAL (VIEW-BASED) PROJECT:
  1. Copy $className.java into your project.
  2. Change package name at top of file.
  3. In any Activity:
       $className.show(this);           // simple
       $className.show(this, (d,w) -> { // with callback
           // positive clicked
       }, null);

METHOD 3 — APK EDITOR PRO PATCH:
  1. Open APK Editor Pro → patch mode.
  2. Select the target APK.
  3. Place $className.smali in:
       smali/$pkgPath/$className.smali
  4. In the target Activity's smali, inside onCreate, add:
       invoke-static {p0}, L$pkgPath/$className;->show(Landroid/content/Context;)V
  5. Rebuild and sign.

${if (settings.encryptOutput) """
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
⚠  ENCRYPTION NOTE:
   Source files are XOR-encoded with key: DM_xR9k4_Secret
   Decode before use with any XOR decoder tool.
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
""" else ""}

Generated by DialogMaker — github.com/hiaashuu
""".trimIndent()
    }

    private fun generateUsageSnippet(pkg: String, className: String): String {
        return """
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 QUICK CALL REFERENCE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

── KOTLIN (Compose) ──────────────
import $pkg.$className

// Inside any @Composable function:
var show by remember { mutableStateOf(false) }
Button(onClick = { show = true }) { Text("Show") }
if (show) {
    $className(onDismiss = { show = false })
}

── JAVA (Activity) ───────────────
import $pkg.$className;

// Inside onCreate():
$className.show(this);

── SMALI (APK patch) ─────────────
// In MainActivity.smali, inside .method ... onCreate ...
invoke-static {p0}, L${pkg.replace(".", "/")}/$className;->show(Landroid/content/Context;)V

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
""".trimIndent()
    }
}

private fun FontWeightOption.toKotlinFontWeight(): String {
    return when (this) {
        FontWeightOption.THIN -> "FontWeight.Thin"
        FontWeightOption.LIGHT -> "FontWeight.Light"
        FontWeightOption.NORMAL -> "FontWeight.Normal"
        FontWeightOption.MEDIUM -> "FontWeight.Medium"
        FontWeightOption.SEMI_BOLD -> "FontWeight.SemiBold"
        FontWeightOption.BOLD -> "FontWeight.Bold"
        FontWeightOption.EXTRA_BOLD -> "FontWeight.ExtraBold"
        FontWeightOption.BLACK -> "FontWeight.Black"
    }
}

private fun TextAlignOption.toKotlinAlign(): String {
    return when (this) {
        TextAlignOption.START -> "TextAlign.Start"
        TextAlignOption.CENTER -> "TextAlign.Center"
        TextAlignOption.END -> "TextAlign.End"
    }
}

private fun TextAlignOption.toJavaGravity(): String {
    return when (this) {
        TextAlignOption.START -> "Gravity.START"
        TextAlignOption.CENTER -> "Gravity.CENTER"
        TextAlignOption.END -> "Gravity.END"
    }
}