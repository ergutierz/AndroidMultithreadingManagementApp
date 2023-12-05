package com.example.citytour.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.citytour.view.Destination
import com.example.citytour.view.NavigationGraph
import com.example.citytour.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewState: MainViewModel.ViewState,
    onIntent: (MainViewModel.Action) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    modifier = Modifier.width(200.dp),
                    navController = navController,
                    drawerState = drawerState
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    viewState = viewState,
                    onIntent = onIntent,
                    drawerState = drawerState,
                    scope = scope,
                    navController = navController
                )
            }
        ) { paddingValues ->
            NavigationGraph(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                viewState = viewState,
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun DrawerContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()
    Column(modifier = modifier) {
        DrawerButton("Account", Icons.Default.Person) {
            navigateToDestination(Destination.Account.route, navController, drawerState, scope)
        }
        DrawerButton("Monitor", Icons.Default.Home) {
            navigateToDestination(Destination.Monitor.route, navController, drawerState, scope)
        }
        DrawerButton("Edit", Icons.Default.Edit) {
            navigateToDestination(Destination.Edit.route, navController, drawerState, scope)
        }
    }
}

@Composable
private fun DrawerButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(16.dp))
            Text(text)
        }
    }
}

private fun navigateToDestination(
    route: String,
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
    }
    scope.launch {
        drawerState.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    viewState: MainViewModel.ViewState,
    onIntent: (MainViewModel.Action) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavController,
) {
    val currentRoute = remember { mutableStateOf(navController.currentDestination?.route) }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentRoute.value = destination.route
        }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Blue,
            titleContentColor = Color.White,
        ),
        title = {
            Text(
                text = getScreenTitle(currentRoute.value),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate(Destination.Settings.route.takeIf {
                    currentRoute.value != Destination.Settings.route
                } ?: Destination.Monitor.route)
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings.takeIf {
                        currentRoute.value != Destination.Settings.route
                    } ?: Icons.Filled.Home,
                    contentDescription = "Toggle Settings",
                    tint = Color.White
                )
            }

            IconButton(onClick = {
                onIntent(MainViewModel.Action.ClearData)
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }

            IconButton(onClick = {
                onIntent(MainViewModel.Action.Share)
            }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = Color.White
                )
            }
        }
        ,
        scrollBehavior = scrollBehavior,
    )
}

private fun getScreenTitle(route: String?): String {
    return when (route) {
        Destination.Monitor.route -> "Monitor"
        Destination.Edit.route -> "Edit"
        Destination.Settings.route -> "Settings"
        Destination.Account.route -> "Account"
        else -> "City Tour"
    }
}
