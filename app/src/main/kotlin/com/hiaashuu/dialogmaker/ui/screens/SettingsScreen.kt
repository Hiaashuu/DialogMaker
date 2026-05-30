package com.hiaashuu.dialogmaker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiaashuu.dialogmaker.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
    val useSystemTheme by settingsViewModel.useSystemTheme.collectAsState()
    val encryptOutput by settingsViewModel.encryptOutput.collectAsState()
    val useRandomFilename by settingsViewModel.useRandomFilename.collectAsState()
    val includeSource by settingsViewModel.includeSource.collectAsState()
    val includeSmali by settingsViewModel.includeSmali.collectAsState()
    val includeReadme by settingsViewModel.includeReadme.collectAsState()
    val includeJavaView by settingsViewModel.includeJavaView.collectAsState()
    val compressZip by settingsViewModel.compressZip.collectAsState()
    val folderName by settingsViewModel.folderName.collectAsState()
    val obfuscateNames by settingsViewModel.obfuscateNames.collectAsState()
    val showPreviewDark by settingsViewModel.showPreviewDark.collectAsState()
    val autoOpenOnSave by settingsViewModel.autoOpenOnSave.collectAsState()

    var folderNameInput by remember(folderName) { mutableStateOf(folderName) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── APPEARANCE ───────────────────────────────────────────────────
            SettingsSection(title = "Appearance", icon = Icons.Filled.Palette) {
                SettingsSwitchItem(
                    label = "Use System Theme",
                    description = "Follow device dark/light setting automatically",
                    checked = useSystemTheme,
                    onCheckedChange = { settingsViewModel.setUseSystemTheme(it) }
                )
                SettingsDivider()
                SettingsSwitchItem(
                    label = "Dark Mode",
                    description = if (useSystemTheme) "Controlled by system theme" else "Force dark theme on",
                    checked = isDarkTheme,
                    onCheckedChange = { settingsViewModel.setDarkTheme(it) },
                    enabled = !useSystemTheme
                )
                SettingsDivider()
                SettingsSwitchItem(
                    label = "Preview Dark Background",
                    description = "Show dialog preview on a dark overlay",
                    checked = showPreviewDark,
                    onCheckedChange = { settingsViewModel.setShowPreviewDark(it) }
                )
            }

            // ── EXPORT OPTIONS ───────────────────────────────────────────────
            SettingsSection(title = "Export Options", icon = Icons.Filled.Code) {
                SettingsSwitchItem(
                    label = "Encrypt Source Files",
                    description = "XOR-encode the exported Kotlin/Java source",
                    checked = encryptOutput,
                    onCheckedChange = { settingsViewModel.setEncryptOutput(it) }
                )
                SettingsDivider()
                SettingsSwitchItem(
                    label = "Random Filename",
                    description = "Use a randomized name for the exported ZIP",
                    checked = useRandomFilename,
                    onCheckedChange = { settingsViewModel.setUseRandomFilename(it) }
                )
                SettingsDivider()
                SettingsSwitchItem(
                    label = "Include Compose Source (.kt)",
                    description = "Jetpack Compose dialog — drop into any project",
                    checked = includeSource,
                    onCheckedChange = { settingsViewModel.setIncludeSource(it) }
                )
                SettingsDivider()
                SettingsSwitchItem(
                    label = "Include Java View Source (.java)",
                    description = "Legacy view-based dialog for non-Compose apps",
                    checked = includeJavaView,
                    onCheckedChange = { settingsViewModel.setIncludeJavaView(it) }
                )
                SettingsDivider()
                SettingsSwitchItem(
                    label = "Include Smali File (.smali)",
                    description = "APK Editor Pro patch — inject into any APK",
                    checked = includeSmali,
                    onCheckedChange = { settingsViewModel.setIncludeSmali(it) }
                )
                SettingsDivider()
                SettingsSwitchItem(
                    label = "Include README.txt",
                    description = "Add usage instructions to the ZIP",
                    checked = includeReadme,
                    onCheckedChange = { settingsViewModel.setIncludeReadme(it) }
                )
                SettingsDivider()
                SettingsSwitchItem(
                    label = "Compress ZIP",
                    description = "Reduce file size with deflate compression",
                    checked = compressZip,
                    onCheckedChange = { settingsViewModel.setCompressZip(it) }
                )
            }

            // ── SECURITY ─────────────────────────────────────────────────────
            SettingsSection(title = "Security", icon = Icons.Filled.Security) {
                SettingsSwitchItem(
                    label = "Obfuscate Class & Package Names",
                    description = "Use randomized names — makes it hard for\nleechers to find and delete your dialog",
                    checked = obfuscateNames,
                    onCheckedChange = { settingsViewModel.setObfuscateNames(it) }
                )
            }

            // ── OUTPUT ───────────────────────────────────────────────────────
            SettingsSection(title = "Output", icon = Icons.Filled.FolderOpen) {
                // Folder name text field
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Output Folder Name",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Saves to: Downloads/$folderNameInput",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = folderNameInput,
                        onValueChange = { folderNameInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("DialogMaker") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                val trimmed = folderNameInput.trim()
                                if (trimmed.isNotBlank()) {
                                    settingsViewModel.setFolderName(trimmed)
                                }
                            }
                        ),
                        trailingIcon = {
                            if (folderNameInput.trim() != folderName && folderNameInput.isNotBlank()) {
                                TextButton(
                                    onClick = {
                                        focusManager.clearFocus()
                                        val trimmed = folderNameInput.trim()
                                        if (trimmed.isNotBlank()) {
                                            settingsViewModel.setFolderName(trimmed)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "Save",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    )
                }
                SettingsDivider()
                SettingsSwitchItem(
                    label = "Auto-open After Save",
                    description = "Try to open file manager after exporting",
                    checked = autoOpenOnSave,
                    onCheckedChange = { settingsViewModel.setAutoOpenOnSave(it) }
                )
            }

            // ── ABOUT ────────────────────────────────────────────────────────
            SettingsSection(title = "About", icon = Icons.Filled.Info) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DialogMaker",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "v1.0",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Create fully customizable dialogs for your Android apps. Export as Jetpack Compose source, traditional Java View code, or Smali patch for APK Editor Pro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Built by hiaashuu",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private helper composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 2.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f)
            ),
            border = BorderStroke(
                width = 0.7.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    label: String,
    description: String = "",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}