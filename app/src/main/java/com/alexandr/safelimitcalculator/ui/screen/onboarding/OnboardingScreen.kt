package com.alexandr.safelimitcalculator.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.alexandr.safelimitcalculator.R
import com.alexandr.safelimitcalculator.theme.LocalAppTheme
import com.alexandr.safelimitcalculator.ui.navigation.Routes
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // Observe completion and navigate
    }

    when (uiState.currentStep) {
        0 -> OnboardingStep1(onNext = { viewModel.nextStep() })
        1 -> OnboardingStep2(
            onFinish = {
                viewModel.completeOnboarding()
                navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            },
            onBack = { viewModel.previousStep() }
        )
    }
}

@Composable
fun OnboardingStep1(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.colors.background)
            .padding(LocalAppTheme.dimen.extraLarge),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(LocalAppTheme.dimen.spacing48))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_title_1),
                style = LocalAppTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(LocalAppTheme.dimen.large))
            Text(
                text = stringResource(R.string.onboarding_desc_1),
                style = LocalAppTheme.typography.bodyLarge,
                color = LocalAppTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalAppTheme.dimen.buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalAppTheme.colors.primary
            ),
            shape = LocalAppTheme.shapes.medium
        ) {
            Text(
                text = stringResource(R.string.onboarding_next),
                style = LocalAppTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.onPrimary
            )
        }
    }
}

@Composable
fun OnboardingStep2(
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.colors.background)
            .padding(LocalAppTheme.dimen.extraLarge),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(LocalAppTheme.dimen.spacing48))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_title_2),
                style = LocalAppTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(LocalAppTheme.dimen.large))
            Text(
                text = stringResource(R.string.onboarding_desc_2),
                style = LocalAppTheme.typography.bodyLarge,
                color = LocalAppTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalAppTheme.dimen.buttonHeight),
            horizontalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.medium)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalAppTheme.colors.surface
                ),
                shape = LocalAppTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.common_back),
                    style = LocalAppTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LocalAppTheme.colors.primary
                )
            }

            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalAppTheme.colors.primary
                ),
                shape = LocalAppTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.onboarding_get_started),
                    style = LocalAppTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LocalAppTheme.colors.onPrimary
                )
            }
        }
    }
}
