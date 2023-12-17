package com.ghadeer.posassignment.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.ghadeer.posassignment.data.datastore.PrefsManager
import com.ghadeer.posassignment.database.AppDatabase
import com.ghadeer.posassignment.repository.datasources.local.*
import com.ghadeer.posassignment.repository.datasources.remote.CategoryRemoteDataSource
import com.ghadeer.posassignment.repository.datasources.remote.CustomerRemoteDataSource
import com.ghadeer.posassignment.repository.datasources.remote.OrderRemoteDataSource
import com.ghadeer.posassignment.repository.datasources.remote.ProductRemoteDataSource
import com.ghadeer.posassignment.repository.imp.CategoryRepositoryImp
import com.ghadeer.posassignment.repository.imp.CustomerRepositoryImp
import com.ghadeer.posassignment.repository.imp.OrderRepositoryImp
import com.ghadeer.posassignment.repository.imp.ProductRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabaseInstance(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context)

    /**
     * DataStore
     */
    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { appContext.preferencesDataStoreFile("LiteDataStore") })
    }

    @Provides
    @Singleton
    fun providePrefsManager(liteDataStore: DataStore<Preferences>) = PrefsManager(liteDataStore)

    /**
     * Product
     */
    @Provides
    @Singleton
    fun provideProductLocalDataStoreInstance(appDatabase: AppDatabase): ProductLocalDataSource =
        ProductLocalDataSource(appDatabase)

    @Provides
    @Singleton
    fun provideProductRemoteDataStoreInstance(): ProductRemoteDataSource =
        ProductRemoteDataSource()

    @Provides
    @Singleton
    fun provideProductRepositoryInstance(
        productLocalDataSource: ProductLocalDataSource,
        productRemoteDataSource: ProductRemoteDataSource,
    ): ProductRepositoryImp =
        ProductRepositoryImp(productLocalDataSource, productRemoteDataSource)


    /**
     * Category
     */
    @Provides
    @Singleton
    fun provideCategoryLocalDataStoreInstance(appDatabase: AppDatabase): CategoryLocalDataSource =
        CategoryLocalDataSource(appDatabase)

    @Provides
    @Singleton
    fun provideCategoryRemoteDataStoreInstance(): CategoryRemoteDataSource =
        CategoryRemoteDataSource()

    @Provides
    @Singleton
    fun provideCategoryRepositoryInstance(
        categoryLocalDataSource: CategoryLocalDataSource,
        categoryRemoteDataSource: CategoryRemoteDataSource,
    ): CategoryRepositoryImp =
        CategoryRepositoryImp(categoryLocalDataSource, categoryRemoteDataSource)


    /**
     * Order
     */
    @Provides
    @Singleton
    fun provideOrderLocalDataStoreInstance(appDatabase: AppDatabase): OrderLocalDataSource =
        OrderLocalDataSource(appDatabase)

    @Provides
    @Singleton
    fun provideOrderRemoteDataStoreInstance(): OrderRemoteDataSource =
        OrderRemoteDataSource()

    @Provides
    @Singleton
    fun provideOrderRepositoryInstance(
        orderLocalDataSource: OrderLocalDataSource,
        orderItemLocalDataSource: OrderItemLocalDataSource,
        orderRemoteDataSource: OrderRemoteDataSource,
    ): OrderRepositoryImp =
        OrderRepositoryImp(orderLocalDataSource, orderItemLocalDataSource, orderRemoteDataSource)

    /**
     * Order Item
     */
    @Provides
    @Singleton
    fun provideOrderItemLocalDataStoreInstance(appDatabase: AppDatabase): OrderItemLocalDataSource =
        OrderItemLocalDataSource(appDatabase)


    /**
     * Customer
     */
    @Provides
    @Singleton
    fun provideCustomerLocalDataStoreInstance(appDatabase: AppDatabase): CustomerLocalDataSource =
        CustomerLocalDataSource(appDatabase)

    @Provides
    @Singleton
    fun provideCustomerRemoteDataStoreInstance(): CustomerRemoteDataSource =
        CustomerRemoteDataSource()

    @Provides
    @Singleton
    fun provideCustomerRepositoryInstance(
        customerLocalDataSource: CustomerLocalDataSource,
        customerRemoteDataSource: CustomerRemoteDataSource,
    ): CustomerRepositoryImp =
        CustomerRepositoryImp(customerLocalDataSource, customerRemoteDataSource)
}
