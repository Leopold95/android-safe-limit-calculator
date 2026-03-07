package com.alexandr.safelimitcalculator.ui.screen.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexandr.safelimitcalculator.R
import com.alexandr.safelimitcalculator.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val daysUntilIncome = remember(uiState.nextIncomeDate) {
        ChronoUnit.DAYS.between(java.time.LocalDate.now(), uiState.nextIncomeDate).coerceAtLeast(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.analytics_overview),
            style = LocalAppTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = LocalAppTheme.colors.onBackground
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = LocalAppTheme.colors.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.analytics_total_spent),
                            style = LocalAppTheme.typography.bodyMedium,
                            color = LocalAppTheme.colors.onPrimaryContainer
                        )
                        Text(
                            text = "$${"%.2f".format(uiState.totalSpent)}",
                            style = LocalAppTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = LocalAppTheme.colors.onPrimaryContainer
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.analytics_avg_daily),
                            style = LocalAppTheme.typography.bodyMedium,
                            color = LocalAppTheme.colors.onPrimaryContainer
                        )
                        Text(
                            text = "$${"%.2f".format(uiState.averageDailySpending)}",
                            style = LocalAppTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = LocalAppTheme.colors.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = LocalAppTheme.colors.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalAppTheme.dimen.spacing20),
                verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.spacing12)
            ) {
                Text(
                    text = stringResource(R.string.analytics_planned_vs_actual),
                    style = LocalAppTheme.typography.titleMedium,
                    color = LocalAppTheme.colors.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )

                val planned = uiState.plannedTotalUntilIncome.coerceAtLeast(0.0)
                val actual = uiState.totalSpent.coerceAtLeast(0.0)
                val progress = if (planned > 0.0) (actual / planned).toFloat().coerceIn(0f, 1f) else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = LocalAppTheme.colors.primary,
                    trackColor = LocalAppTheme.colors.surfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$${"%.2f".format(actual)}",
                        style = LocalAppTheme.typography.bodyMedium,
                        color = LocalAppTheme.colors.onSecondaryContainer
                    )
                    Text(
                        text = "$${"%.2f".format(planned)}",
                        style = LocalAppTheme.typography.bodyMedium,
                        color = LocalAppTheme.colors.onSecondaryContainer
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = LocalAppTheme.colors.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalAppTheme.dimen.spacing20),
                verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.spacing12)
            ) {
                Text(
                    text = stringResource(R.string.analytics_forecast),
                    style = LocalAppTheme.typography.titleMedium,
                    color = LocalAppTheme.colors.onTertiaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.analytics_forecast_template, daysUntilIncome, uiState.forecastTotalUntilIncome),
                    style = LocalAppTheme.typography.bodyMedium,
                    color = LocalAppTheme.colors.onTertiaryContainer
                )
            }
        }

        if (uiState.categoryBreakdown.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = LocalAppTheme.colors.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.analytics_no_data),
                        style = LocalAppTheme.typography.bodyLarge,
                        color = LocalAppTheme.colors.onSurfaceVariant
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.analytics_by_category),
                style = LocalAppTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.onBackground
            )

            uiState.categoryBreakdown.forEach { categorySpending ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = LocalAppTheme.colors.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = categorySpending.category,
                            style = LocalAppTheme.typography.titleMedium,
                            color = LocalAppTheme.colors.onSurface
                        )
                        Text(
                            text = "$${"%.2f".format(categorySpending.amount)}",
                            style = LocalAppTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = LocalAppTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }
}
