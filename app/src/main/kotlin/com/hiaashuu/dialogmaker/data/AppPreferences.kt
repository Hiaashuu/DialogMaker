package com.hiaashuu.dialogmaker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dm_settings")

object PrefsKeys {
    val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
    val ENCRYPT_OUTPUT = booleanPreferencesKey("encrypt_output")
    val USE_RANDOM_FILENAME = booleanPreferencesKey("use_random_filename")
    val INCLUDE_SOURCE = booleanPreferencesKey("include_source")
    val INCLUDE_SMALI = booleanPreferencesKey("include_smali")
    val INCLUDE_README = booleanPreferencesKey("include_readme")
    val INCLUDE_JAVA_VIEW = booleanPreferencesKey("include_java_view")
    val COMPRESS_ZIP = booleanPreferencesKey("compress_zip")
    val FOLDER_NAME = stringPreferencesKey("folder_name")
    val OBFUSCATE_NAMES = booleanPreferencesKey("obfuscate_names")
    val SHOW_PREVIEW_DARK = booleanPreferencesKey("show_preview_dark")
    val AUTO_OPEN_ON_SAVE = booleanPreferencesKey("auto_open_on_save")
}

data class AppSettings(
    val isDarkTheme: Boolean = false,
    val useSystemTheme: Boolean = true,
    val encryptOutput: Boolean = false,
    val useRandomFilename: Boolean = false,
    val includeSource: Boolean = true,
    val includeSmali: Boolean = true,
    val includeReadme: Boolean = true,
    val includeJavaView: Boolean = true,
    val compressZip: Boolean = true,
    val folderName: String = "DialogMaker",
    val obfuscateNames: Boolean = true,
    val showPreviewDark: Boolean = false,
    val autoOpenOnSave: Boolean = false
)