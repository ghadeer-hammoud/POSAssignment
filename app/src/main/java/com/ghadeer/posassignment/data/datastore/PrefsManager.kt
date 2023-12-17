package com.ghadeer.posassignment.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.ghadeer.posassignment.data.enums.TaxType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class PrefsManager @Inject constructor(private val liteStore: DataStore<Preferences>) {


    suspend fun setTaxType(value: Int) {
        setKey(PreferencesKeys.TAX_TYPE, value)
    }

    suspend fun getTaxType(): Int {
        return getKey(PreferencesKeys.TAX_TYPE) ?: TaxType.TAX_INCLUDED
    }

    suspend fun setTaxValue(value: Double) {
        setKey(PreferencesKeys.TAX_VALUE, value)
    }


    suspend fun getTaxValue(): Double {
        return getKey(PreferencesKeys.TAX_VALUE) ?: 0.0
    }


    private suspend fun <T> getKey(key: Preferences.Key<T>): T? {
        val value: Flow<T?> = liteStore.data.map {
            it[key]
        }
        return value.first()
    }

    private suspend fun <T> setKey(key: Preferences.Key<T>, value: T) {
        liteStore.edit {
            it[key] = value
        }
    }
}

