package com.ghadeer.posassignment.ui.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.data.model.SalesProduct
import com.ghadeer.posassignment.repository.imp.ProductRepositoryImp

class SalesProductsPagingSource(
    private val productRepository: ProductRepositoryImp,
    private val filtersMap: Map<String, String>,
) : PagingSource<Int, SalesProduct>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SalesProduct> {
        val page = params.key ?: 0

        return try {
            when(val productsListResult = productRepository.getPagedProductsList(params.loadSize, page * params.loadSize, filtersMap)){
                is DataResult.Success -> {

                    val productsList = productsListResult.data.map{
                        SalesProduct(
                            id = it.id,
                            barcode = it.barcode,
                            name = it.name,
                            price = it.price,
                            qtyInCart = 0.0,
                            qtyInStock = 0.0
                        )
                    }

                    LoadResult.Page(
                        data = productsList,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (productsList.isEmpty()) null else page + 1
                    )

                }
                is DataResult.Error -> {
                    LoadResult.Error(Exception(productsListResult.message))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SalesProduct>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}