package com.ghadeer.posassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghadeer.posassignment.data.datastore.PrefsManager
import com.ghadeer.posassignment.ui.states.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsManager: PrefsManager,
)
: ViewModel() {

    companion object {
        private const val TAG = "SettingsViewModel"
    }

    private val _taxTypeState = MutableStateFlow(MainState<Int>())
    val taxTypeState = _taxTypeState.asStateFlow()

    private val _taxValueState = MutableStateFlow(MainState<Double>())
    val taxValueState = _taxValueState.asStateFlow()


    fun observeSettings() = viewModelScope.launch {

        _taxTypeState.update { it.successWith(data = prefsManager.getTaxType()) }
        _taxValueState.update { it.successWith(data = prefsManager.getTaxValue()) }

    }

    fun updateTaxType(taxType: Int) = viewModelScope.launch {
        prefsManager.setTaxType(taxType)
    }

    fun updateTaxValue(taxValue: Double) = viewModelScope.launch {
        prefsManager.setTaxValue(taxValue)
        _taxValueState.update { it.successWith(data = prefsManager.getTaxValue()) }
    }


    fun getTaxTypePref() = runBlocking {
        prefsManager.getTaxType()
    }

    fun getTaxValuePref() = runBlocking {
        prefsManager.getTaxValue()
    }
}