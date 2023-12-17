package com.ghadeer.posassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.datastore.PrefsManager
import com.ghadeer.posassignment.data.model.view.HomeMenuAction
import com.ghadeer.posassignment.data.model.view.HomeMenuItem
import com.ghadeer.posassignment.ui.states.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefsManager: PrefsManager,
)
: ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    /**
     * MENU ITEMS
     */

    private val homeMenuItems: MutableList<HomeMenuItem> by lazy {
        mutableListOf(
            HomeMenuItem(HomeMenuAction.NEW_ORDER, R.string.new_order, R.drawable.ic_order_add_24),
            HomeMenuItem(HomeMenuAction.ORDERS_HISTORY, R.string.orders_history, R.drawable.ic_orders_24),
            HomeMenuItem(HomeMenuAction.REPORTS, R.string.reports, R.drawable.ic_bar_chart_24),
            HomeMenuItem(HomeMenuAction.PRODUCTS, R.string.products, R.drawable.ic_format_list_bulleted_24),
            HomeMenuItem(HomeMenuAction.CUSTOMERS, R.string.customers, R.drawable.ic_groups_24),
            HomeMenuItem(HomeMenuAction.SETTINGS, R.string.settings, R.drawable.ic_settings_24),
        )
    }

    private val _homeMenuState = MutableStateFlow(MainState<List<HomeMenuItem>>())
    val homeMenuState = _homeMenuState.asStateFlow()

    private val _usernameState = MutableStateFlow(MainState<String>())
    val usernameState = _usernameState.asStateFlow()


    fun observeMenu() = viewModelScope.launch {
        _homeMenuState.update { it.successWith(data = homeMenuItems) }
    }

    fun observeUsername() = viewModelScope.launch {

        val username = "Tester" //prefsManager.getCurrentUserName()
        _usernameState.update { it.successWith(data = username) }
    }

    fun decideDestination(homeMenuAction: HomeMenuAction): Int{
        return when(homeMenuAction){
            HomeMenuAction.NEW_ORDER -> R.id.newOrderFragment
            HomeMenuAction.ORDERS_HISTORY -> R.id.ordersFragment
            HomeMenuAction.REPORTS -> R.id.newOrderFragment
            HomeMenuAction.PRODUCTS -> R.id.productsFragment
            HomeMenuAction.CUSTOMERS -> R.id.customersFragment
            HomeMenuAction.SETTINGS -> R.id.settingsFragment
        }
    }


}