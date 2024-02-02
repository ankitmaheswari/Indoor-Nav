package com.indoornav.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indoornav.business.store.Product
import com.indoornav.business.store.ProductWithPosition
import com.indoornav.repository.ShortestPathFinder
import com.indoornav.repository.StoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FloorPlanViewModel: ViewModel() {

    private val storeRepository: StoreRepository = StoreRepository()

    private val _screenState = MutableStateFlow(ScreenState.LOADING)
    val screenState = _screenState.asStateFlow()

    private val _isPathFetched: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isPathFetched = _isPathFetched.asStateFlow()

    private var floorPlan: Array<Array<Int>>? = null
    private var shortestPath: ArrayList<ArrayList<Int>>? = null
    private var productDetails: ProductWithPosition? = null
    private var destinationRow: Int? = null
    private var destinationColumn: Int? = null

    fun getFloorPlan(start: Array<Int>, storeId: String, floorId: String, productId: String) {
        viewModelScope.launch {
            _screenState.value = ScreenState.LOADING
            storeRepository.findStoreLayout(storeId, floorId).collectLatest {
                if (it != null) {
                    floorPlan = it
                    getShortestPath(start, productId, storeId)
                }
            }
        }
    }

    fun getProductDetails(productId: String, storeId: String): Product? {
        return productDetails?.product
    }

    fun getShortestPath(start: Array<Int>, productId: String, storeId: String) {
        _isPathFetched.value = false
        viewModelScope.launch(Dispatchers.IO) {
            val productWithPosition = storeRepository.getProductDetails(storeId, productId)
            productWithPosition.collectLatest {productInfo ->
                if (productInfo != null) {
                    productDetails = productInfo
                    val rackId = productInfo.tagMapping.rackId
                    val rackRow = rackId[0].digitToInt()
                    val rackColumn = rackId[1].digitToInt()
                    val dest: Array<Int> = if (!hasShelf(rackRow, rackColumn-1)) {
                        destinationRow = rackRow
                        destinationColumn = rackColumn - 1
                        arrayOf(rackRow, rackColumn-1)
                    } else {
                        destinationRow = rackRow
                        destinationColumn = rackColumn + 1
                        arrayOf(rackRow, rackColumn+1)
                    }
                    val arrayList = ArrayList<ArrayList<Int>>()
                    floorPlan?.forEach {
                        val list = ArrayList<Int>()
                        it.forEach { data ->
                            list.add(data)
                        }
                        arrayList.add(list)
                    }
                    val shortestPathFound = ShortestPathFinder(arrayList).findOptimalPath(
                        start.toIntArray(), dest.toIntArray()
                    )
                    if (shortestPathFound.isNotEmpty()) {
                        Log.d("Shortest Path", "$shortestPathFound")
                        shortestPath = shortestPathFound
                    }
                    _isPathFetched.value = true
                    _screenState.value = ScreenState.SUCCESS
                }
            }
        }
    }

    fun getRows() = floorPlan?.size ?: 0
    fun getColumns() = floorPlan?.get(0)?.size ?: 0

    fun hasShelf(row: Int, column: Int): Boolean {
        if (floorPlan != null && row > 0 && row < floorPlan!!.size && column > 0 && column < floorPlan!![0].size) {
            return floorPlan!![row][column] == 1
        }
        return false
    }

    fun isInPath(row: Int, column: Int): Boolean {
        if (!floorPlan.isNullOrEmpty() && !shortestPath.isNullOrEmpty()) {
            return shortestPath?.firstOrNull { it[0] == row && it[1] == column } != null
        }
        return false
    }

    fun isDestination(row: Int, column: Int): Boolean {
        return row == destinationRow && column == destinationColumn
    }

    fun getLabel(row: Int, column: Int): String? {
        if (row == 2 && column == 1) {
            return "10% Off"
        }
        if (row == 2 && column == 4) {
            return "20% Off"
        }
        return null
    }

}

enum class ScreenState {
    LOADING,
    SUCCESS,
    ERROR,
    DEFAULT
}