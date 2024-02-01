package com.indoornav.navigation

class NavigationRoute {
    companion object {
        const val HOME = "home"
        const val SELECT_STORE = "home/store"
        const val FLOOR_PLAN = "home/plan"
        const val LANDING_SCREEN = "home/landing"
        const val CUSTOMER_STORE_SCREEN = "home/customer_store/{QR_DATA}"

        //Argumnets
        const val QR_DATA = "QR_DATA"
    }
}