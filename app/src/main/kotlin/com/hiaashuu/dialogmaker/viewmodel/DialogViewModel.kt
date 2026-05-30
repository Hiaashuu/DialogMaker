package com.hiaashuu.dialogmaker.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiaashuu.dialogmaker.data.AppSettings
import com.hiaashuu.dialogmaker.data.DialogConfig
import com.hiaashuu.dialogmaker.utils.ExportManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DialogViewModel : ViewModel() {

    var config by mutableStateOf(DialogConfig())
        private set

    var isExporting by mutableStateOf(false)
        private set

    var exportResult by mutableStateOf<ExportResult?>(null)
        private set

    fun updateConfig(newConfig: DialogConfig) {
        config = newConfig
    }

    fun clearExportResult() {
        exportResult = null
    }

    fun exportDialog(context: Context, settings: AppSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            isExporting = true
            exportResult = null
            try {
                val path = ExportManager.export(context, config, settings)
                exportResult = ExportResult.Success(path)
            } catch (e: Exception) {
                exportResult = ExportResult.Error(e.message ?: "Unknown error occurred")
            } finally {
                isExporting = false
            }
        }
    }
}

sealed class ExportResult {
    data class Success(val filePath: String) : ExportResult()
    data class Error(val message: String) : ExportResult()
}