package com.ghadeer.posassignment.ui.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ghadeer.posassignment.data.model.*
import com.ghadeer.posassignment.repository.imp.CategoryRepositoryImp
import com.ghadeer.posassignment.repository.imp.CustomerRepositoryImp
import com.ghadeer.posassignment.repository.imp.OrderRepositoryImp
import com.ghadeer.posassignment.repository.imp.ProductRepositoryImp
import com.ghadeer.posassignment.util.Mapper

class OrdersPagingSource(
    private val orderRepository: OrderRepositoryImp,
    private val customerRepository: CustomerRepositoryImp,
    private val filtersMap: Map<String, String>,
) : PagingSource<Int, Order>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Order> {
        val page = params.key ?: 0

        return try {
            when(val ordersListResult = orderRepository.getPagedOrdersList(params.loadSize, page * params.loadSize, filtersMap)){
                is DataResult.Success -> {

                    val ordersList = ordersListResult.data.map{
                        Mapper.orderEntityToOrderModel(
                            it,
                            when(val res2 = orderRepository.getOrderItems(it.orderId)){
                                is DataResult.Success -> res2.data
                                is DataResult.Error -> emptyList()
                            },
                            when(val res3 = customerRepository.getCustomerById(it.customerId ?: 0)){
                                is DataResult.Success -> Mapper.customerEntityToCustomerModel(res3.data)
                                is DataResult.Error -> Customer(name = "Visitor")
                            },
                        )
                    }

                    LoadResult.Page(
                        data = ordersList,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (ordersList.isEmpty()) null else page + 1
                    )

                }
                is DataResult.Error -> {
                    LoadResult.Error(Exception(ordersListResult.message))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Order>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}