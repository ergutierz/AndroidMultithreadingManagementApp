package com.example.citytour.view

sealed class Destination(val route: String) {
    data object Account : Destination("account")
    data object Monitor : Destination("monitor")
    data object Edit : Destination("edit")
    data object Settings : Destination("settings")
}