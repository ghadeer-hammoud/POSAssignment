package com.ghadeer.posassignment.ui.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.data.model.Customer
import com.ghadeer.posassignment.repository.imp.CustomerRepositoryImp
import com.ghadeer.posassignment.util.Mapper

class CustomersPagingSource(
    private val customerRepository: CustomerRepositoryImp,
    private val filtersMap: Map<String, String>,
) : PagingSource<Int, Customer>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Customer> {
        val page = params.key ?: 0

        return try {
            when(val customersListResult = customerRepository.getPagedCustomersList(params.loadSize, page * params.loadSize, filtersMap)){
                is DataResult.Success -> {

                    val customersList = customersListResult.data.map{ Mapper.customerEntityToCustomerModel(it) }
                    LoadResult.Page(
                        data = customersList,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (customersList.isEmpty()) null else page + 1
                    )

                }
                is DataResult.Error -> {
                    LoadResult.Error(Exception(customersListResult.message))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Customer>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}