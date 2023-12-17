package com.ghadeer.posassignment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.enums.Status
import com.ghadeer.posassignment.data.model.CartItem
import com.ghadeer.posassignment.data.model.Category
import com.ghadeer.posassignment.data.model.SalesProduct
import com.ghadeer.posassignment.databinding.FragmentSalesProductsBinding
import com.ghadeer.posassignment.ui.adapters.OnSalesProductInteractListener
import com.ghadeer.posassignment.ui.adapters.SalesProductsRecyclerAdapter
import com.ghadeer.posassignment.ui.pagination.MainLoadStateAdapter
import com.ghadeer.posassignment.ui.states.MainState
import com.ghadeer.posassignment.util.showIf
import com.ghadeer.posassignment.viewmodel.CartViewModel
import com.ghadeer.posassignment.viewmodel.CategoriesViewModel
import com.ghadeer.posassignment.viewmodel.SalesProductsViewModel
import com.ghadeer.posassignment.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SalesProductsFragment: Fragment(), OnSalesProductInteractListener {

    private lateinit var binding: FragmentSalesProductsBinding
    private lateinit var productsAdapter: SalesProductsRecyclerAdapter
    private val gridSpanCount = 3

    private val salesProductsViewModel: SalesProductsViewModel by activityViewModels()
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalesProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initViews()
        observeStates()
        categoriesViewModel.observeCategories()
    }

    private fun FragmentSalesProductsBinding.initViews() {

        productsAdapter = SalesProductsRecyclerAdapter(this@SalesProductsFragment)

        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), gridSpanCount)
            adapter = productsAdapter.withLoadStateFooter(MainLoadStateAdapter())
        }

        ivAddProduct.setOnClickListener { AddProductFragment().show(requireActivity().supportFragmentManager, AddProductFragment.TAG) }

        setupCategorySpinner()
        setupSearchView()

    }

    private fun FragmentSalesProductsBinding.setupCategorySpinner(){

        spCategoryFilter.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                categoriesViewModel.categoriesState.value.data?.let {

                    val categoryId = when (position) {
                        0 -> null
                        else -> it[position - 1].id
                    }
                    salesProductsViewModel.setCategoryFilter(categoryId)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
    }

    private fun FragmentSalesProductsBinding.setupSearchView(){

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let { salesProductsViewModel.setSearchTerm(it) }
                return true
            }

        })
    }

    private fun observeStates(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){

                launch {
                    categoriesViewModel.categoriesState.collect {
                        updateCategoriesState(it)
                    }
                }

                launch {
                    salesProductsViewModel.productsFlow.collectLatest {
                        productsAdapter.submitData(it)
                    }
                }

                launch {
                    productsAdapter.loadStateFlow.collectLatest { loadStates ->
                        val isEmpty = loadStates.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached && productsAdapter.itemCount == 0
                        val isLoading = loadStates.refresh is LoadState.Loading
                        updateLoadingState(isLoading, isEmpty, salesProductsViewModel.isUserSearching())
                    }
                }
            }
        }

        salesProductsViewModel.productsTotalCount.observe(viewLifecycleOwner){
            binding.tvTotalProducts.text = getString(R.string.total_products_x, it)
        }
    }

    private fun updateLoadingState(isLoading: Boolean, isEmpty: Boolean, isSearchResult: Boolean) {

        binding.run {
            progressBar.showIf(isLoading)
            recyclerView.showIf(!isLoading && !isEmpty)
            noResultsLayout.showIf(!isLoading && isEmpty && !isSearchResult)
            tvNoSearchResultsText.showIf(!isLoading && isEmpty && isSearchResult)
        }

    }

    private fun updateCategoriesState(state: MainState<List<Category>>){
        if (state.status == Status.Success) {
            val categoryNamesList =
                (state.data ?: emptyList()).map { item -> item.name }.toMutableList()
                    .apply {
                        add(0, "All")
                    }
            binding.spCategoryFilter.apply {
                adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    categoryNamesList
                )
            }
        }
    }

    override fun onSalesProductClicked(salesProduct: SalesProduct) {
        cartViewModel.addCartItem(
            CartItem(
                productId = salesProduct.id,
                productName = salesProduct.name,
                barcode = salesProduct.barcode,
                quantity = 1.0,
                price = salesProduct.price,
                taxType = settingsViewModel.getTaxTypePref(),
                tax = settingsViewModel.getTaxValuePref(),
            )
        )
    }

    override fun onSalesProductLongClicked(salesProduct: SalesProduct) {

    }

}