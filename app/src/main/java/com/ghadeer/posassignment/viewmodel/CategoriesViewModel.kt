package com.ghadeer.posassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.enums.Status
import com.ghadeer.posassignment.data.model.Category
import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.repository.imp.CategoryRepositoryImp
import com.ghadeer.posassignment.ui.states.MainState
import com.ghadeer.posassignment.util.Mapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositoryImp,
)
: ViewModel() {

    companion object {
        private const val TAG = "CategoriesViewModel"
    }

    private val _categoriesState = MutableStateFlow(MainState<List<Category>>())
    val categoriesState = _categoriesState.asStateFlow()

    private val _addCategoryState = MutableStateFlow(MainState<Nothing>())
    val addCategoryState = _addCategoryState.asStateFlow()

    fun observeCategories() = viewModelScope.launch {
        _categoriesState.update { it.showProgress() }

        categoryRepository.getCategories().collectLatest { entitiesList ->

            val categoriesList = entitiesList.map { Mapper.categoryEntityToCategoryModel(it) }
            _categoriesState.update {
                it.successWith(data = categoriesList)
            }
        }

    }

    fun addCategory(name: String) = viewModelScope.launch {
        _addCategoryState.update { it.showProgress() }


        val category = Category(name = name)

        when(val result = categoryRepository.addCategory(Mapper.categoryModelToCategoryEntity(category))){
            is DataResult.Success -> {
//                val categoriesList = _categoriesState.value.data?.toMutableList()?.apply {
//                    add(category.copy(id = result.data))
//                }
//                _categoriesState.update { it.copy(data = categoriesList) }
                _addCategoryState.update { it.copy(progress = false, status = Status.Success) }
                _addCategoryState.update { it.empty() }
            }
            is DataResult.Error -> {
                _addCategoryState.update { it.failureWith(result.message) }
            }
        }
    }

    fun validateCategoryName(categoryName: String): Pair<Boolean, Int?>{
        if(categoryName.isEmpty())
            return Pair(false, R.string.validation_category_name_empty_message)
        return Pair(true, null)
    }
}