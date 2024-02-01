package com.indoornav.business.store

import java.util.Objects


const val GRID = "grid"
const val TAG = "FireBaseScreen"
const val USERS = "users"
const val STORES = "stores"
const val FLOOR_PLAN = "floorPlan"
const val TAG_MAPPING = "TagMapping"
const val PRODUCT = "Product"
const val PRODUCT_POSITION = "ProductPosition"
const val KEY = PRODUCT_POSITION

data class User(val username: String? = null, val email: String? = null)
data class Coordinate(val rowId: Int, val colId: Int)
data class Store(
    val storeId: String? = null,
    val name: String? = null,
    val address: String? = null,
    val floorPlan: HashMap<String, Object>? = null
)
data class Rack(
    val coordinate: Coordinate,
    val shelf: Int
)
data class FloorPlan(
    val floorId: String,
    val floorNumber: Int,
    val layout: String = GRID,
    val rowsCount: Int,
    val columnsCount: Int,
    val shelvesCount: Int,
    val entry: Coordinate = Coordinate(0,0),
    val rackMapping: Map<String, Rack>)
data class TagMapping(
    val tagId: String,
    val storeId: String,
    val floorId: String,
    val rackId: String,
    val tagType: String,
    val occupancyType: String
)
data class ProductPosition(
    val productId: String,
    val positionTagId: String,
    val count: Int
)
data class Product(
    val productId: String,
    val name: String,
    val priceInPaisa: Int,
    val mrpInPaisa: Int,
    val category: String,
)

data class ProductWithPosition(
    val product: Product,
    val productWithPosition: ProductPosition,
    val tagMapping: TagMapping
)