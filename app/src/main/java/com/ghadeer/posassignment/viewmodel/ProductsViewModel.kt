package com.ghadeer.posassignment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ghadeer.posassignment.data.model.Product
import com.ghadeer.posassignment.repository.imp.CategoryRepositoryImp
import com.ghadeer.posassignment.repository.imp.ProductRepositoryImp
import com.ghadeer.posassignment.ui.pagination.ProductsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepositoryImp,
    private val categoryRepository: CategoryRepositoryImp,
)
: ViewModel() {

    companion object {
        private const val TAG = "ProductsViewModel"
    }

    init {
        observeProducts()
    }

    private var searchTerm: String = ""
    private var categoriesFilterList: MutableList<Int> = mutableListOf()

    private val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            initialLoadSize = 60
        ),
        pagingSourceFactory = {
            currentPagingSource = ProductsPagingSource(
                productRepository,
                categoryRepository,
                mapOf(
                    "searchTerm" to searchTerm,
                    "categories" to categoriesFilterList.joinToString(","),
                ),
            )

            currentPagingSource!!
        }
    )

    private var currentPagingSource: ProductsPagingSource? = null

    val productsFlow: Flow<PagingData<Product>> = pager.flow.cachedIn(viewModelScope)

    val productsTotalCount = MutableLiveData(0)


    private fun observeProducts() = viewModelScope.launch {
        productRepository.getProductsFlow().collectLatest {
            productsTotalCount.value = it.size
            refreshData()
        }
    }

    private fun refreshData() {
        currentPagingSource?.invalidate()
    }

    fun setSearchTerm(searchTerm: String){
        this.searchTerm = searchTerm
        refreshData()
    }

    fun setCategoryFilter(categoryId: Int?){
        this.categoriesFilterList.clear()
        categoryId?.let { this.categoriesFilterList.add(it) }
        refreshData()
    }

    fun isUserSearching() = searchTerm.isNotEmpty() || categoriesFilterList.isNotEmpty()

    fun deleteProduct(productId: Int) = viewModelScope.launch {
        productRepository.deleteProduct(productId)
    }
}