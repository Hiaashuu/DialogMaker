package com.hiaashuu.dialogmaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import com.hiaashuu.dialogmaker.data.AppSettings
import com.hiaashuu.dialogmaker.data.PrefsKeys
import com.hiaashuu.dialogmaker.data.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val ds = application.dataStore

    val isDarkTheme: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.IS_DARK_THEME] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val useSystemTheme: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.USE_SYSTEM_THEME] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val encryptOutput: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.ENCRYPT_OUTPUT] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val useRandomFilename: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.USE_RANDOM_FILENAME] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val includeSource: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.INCLUDE_SOURCE] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val includeSmali: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.INCLUDE_SMALI] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val includeReadme: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.INCLUDE_README] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val includeJavaView: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.INCLUDE_JAVA_VIEW] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val compressZip: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.COMPRESS_ZIP] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val folderName: StateFlow<String> = ds.data
        .map { it[PrefsKeys.FOLDER_NAME] ?: "DialogMaker" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "DialogMaker")

    val obfuscateNames: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.OBFUSCATE_NAMES] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val showPreviewDark: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.SHOW_PREVIEW_DARK] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val autoOpenOnSave: StateFlow<Boolean> = ds.data
        .map { it[PrefsKeys.AUTO_OPEN_ON_SAVE] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setDarkTheme(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.IS_DARK_THEME] = value } }
    }

    fun setUseSystemTheme(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.USE_SYSTEM_THEME] = value } }
    }

    fun setEncryptOutput(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.ENCRYPT_OUTPUT] = value } }
    }

    fun setUseRandomFilename(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.USE_RANDOM_FILENAME] = value } }
    }

    fun setIncludeSource(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.INCLUDE_SOURCE] = value } }
    }

    fun setIncludeSmali(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.INCLUDE_SMALI] = value } }
    }

    fun setIncludeReadme(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.INCLUDE_README] = value } }
    }

    fun setIncludeJavaView(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.INCLUDE_JAVA_VIEW] = value } }
    }

    fun setCompressZip(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.COMPRESS_ZIP] = value } }
    }

    fun setFolderName(value: String) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.FOLDER_NAME] = value } }
    }

    fun setObfuscateNames(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.OBFUSCATE_NAMES] = value } }
    }

    fun setShowPreviewDark(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.SHOW_PREVIEW_DARK] = value } }
    }

    fun setAutoOpenOnSave(value: Boolean) {
        viewModelScope.launch { ds.edit { it[PrefsKeys.AUTO_OPEN_ON_SAVE] = value } }
    }

    fun currentSettings(
        isDark: Boolean,
        useSys: Boolean,
        encrypt: Boolean,
        randName: Boolean,
        incSrc: Boolean,
        incSmali: Boolean,
        incReadme: Boolean,
        incJava: Boolean,
        compress: Boolean,
        folder: String,
        obf: Boolean,
        prevDark: Boolean,
        autoOpen: Boolean
    ): AppSettings {
        return AppSettings(
            isDarkTheme = isDark,
            useSystemTheme = useSys,
            encryptOutput = encrypt,
            useRandomFilename = randName,
            includeSource = incSrc,
            includeSmali = incSmali,
            includeReadme = incReadme,
            includeJavaView = incJava,
            compressZip = compress,
            folderName = folder,
            obfuscateNames = obf,
            showPreviewDark = prevDark,
            autoOpenOnSave = autoOpen
        )
    }
}