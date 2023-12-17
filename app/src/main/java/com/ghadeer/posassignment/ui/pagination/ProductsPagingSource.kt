package com.ghadeer.posassignment.ui.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ghadeer.posassignment.data.model.Category
import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.data.model.Product
import com.ghadeer.posassignment.repository.imp.CategoryRepositoryImp
import com.ghadeer.posassignment.repository.imp.ProductRepositoryImp
import com.ghadeer.posassignment.util.Mapper

class ProductsPagingSource(
    private val productRepository: ProductRepositoryImp,
    private val categoryRepository: CategoryRepositoryImp,
    private val filtersMap: Map<String, String>,
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 0

        return try {
            when(val productsListResult = productRepository.getPagedProductsList(params.loadSize, page * params.loadSize, filtersMap)){
                is DataResult.Success -> {

                    val productsList = productsListResult.data.map{
                        Mapper.productEntityToProductModel(
                            it,
                            when(val res2 = categoryRepository.getCategoryById(it.categoryId)){
                                is DataResult.Success -> Mapper.categoryEntityToCategoryModel(res2.data)
                                is DataResult.Error -> Category(name = "Unknown")
                            }
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

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}