package com.ghadeer.posassignment.data.datastore

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object PreferencesKeys {
    val TAX_TYPE = intPreferencesKey("tax_type")
    val TAX_VALUE = doublePreferencesKey("tax_value")
}