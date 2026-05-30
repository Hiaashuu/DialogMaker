package com.hiaashuu.dialogmaker.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.hiaashuu.dialogmaker.ui.components.CustomizationPanel
import com.hiaashuu.dialogmaker.ui.components.LiveDialogPreview
import com.hiaashuu.dialogmaker.viewmodel.DialogViewModel
import com.hiaashuu.dialogmaker.viewmodel.ExportResult
import com.hiaashuu.dialogmaker.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    dialogViewModel: DialogViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToSettings: () -> Unit
) {
    val config = dialogViewModel.config
    val isExporting = dialogViewModel.isExporting
    val exportResult = dialogViewModel.exportResult

    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
    val useSystemTheme by settingsViewModel.useSystemTheme.collectAsState()
    val showPreviewDark by settingsViewModel.showPreviewDark.collectAsState()
    val encryptOutput by settingsViewModel.encryptOutput.collectAsState()
    val useRandomFilename by settingsViewModel.useRandomFilename.collectAsState()
    val includeSource by settingsViewModel.includeSource.collectAsState()
    val includeSmali by settingsViewModel.includeSmali.collectAsState()
    val includeReadme by settingsViewModel.includeReadme.collectAsState()
    val includeJavaView by settingsViewModel.includeJavaView.collectAsState()
    val compressZip by settingsViewModel.compressZip.collectAsState()
    val folderName by settingsViewModel.folderName.collectAsState()
    val obfuscateNames by settingsViewModel.obfuscateNames.collectAsState()
    val autoOpenOnSave by settingsViewModel.autoOpenOnSave.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // ── Build and trigger export ─────────────────────────────────────────────
    val doExport: () -> Unit = {
        val settings = settingsViewModel.currentSettings(
            isDark = isDarkTheme,
            useSys = useSystemTheme,
            encrypt = encryptOutput,
            randName = useRandomFilename,
            incSrc = includeSource,
            incSmali = includeSmali,
            incReadme = includeReadme,
            incJava = includeJavaView,
            compress = compressZip,
            folder = folderName,
            obf = obfuscateNames,
            prevDark = showPreviewDark,
            autoOpen = autoOpenOnSave
        )
        dialogViewModel.exportDialog(context, settings)
    }

    // ── Permission launcher (needed only for API < 29) ───────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            doExport()
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Storage permission required to save files.")
            }
        }
    }

    // ── Save button click handler ────────────────────────────────────────────
    val onSaveClick: () -> Unit = {
        if (!isExporting) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
                    doExport()
                } else {
                    permissionLauncher.launch(perm)
                }
            } else {
                doExport()
            }
        }
    }

    // ── React to export result via Snackbar ──────────────────────────────────
    LaunchedEffect(exportResult) {
        exportResult?.let { result ->
            when (result) {
                is ExportResult.Success -> {
                    val fileName = result.filePath.substringAfterLast("/")
                    val snackResult = snackbarHostState.showSnackbar(
                        message = "✅ Saved: $fileName",
                        actionLabel = if (autoOpenOnSave) "Open" else null,
                        duration = SnackbarDuration.Long
                    )
                    if (snackResult == SnackbarResult.ActionPerformed) {
                        try {
                            val downloadsUri = Uri.parse(
                                Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .toURI()
                                    .toString()
                            )
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(downloadsUri, "resource/folder")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(
                                "File saved to Downloads/$folderName"
                            )
                        }
                    }
                }
                is ExportResult.Error -> {
                    snackbarHostState.showSnackbar(
                        message = "❌ ${result.message}",
                        duration = SnackbarDuration.Short
                    )
                }
            }
            dialogViewModel.clearExportResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DialogMaker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = config.dialogType.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    // Theme toggle (only visible when not following system theme)
                    if (!useSystemTheme) {
                        IconButton(
                            onClick = { settingsViewModel.setDarkTheme(!isDarkTheme) }
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = if (isDarkTheme) "Light Mode" else "Dark Mode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !isExporting
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Saving...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save Dialog",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── STATIC: Live Preview ─────────────────────────────────────────
            // This is always visible and does NOT scroll away
            LiveDialogPreview(
                config = config,
                isDarkBackground = showPreviewDark,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )

            // ── SCROLLABLE: Customization Panel ──────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                CustomizationPanel(
                    config = config,
                    onUpdate = { dialogViewModel.updateConfig(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}