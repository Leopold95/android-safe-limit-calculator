package com.alexandr.safelimitcalculator.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alexandr.safelimitcalculator.R
import com.alexandr.safelimitcalculator.theme.LocalAppTheme
import com.alexandr.safelimitcalculator.ui.navigation.Routes
import com.alexandr.safelimitcalculator.ui.screen.analytics.AnalyticsScreen
import com.alexandr.safelimitcalculator.ui.screen.expenses.ExpensesScreen
import com.alexandr.safelimitcalculator.ui.screen.home.HomeScreen
import com.alexandr.safelimitcalculator.ui.screen.payment_details.PaymentDetailsScreen
import com.alexandr.safelimitcalculator.ui.screen.payments.PaymentsScreen
import com.alexandr.safelimitcalculator.ui.screen.settings.SettingsScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarTitle = when (currentRoute) {
        Routes.HOME -> stringResource(R.string.nav_home)
        Routes.EXPENSES -> stringResource(R.string.nav_expenses)
        Routes.PAYMENTS -> stringResource(R.string.nav_payments)
        Routes.ANALYTICS -> stringResource(R.string.nav_analytics)
        Routes.SETTINGS -> stringResource(R.string.nav_settings)
        Routes.PAYMENT_DETAILS -> stringResource(R.string.payment_details_title)
        else -> stringResource(R.string.app_name)
    }

    val items = listOf(
        BottomNavItem(Routes.HOME, stringResource(R.string.nav_home)) { Icon(Icons.Outlined.Home, contentDescription = null) },
        BottomNavItem(Routes.EXPENSES, stringResource(R.string.nav_expenses)) { Icon(Icons.Outlined.Receipt, contentDescription = null) },
        BottomNavItem(Routes.PAYMENTS, stringResource(R.string.nav_payments)) { Icon(Icons.Outlined.Payment, contentDescription = null) },
        BottomNavItem(Routes.ANALYTICS, stringResource(R.string.nav_analytics)) { Icon(Icons.Outlined.Analytics, contentDescription = null) },
        BottomNavItem(Routes.SETTINGS, stringResource(R.string.nav_settings)) { Icon(Icons.Outlined.Settings, contentDescription = null) }
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, items) },
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = {
                    if (currentRoute == Routes.PAYMENT_DETAILS) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = LocalAppTheme.dimen.medium)
                .padding(top = LocalAppTheme.dimen.small, bottom = LocalAppTheme.dimen.large)
        ){
            NavHost(navController = navController, startDestination = Routes.HOME) {
                composable(Routes.HOME) { HomeScreen() }
                composable(Routes.EXPENSES) { ExpensesScreen() }
                composable(Routes.PAYMENTS) { PaymentsScreen(navController) }
                composable(Routes.ANALYTICS) { AnalyticsScreen() }
                composable(Routes.SETTINGS) { SettingsScreen() }
                composable(Routes.PAYMENT_DETAILS) { backStackEntry ->
                    val paymentId = backStackEntry.arguments?.getString("paymentId")?.toLongOrNull()
                    if (paymentId != null) {
                        PaymentDetailsScreen(paymentId, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, items: List<BottomNavItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = item.icon,
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
