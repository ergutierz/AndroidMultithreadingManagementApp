package com.example.citytour.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.citytour.view.screens.AccountScreen
import com.example.citytour.view.screens.HomeScreen
import com.example.citytour.view.screens.MonitoringScreen
import com.example.citytour.view.screens.SettingsScreen
import com.example.citytour.viewmodel.MainViewModel

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewState: MainViewModel.ViewState,
    onIntent: (MainViewModel.Action) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        contentAlignment = Alignment.Center,
        startDestination = Destination.Monitor.route
    ) {
        composable(Destination.Account.route) { AccountScreen() }
        composable(Destination.Monitor.route) { HomeScreen(viewState, onIntent) }
        composable(Destination.Edit.route) { MonitoringScreen() }
        composable(Destination.Settings.route) { SettingsScreen(viewState, onIntent) }
    }
}




