package com.ghadeer.posassignment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.model.Customer
import com.ghadeer.posassignment.databinding.FragmentCustomersBinding
import com.ghadeer.posassignment.ui.adapters.OnCustomerInteractListener
import com.ghadeer.posassignment.ui.adapters.CustomersRecyclerAdapter
import com.ghadeer.posassignment.ui.pagination.MainLoadStateAdapter
import com.ghadeer.posassignment.util.*
import com.ghadeer.posassignment.viewmodel.CustomersViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class CustomersFragment: DialogFragment(), OnCustomerInteractListener {


    companion object{
        const val TAG = "CustomersFragment"
    }

    private lateinit var binding: FragmentCustomersBinding
    private lateinit var customersAdapter: CustomersRecyclerAdapter
    private var gridSpanCount = 2

    private val customersViewModel: CustomersViewModel by activityViewModels()

    private var onCustomerSelectedListener: ((Customer) -> Unit)? = null

    private var isPickMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(getString(R.string.customers))
        isPickMode = showsDialog
        binding.initViews()
        observeStates()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            resources.configuration.screenWidthDp / 2,
            ConstraintLayout.LayoutParams.MATCH_PARENT)
    }

    private fun FragmentCustomersBinding.initViews() {

        customersAdapter = CustomersRecyclerAdapter(isPickMode, this@CustomersFragment)

        titleSection.showIf(showsDialog)
        tvTitle.text = when(isPickMode){
            true -> getString(R.string.select_customer)
            false -> getString(R.string.customers)
        }
        btnAdd.setOnClickListener { onAddCustomerClicked() }
        ivClose.setOnClickListener { dismiss() }
        recyclerView.apply {
            layoutManager = when(showsDialog){
                true -> LinearLayoutManager(requireContext())
                false -> GridLayoutManager(requireContext(), gridSpanCount)
            }
            adapter = customersAdapter.withLoadStateFooter(MainLoadStateAdapter())
        }

        setupSearchView()
    }

    private fun FragmentCustomersBinding.setupSearchView(){

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let { customersViewModel.setSearchTerm(it) }
                return true
            }

        })
    }

    private fun observeStates(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    customersViewModel.customersFlow.collectLatest {
                        customersAdapter.submitData(it)
                    }
                }

                launch {
                    customersAdapter.loadStateFlow.collectLatest { loadStates ->
                        val isEmpty = loadStates.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached && customersAdapter.itemCount == 0
                        val isLoading = loadStates.refresh is LoadState.Loading
                        updateLoadingState(isLoading, isEmpty, customersViewModel.isUserSearching())
                    }
                }
            }
        }

        customersViewModel.customersTotalCount.observe(viewLifecycleOwner){
            binding.tvTotalCustomers.text = getString(R.string.total_customers_x, it)
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


    private fun onAddCustomerClicked(){
        val dialog = AddTextValueDialog.newInstance(
            title = getString(R.string.add_new_customer),
            hint = getString(R.string.customer_name),
            defaultValue = "",
        )
        dialog.setListener { name ->
            customersViewModel.addCustomer(name)
            dialog.dismiss()
        }
        dialog.show(requireActivity().supportFragmentManager, AddTextValueDialog.TAG)
    }


    override fun onCustomerClicked(customer: Customer) {

        if(isPickMode){
            onCustomerSelectedListener?.invoke(customer)
            dismiss()
        }
    }

    override fun onCustomerEditClicked(customerId: Int) {
        val dialog = AddTextValueDialog.newInstance(
            title = getString(R.string.edit_customer),
            hint = getString(R.string.new_customer_name),
            defaultValue = "",
        )
        dialog.setListener { name ->
            customersViewModel.updateCustomerName(customerId, name)
            dialog.dismiss()
        }
        dialog.show(requireActivity().supportFragmentManager, AddTextValueDialog.TAG)
    }

    override fun onCustomerDeleteClicked(customerId: Int) {
        customersViewModel.deleteCustomer(customerId)
    }



    fun setListener(listener: (Customer) -> Unit){
        onCustomerSelectedListener = listener
    }
}