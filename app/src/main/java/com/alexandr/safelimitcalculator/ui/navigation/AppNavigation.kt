package com.alexandr.safelimitcalculator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import com.alexandr.safelimitcalculator.ui.screen.main.MainScreen
import com.alexandr.safelimitcalculator.ui.screen.onboarding.OnboardingScreen
import com.alexandr.safelimitcalculator.ui.screen.splash.SplashScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.ONBOARDING_CHECK) { OnboardingCheck(navController) }
        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }
        composable(Routes.MAIN) { MainScreen() }
    }
}

@Composable
fun OnboardingCheck(navController: NavController) {
    val repository: BudgetRepository = koinInject()
    val onboardingShown by repository.onboardingShownFlow.collectAsState(initial = false)

    LaunchedEffect(onboardingShown) {
        if (onboardingShown) {
            navController.navigate(Routes.MAIN) {
                popUpTo(Routes.ONBOARDING_CHECK) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.ONBOARDING) {
                popUpTo(Routes.ONBOARDING_CHECK) { inclusive = true }
            }
        }
    }

    // Loading indicator or empty
}
