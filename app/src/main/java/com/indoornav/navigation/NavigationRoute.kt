package com.indoornav.navigation

class NavigationRoute {
    companion object {
        const val HOME = "home"
        const val STORE_ACTION_SCREEN = "store/action/{storeId}/{floorId}"
        const val STORE_MAP_TAG_TO_RACK_SCREEN = "store/maptagtorack/{storeId}/{floorId}"
        const val STORE_ADD_PRODUCT_SCREEN = "store/addproduct/{storeId}/{floorId}"
        const val STORE_MAP_PRODUCT_TO_TAG_SCREEN = "store/mapproducttotag/{storeId}/{floorId}"
        const val SELECT_STORE = "home/store"
        const val FLOOR_PLAN = "home/plan/{storeId}/{floorId}"
    }
}