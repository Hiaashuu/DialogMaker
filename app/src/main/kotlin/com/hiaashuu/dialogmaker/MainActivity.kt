package com.hiaashuu.dialogmaker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hiaashuu.dialogmaker.ui.screens.HomeScreen
import com.hiaashuu.dialogmaker.ui.screens.SettingsScreen
import com.hiaashuu.dialogmaker.ui.theme.ComposeEmptyActivityTheme
import com.hiaashuu.dialogmaker.viewmodel.DialogViewModel
import com.hiaashuu.dialogmaker.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm: SettingsViewModel = viewModel()
            val darkThemePref by settingsVm.isDarkTheme.collectAsState()
            val useSystemTheme by settingsVm.useSystemTheme.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val effectiveDark = if (useSystemTheme) systemDark else darkThemePref

            ComposeEmptyActivityTheme(
                darkTheme = effectiveDark,
                dynamicColor = false
            ) {
                val navController = rememberNavController()
                val dialogVm: DialogViewModel = viewModel()

                AppNavHost(
                    navController = navController,
                    dialogVm = dialogVm,
                    settingsVm = settingsVm
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavController,
    dialogVm: DialogViewModel,
    settingsVm: SettingsViewModel
) {
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var backPressedTime by remember { mutableLongStateOf(0L) }

    BackHandler(enabled = currentRoute == "home") {
        val now = System.currentTimeMillis()
        if (now - backPressedTime < 2000L) {
            (context as? ComponentActivity)?.finish()
        } else {
            backPressedTime = now
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    NavHost(
        navController = navController as androidx.navigation.NavHostController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                dialogViewModel = dialogVm,
                settingsViewModel = settingsVm,
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                settingsViewModel = settingsVm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}