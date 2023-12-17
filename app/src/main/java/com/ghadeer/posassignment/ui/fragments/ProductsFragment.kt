package com.ghadeer.posassignment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.enums.Status
import com.ghadeer.posassignment.data.model.Category
import com.ghadeer.posassignment.databinding.FragmentProductsBinding
import com.ghadeer.posassignment.databinding.FragmentSalesProductsBinding
import com.ghadeer.posassignment.ui.adapters.OnProductInteractListener
import com.ghadeer.posassignment.ui.adapters.ProductsRecyclerAdapter
import com.ghadeer.posassignment.ui.pagination.MainLoadStateAdapter
import com.ghadeer.posassignment.ui.states.MainState
import com.ghadeer.posassignment.util.*
import com.ghadeer.posassignment.viewmodel.CategoriesViewModel
import com.ghadeer.posassignment.viewmodel.ProductsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductsFragment: Fragment(), OnProductInteractListener {

    private lateinit var binding: FragmentProductsBinding
    private lateinit var productsAdapter: ProductsRecyclerAdapter
    private val gridSpanCount = 2

    private val productsViewModel: ProductsViewModel by activityViewModels()
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(getString(R.string.products))
        binding.initViews()
        observeStates()
        categoriesViewModel.observeCategories()
    }

    private fun FragmentProductsBinding.initViews() {

        productsAdapter = ProductsRecyclerAdapter(this@ProductsFragment)

        btnAdd.setOnClickListener { onAddProductClicked() }
        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), gridSpanCount)
            adapter = productsAdapter.withLoadStateFooter(MainLoadStateAdapter())
        }

        setupSearchView()
        setupCategorySpinner()
    }

    private fun FragmentProductsBinding.setupCategorySpinner(){

        spCategoryFilter.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                categoriesViewModel.categoriesState.value.data?.let {

                    val categoryId = when (position) {
                        0 -> null
                        else -> it[position - 1].id
                    }
                    productsViewModel.setCategoryFilter(categoryId)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
    }

    private fun FragmentProductsBinding.setupSearchView(){

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let { productsViewModel.setSearchTerm(it) }
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
                    productsViewModel.productsFlow.collectLatest {
                        productsAdapter.submitData(it)
                    }
                }

                launch {
                    productsAdapter.loadStateFlow.collectLatest { loadStates ->
                        val isEmpty = loadStates.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached && productsAdapter.itemCount == 0
                        val isLoading = loadStates.refresh is LoadState.Loading
                        updateLoadingState(isLoading, isEmpty, productsViewModel.isUserSearching())
                    }
                }
            }
        }

        productsViewModel.productsTotalCount.observe(viewLifecycleOwner){
            binding.tvTotalProducts.text = getString(R.string.total_products_x, it)
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

    private fun updateLoadingState(isLoading: Boolean, isEmpty: Boolean, isSearchResult: Boolean) {

        binding.run {
            progressBar.showIf(isLoading)
            recyclerView.showIf(!isLoading && !isEmpty)
            noResultsLayout.showIf(!isLoading && isEmpty && !isSearchResult)
            tvNoSearchResultsText.showIf(!isLoading && isEmpty && isSearchResult)
        }

    }


    private fun onAddProductClicked(){
        findNavController().navigate(
            R.id.addProductFragment,
            null,
            Constants.NAV_OPTIONS
        )
    }


    override fun onProductClicked(productId: Int) {

    }

    override fun onProductEditClicked(productId: Int) {
        findNavController().navigate(
            R.id.addProductFragment,
            bundleOf("productId" to productId),
            Constants.NAV_OPTIONS
        )
    }

    override fun onProductDeleteClicked(productId: Int) {
        productsViewModel.deleteProduct(productId)
    }
}