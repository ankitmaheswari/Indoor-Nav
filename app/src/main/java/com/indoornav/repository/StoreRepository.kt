package com.indoornav.repository

import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.gson.Gson
import com.indoornav.business.store.FloorPlan
import com.indoornav.business.store.PRODUCT
import com.indoornav.business.store.PRODUCT_POSITION
import com.indoornav.business.store.Product
import com.indoornav.business.store.ProductPosition
import com.indoornav.business.store.ProductWithPosition
import com.indoornav.business.store.STORES
import com.indoornav.business.store.Store
import com.indoornav.business.store.TAG_MAPPING
import com.indoornav.business.store.TagMapping
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StoreRepository {

    val storeDatabase = Firebase.database.getReference(STORES)
    val productDatabase = Firebase.database.getReference(PRODUCT)
    val productPositionDatabase = Firebase.database.getReference(PRODUCT_POSITION)
    val tagMappingDatabase = Firebase.database.getReference(TAG_MAPPING)

    private val gson = Gson()
    fun findStoreLayout(storeId: String, floorId: String): StateFlow<Array<Array<Int>>?> {

        val resultFlow = MutableStateFlow<Array<Array<Int>>?>(null)
        storeDatabase.child(storeId)
            .get()
            .addOnSuccessListener {

                val store = gson.fromJson(gson.toJson(it.value), Store::class.java)
                val floor = gson.fromJson(gson.toJson(store.floorPlan?.get(floorId) ?: "{}"), FloorPlan::class.java)
                val layoutPlan = Array(floor.rowsCount) { Array(floor.columnsCount) { 0 } }
                floor.rackMapping.values.forEach {rack ->
                    layoutPlan[rack.coordinate.rowId][rack.coordinate.colId] = 1
                }
                resultFlow.value = layoutPlan
        }
        return resultFlow
    }

    fun getProductDetails(storeId: String, productId: String): StateFlow<ProductWithPosition?> {
        val resultFlow = MutableStateFlow<ProductWithPosition?>(null)
        productDatabase.child(productId)
            .get()
            .addOnSuccessListener {
                val product = gson.fromJson(gson.toJson(it.value), Product::class.java)
                productPositionDatabase.child(productId).get().addOnSuccessListener { position ->
                    val productPosition = gson.fromJson(gson.toJson(position.value), ProductPosition::class.java)
                    tagMappingDatabase.child(productPosition.positionTagId).get().addOnSuccessListener { mapping ->
                        val tagMapping = gson.fromJson(gson.toJson(mapping.value), TagMapping::class.java)
                        resultFlow.value = ProductWithPosition(
                            product,
                            productPosition,
                            tagMapping
                        )
                    }
                }
            }
        return resultFlow
    }
}