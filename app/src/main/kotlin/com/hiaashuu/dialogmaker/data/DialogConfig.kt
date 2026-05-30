package com.hiaashuu.dialogmaker.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

enum class DialogType(val label: String, val emoji: String) {
    SIMPLE("Simple", "💬"),
    INFO("Info", "ℹ️"),
    WARNING("Warning", "⚠️"),
    ERROR("Error", "❌"),
    SUCCESS("Success", "✅"),
    IMAGE("Image", "🖼️"),
    IOS_STYLE("iOS Style", "🍎"),
    RATING("Rating", "⭐"),
    INPUT("Input", "⌨️"),
    LOADING("Loading", "⏳"),
    MULTI_ACTION("Multi-Action", "📋"),
    DONT_SHOW_AGAIN("Don't Show", "🔕"),
    SWITCH_DIALOG("Switch", "🔘"),
    TERMS("Terms", "📜")
}

enum class FontWeightOption(val label: String) {
    THIN("Thin"),
    LIGHT("Light"),
    NORMAL("Normal"),
    MEDIUM("Medium"),
    SEMI_BOLD("Semi Bold"),
    BOLD("Bold"),
    EXTRA_BOLD("Extra Bold"),
    BLACK("Black")
}

fun FontWeightOption.toFontWeight(): FontWeight {
    return when (this) {
        FontWeightOption.THIN -> FontWeight.Thin
        FontWeightOption.LIGHT -> FontWeight.Light
        FontWeightOption.NORMAL -> FontWeight.Normal
        FontWeightOption.MEDIUM -> FontWeight.Medium
        FontWeightOption.SEMI_BOLD -> FontWeight.SemiBold
        FontWeightOption.BOLD -> FontWeight.Bold
        FontWeightOption.EXTRA_BOLD -> FontWeight.ExtraBold
        FontWeightOption.BLACK -> FontWeight.Black
    }
}

enum class TextAlignOption(val label: String) {
    START("Start"),
    CENTER("Center"),
    END("End")
}

fun TextAlignOption.toTextAlign(): TextAlign {
    return when (this) {
        TextAlignOption.START -> TextAlign.Start
        TextAlignOption.CENTER -> TextAlign.Center
        TextAlignOption.END -> TextAlign.End
    }
}

enum class ButtonArrangement(val label: String) {
    HORIZONTAL("Horizontal"),
    VERTICAL("Vertical"),
    POSITIVE_ONLY("Positive Only")
}

enum class AnimationType(val label: String) {
    FADE("Fade"),
    SCALE("Scale"),
    SLIDE_UP("Slide Up"),
    BOUNCE("Bounce")
}

enum class IconStyle(val label: String) {
    CIRCLE("Circle"),
    ROUNDED_SQUARE("Rounded Square"),
    PLAIN("Plain")
}

data class DialogConfig(

    val dialogType: DialogType = DialogType.SIMPLE,

    val title: String = "Attention",
    val message: String = "This is a customizable dialog message. Tap anything to begin editing.",
    val positiveButtonText: String = "OK",
    val negativeButtonText: String = "Cancel",
    val neutralButtonText: String = "",
    val showPositiveButton: Boolean = true,
    val showNegativeButton: Boolean = true,
    val showNeutralButton: Boolean = false,

    val positiveButtonLink: String = "",
    val negativeButtonLink: String = "",
    val neutralButtonLink: String = "",

    val dialogBgColor: Long = 0xFFFFFFFF,
    val overlayColor: Long = 0xCC000000,
    val titleColor: Long = 0xFF1A1C1E,
    val messageColor: Long = 0xFF43474E,
    val posButtonBgColor: Long = 0xFF4359A9,
    val posButtonTextColor: Long = 0xFFFFFFFF,
    val negButtonBgColor: Long = 0x00000000,
    val negButtonTextColor: Long = 0xFF4359A9,
    val neuButtonBgColor: Long = 0x00000000,
    val neuButtonTextColor: Long = 0xFF4359A9,
    val iconBgColor: Long = 0xFFD1E4FF,
    val iconColor: Long = 0xFF4359A9,
    val dialogStrokeColor: Long = 0xFF4359A9,
    val posButtonStrokeColor: Long = 0xFF4359A9,
    val negButtonStrokeColor: Long = 0xFF4359A9,

    val dialogWidthFraction: Float = 0.88f,
    val cornerRadius: Float = 20f,
    val titleFontSize: Float = 19f,
    val messageFontSize: Float = 14f,
    val buttonFontSize: Float = 14f,
    val iconSize: Float = 52f,
    val iconBgSize: Float = 72f,
    val imageHeight: Float = 160f,
    val imageCornerRadius: Float = 12f,
    val buttonCornerRadius: Float = 12f,
    val buttonHeight: Float = 46f,

    val dialogPaddingH: Float = 20f,
    val dialogPaddingV: Float = 24f,
    val contentSpacing: Float = 8f,
    val buttonSpacing: Float = 8f,

    val dialogStrokeWidth: Float = 0f,
    val posButtonStrokeWidth: Float = 0f,
    val negButtonStrokeWidth: Float = 0f,

    val titleFontWeight: FontWeightOption = FontWeightOption.BOLD,
    val messageFontWeight: FontWeightOption = FontWeightOption.NORMAL,
    val buttonFontWeight: FontWeightOption = FontWeightOption.MEDIUM,
    val titleAlignment: TextAlignOption = TextAlignOption.CENTER,
    val messageAlignment: TextAlignOption = TextAlignOption.CENTER,
    val titleItalic: Boolean = false,
    val messageItalic: Boolean = false,

    val showIcon: Boolean = false,
    val iconStyle: IconStyle = IconStyle.CIRCLE,
    val iconUri: String = "",

    val showImage: Boolean = false,
    val imageUri: String = "",

    val showDontShowAgain: Boolean = false,
    val dontShowAgainText: String = "Don't show this again",
    val showSwitch: Boolean = false,
    val switchText: String = "Enable feature",
    val isCancelable: Boolean = true,
    val dimAmount: Float = 0.6f,
    val animationType: AnimationType = AnimationType.FADE,

    val buttonArrangement: ButtonArrangement = ButtonArrangement.HORIZONTAL,

    val ratingLabel: String = "Rate your experience",
    val inputHint: String = "Enter here...",
    val inputLabel: String = "Input",
    val loadingText: String = "Please wait...",
    val termsText: String = "By using this app you agree to our Terms of Service and Privacy Policy. These terms outline your rights and responsibilities while using our services.",
    val multiActionItems: String = "Option 1\nOption 2\nOption 3",
    val iosDestructiveText: String = "Delete"
)

fun DialogConfig.dialogBgComposeColor(): Color = Color(dialogBgColor)
fun DialogConfig.overlayComposeColor(): Color = Color(overlayColor)
fun DialogConfig.titleComposeColor(): Color = Color(titleColor)
fun DialogConfig.messageComposeColor(): Color = Color(messageColor)
fun DialogConfig.posButtonBgComposeColor(): Color = Color(posButtonBgColor)
fun DialogConfig.posButtonTextComposeColor(): Color = Color(posButtonTextColor)
fun DialogConfig.negButtonBgComposeColor(): Color = Color(negButtonBgColor)
fun DialogConfig.negButtonTextComposeColor(): Color = Color(negButtonTextColor)
fun DialogConfig.neuButtonBgComposeColor(): Color = Color(neuButtonBgColor)
fun DialogConfig.neuButtonTextComposeColor(): Color = Color(neuButtonTextColor)
fun DialogConfig.iconBgComposeColor(): Color = Color(iconBgColor)
fun DialogConfig.iconComposeColor(): Color = Color(iconColor)
fun DialogConfig.dialogStrokeComposeColor(): Color = Color(dialogStrokeColor)
fun DialogConfig.posButtonStrokeComposeColor(): Color = Color(posButtonStrokeColor)
fun DialogConfig.negButtonStrokeComposeColor(): Color = Color(negButtonStrokeColor)