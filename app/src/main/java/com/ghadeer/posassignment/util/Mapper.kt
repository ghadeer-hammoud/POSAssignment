package com.ghadeer.posassignment.util

import com.ghadeer.posassignment.data.model.*
import com.ghadeer.posassignment.database.entities.*

object Mapper {

    fun categoryEntityToCategoryModel(categoryEntity: CategoryEntity)
    = Category(
        id = categoryEntity.id,
        name = categoryEntity.name
    )

    fun categoryModelToCategoryEntity(category: Category)
    = CategoryEntity(
        id = category.id,
        name = category.name
    )

    fun productEntityToProductModel(productEntity: ProductEntity, category: Category)
    = Product(
        id = productEntity.id,
        name = productEntity.name,
        price = productEntity.price,
        barcode = productEntity.barcode,
        category = category,
    )

    fun productModelToProductEntity(product: Product)
    = ProductEntity(
        id = product.id,
        name = product.name,
        price = product.price,
        barcode = product.barcode,
        categoryId = product.category.id,
    )

    fun orderEntityToOrderModel(orderEntity: OrderEntity, items: List<OrderItemEntity>, customer: Customer)
    = Order(
        id = orderEntity.id,
        orderId = orderEntity.orderId,
        orderStatus = orderEntity.orderStatus,
        paymentStatus = orderEntity.paymentStatus,
        customer = customer,
        createdAt = orderEntity.createdAt,
        items = items.map { orderItemEntityToOrderItemModel(it) }
    )

    fun orderModelToOrderEntity(order: Order)
    = OrderEntity(
        id = order.id,
        orderId = order.orderId,
        orderStatus = order.orderStatus,
        paymentStatus = order.paymentStatus,
        customerId = order.customer?.id,
        createdAt = order.createdAt
    )

    fun orderItemEntityToOrderItemModel(orderItemEntity: OrderItemEntity)
    = OrderItem(
        id = orderItemEntity.id,
        orderId = orderItemEntity.orderId,
        productId = orderItemEntity.productId,
        productName = orderItemEntity.productName,
        productBarcode = orderItemEntity.productBarcode,
        quantity = orderItemEntity.quantity,
        price = orderItemEntity.price,
        taxType = orderItemEntity.taxType,
        tax = orderItemEntity.tax,
        lineTotal = orderItemEntity.lineTotal,
        discount = orderItemEntity.discount,
        netAmount = orderItemEntity.netAmount,
        taxAmount = orderItemEntity.taxAmount,
        total = orderItemEntity.total,
    )

    fun orderItemModelToOrderItemEntity(orderItem: OrderItem)
    = OrderItemEntity(
        id = orderItem.id,
        orderId = orderItem.orderId,
        productId = orderItem.productId,
        productName = orderItem.productName,
        productBarcode = orderItem.productBarcode,
        quantity = orderItem.quantity,
        price = orderItem.price,
        taxType = orderItem.taxType,
        tax = orderItem.tax,
        lineTotal = orderItem.lineTotal,
        discount = orderItem.discount,
        netAmount = orderItem.netAmount,
        taxAmount = orderItem.taxAmount,
        total = orderItem.total,
    )

    fun customerModelToCustomerEntity(customer: Customer)
    = CustomerEntity(
        id = customer.id,
        name = customer.name
    )

    fun customerEntityToCustomerModel(customerEntity: CustomerEntity)
    = Customer(
        id = customerEntity.id,
        name = customerEntity.name
    )
}