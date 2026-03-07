package com.alexandr.safelimitcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alexandr.safelimitcalculator.di.dataStoreModule
import com.alexandr.safelimitcalculator.di.databaseModule
import com.alexandr.safelimitcalculator.di.repositoryModule
import com.alexandr.safelimitcalculator.di.viewModelModule
import com.alexandr.safelimitcalculator.ui.navigation.AppNavigation
import com.alexandr.safelimitcalculator.ui.theme.SafeLimitCalculatorTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Koin only if not already started
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@MainActivity)
                modules(
                    listOf(
                        databaseModule,
                        repositoryModule,
                        dataStoreModule,
                        viewModelModule
                    )
                )
            }
        }

        setContent {
            SafeLimitCalculatorTheme {
                AppNavigation()
            }
        }
    }
}
