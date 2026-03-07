package com.alexandr.safelimitcalculator.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING_CHECK = "onboarding_check"
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"
    const val HOME = "home"
    const val EXPENSES = "expenses"
    const val PAYMENTS = "payments"
    const val ANALYTICS = "analytics"
    const val SETTINGS = "settings"
    const val PAYMENT_DETAILS = "payment_details/{paymentId}"

    fun paymentDetails(paymentId: Long) = "payment_details/$paymentId"
}
