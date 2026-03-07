package com.alexandr.safelimitcalculator.ui.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.alexandr.safelimitcalculator.R
import com.alexandr.safelimitcalculator.theme.LocalAppTheme
import com.alexandr.safelimitcalculator.ui.navigation.Routes
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isInitialized) {
        if (!uiState.isInitialized) return@LaunchedEffect
        delay(250)
        val nextRoute = if (uiState.hasOnboarded) Routes.MAIN else Routes.ONBOARDING
        navController.navigate(nextRoute) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = LocalAppTheme.colors.primary)

        Text(
            text = stringResource(R.string.app_name),
            style = LocalAppTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = LocalAppTheme.colors.primary
        )
    }
}