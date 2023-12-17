package com.ghadeer.posassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghadeer.posassignment.data.enums.AddProductFormAttribute
import com.ghadeer.posassignment.data.model.Category
import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.data.model.Product
import com.ghadeer.posassignment.repository.imp.CategoryRepositoryImp
import com.ghadeer.posassignment.repository.imp.ProductRepositoryImp
import com.ghadeer.posassignment.ui.states.AddProductState
import com.ghadeer.posassignment.ui.states.MainState
import com.ghadeer.posassignment.util.Mapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val productRepository: ProductRepositoryImp,
    private val categoryRepository: CategoryRepositoryImp,
) : ViewModel() {

    companion object {
        private const val TAG = "AddProductViewModel"
    }


    private val _productState = MutableStateFlow(MainState<AddProductState>())
    val productState = _productState.asStateFlow()

    private val _submitState = MutableStateFlow(MainState<Nothing>())
    val submitState = _submitState.asStateFlow()

    var isEditMode = false


    fun getProductState() = viewModelScope.launch {
        _productState.update {
            it.successWith(data = it.data ?: AddProductState())
        }
    }

    fun setProductState(productId: Int) = viewModelScope.launch {
        if (_productState.value.data == null) {
            setProduct(productId)
        }
    }


    /**
     * SET ATTRIBUTES' VALUES
     */
    fun setProduct(productId: Int) = viewModelScope.launch {

        when (val result = productRepository.getProductById(productId)) {
            is DataResult.Success -> {

                val product = result.data
                val category =
                    when (val res = categoryRepository.getCategoryById(product.categoryId)) {
                        is DataResult.Success -> Mapper.categoryEntityToCategoryModel(res.data)
                        is DataResult.Error -> Category()
                    }

                val newState = AddProductState().apply {

                    id = product.id
                    barcode = product.barcode
                    name = product.name
                    price = product.price
                    this.category = category
                }

                _productState.update { it.successWith(data = newState) }
            }

            is DataResult.Error -> {
                _productState.update { it.failureWith(result.message) }
            }
        }
    }

    fun setValueOf(attribute: AddProductFormAttribute, value: String) {
        when (attribute) {

            AddProductFormAttribute.BARCODE -> {
                _productState.value.data?.barcode = value
            }

            AddProductFormAttribute.NAME -> {
                _productState.value.data?.name = value
            }

            AddProductFormAttribute.PRICE -> {
                _productState.value.data?.price = value.toDouble()
            }

            else -> {}
        }
    }


    fun setCategory(category: Category) {
        _productState.value.data?.category = category
    }

    fun clearMessage() {
        _submitState.update { it.copy(message = "") }
    }


    fun submitForm() = viewModelScope.launch {

        _submitState.update { it.showProgress() }
        val validationResult = validateValues()
        if (validationResult.first) {

            // Submit
            _productState.value.data?.let {
                val product = Product(
                    id = it.id,
                    barcode = it.barcode,
                    name = it.name,
                    price = it.price,
                    category = it.category,
                )

                when (isEditMode) {
                    true -> submitUpdateProduct(product)
                    false -> submitAddProduct(product)
                }
            }
        } else {
            _submitState.update {
                it.failureWith(validationResult.second)
            }
        }
    }

    private fun submitAddProduct(product: Product) = viewModelScope.launch {
        when (val result = productRepository.addProduct(Mapper.productModelToProductEntity(product))) {
            is DataResult.Success -> {

                _submitState.update {
                    it.successWith(
                        message = "New product was added successfully."
                    )
                }
                _submitState.update { it.empty() }
                _productState.update { it.empty() }
            }

            is DataResult.Error -> {
                _submitState.update {
                    it.failureWith(result.message)
                }
                _submitState.update { it.empty() }
            }
        }
    }

    private fun submitUpdateProduct(product: Product) = viewModelScope.launch {
        when (val result = productRepository.updateProduct(Mapper.productModelToProductEntity(product))) {
            is DataResult.Success -> {

                _submitState.update {
                    it.successWith(message = "Product was updated successfully.")
                }
                _submitState.update { it.empty() }
                _productState.update { it.empty() }
            }

            is DataResult.Error -> {
                _submitState.update {
                    it.failureWith(result.message)
                }
            }
        }
    }

    /**
     * Validate Form Fields
     */

    fun validateValues(): Triple<Boolean, String, AddProductFormAttribute?> {

        _productState.value.data?.let {

            if (it.barcode.isEmpty())
                return Triple(
                    false,
                    "Please provide a product barcode.",
                    AddProductFormAttribute.BARCODE
                )

            if (it.name.isEmpty())
                return Triple(
                    false,
                    "Please provide a product name.",
                    AddProductFormAttribute.NAME
                )

            if (it.price < 0.0)
                return Triple(
                    false,
                    "Please provide a valid product name.",
                    AddProductFormAttribute.PRICE
                )

            if(it.category.id == 0)
                return Triple(false, "Please select product category.", AddProductFormAttribute.CATEGORY)

            return Triple(true, "Form is valid", null)

        }

        return Triple(false, "Form state is invalid", null)
    }
}
