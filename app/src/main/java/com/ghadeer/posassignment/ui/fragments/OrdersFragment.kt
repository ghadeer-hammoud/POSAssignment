package com.ghadeer.posassignment.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.enums.OrderStatus
import com.ghadeer.posassignment.data.enums.PaymentStatus
import com.ghadeer.posassignment.databinding.FragmentOrdersBinding
import com.ghadeer.posassignment.ui.adapters.OnOrderInteractListener
import com.ghadeer.posassignment.ui.adapters.OrdersRecyclerAdapter
import com.ghadeer.posassignment.ui.pagination.MainLoadStateAdapter
import com.ghadeer.posassignment.util.*
import com.ghadeer.posassignment.viewmodel.OrdersViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class OrdersFragment: Fragment(), OnOrderInteractListener, View.OnClickListener {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var ordersAdapter: OrdersRecyclerAdapter

    private val ordersViewModel: OrdersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(getString(R.string.orders_history))
        binding.initViews()
        observeStates()
    }

    private fun FragmentOrdersBinding.initViews() {

        ordersAdapter = OrdersRecyclerAdapter(this@OrdersFragment)


        ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ordersAdapter.withLoadStateFooter(MainLoadStateAdapter())
        }

        tvStartDate.setOnClickListener(this@OrdersFragment)
        tvEndDate.setOnClickListener(this@OrdersFragment)
        ivClearStartDate.setOnClickListener(this@OrdersFragment)
        ivClearEndDate.setOnClickListener(this@OrdersFragment)
        setupSearchView()
        setupOrderStatusSpinner()
        setupPaymentStatusSpinner()
    }

    private fun FragmentOrdersBinding.setupSearchView(){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let { ordersViewModel.setSearchTerm(it) }
                return true
            }
        })
    }

    private fun FragmentOrdersBinding.setupOrderStatusSpinner(){

        spOrderStatus.run {
            val statuses =  mutableListOf<String>().apply{
                add("All")
                addAll(OrderStatus.labels())
            }
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses)

            onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                    val orderStatus = when (position) {
                        0 -> null
                        else -> OrderStatus.values()[position - 1]
                    }
                    ordersViewModel.setOrderStatusFilter(orderStatus)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }
        }
    }

    private fun FragmentOrdersBinding.setupPaymentStatusSpinner(){

        spPaymentStatus.run {
            val statuses =  mutableListOf<String>().apply{
                add("All")
                addAll(PaymentStatus.labels())
            }
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses)

            onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                    val paymentStatus = when (position) {
                        0 -> null
                        else -> PaymentStatus.values()[position - 1]
                    }
                    ordersViewModel.setPaymentStatusFilter(paymentStatus)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }
        }
    }

    private fun observeStates(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    ordersViewModel.ordersFlow.collectLatest {
                        ordersAdapter.submitData(it)
                    }
                }

                launch {
                    ordersAdapter.loadStateFlow.collectLatest { loadStates ->
                        val isEmpty = loadStates.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached && ordersAdapter.itemCount == 0
                        val isLoading = loadStates.refresh is LoadState.Loading
                        updateLoadingState(isLoading, isEmpty, ordersViewModel.isUserSearching())
                    }
                }
            }
        }

        ordersViewModel.ordersTotalCount.observe(viewLifecycleOwner){
            binding.tvTotalOrders.text = getString(R.string.total_x, it)
        }
    }

    private fun updateLoadingState(isLoading: Boolean, isEmpty: Boolean, isSearchResult: Boolean) {

        binding.run {
            progressBar.showIf(isLoading)
            ordersRecyclerView.showIf(!isLoading && !isEmpty)
            noResultsLayout.showIf(!isLoading && isEmpty && !isSearchResult)
            tvNoSearchResultsText.showIf(!isLoading && isEmpty && isSearchResult)
        }

    }



    override fun onOrderClicked(orderId: String) {

    }

    private fun showDatePickerDialogFor(textView: TextView){

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, yearSelected, monthSelected, daySelected ->

                val hour = if(textView.id == binding.tvStartDate.id) 0 else 23
                val minute = if(textView.id == binding.tvStartDate.id) 0 else 59
                val second = if(textView.id == binding.tvStartDate.id) 0 else 59

                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, yearSelected)
                    set(Calendar.MONTH, monthSelected)
                    set(Calendar.DAY_OF_MONTH, daySelected)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, second)
                    set(Calendar.MILLISECOND, 0)
                }
                val dateStr = cal.time.asString()
                when(textView.id){
                    binding.tvStartDate.id -> {
                        ordersViewModel.setStartDate(dateStr)
                        binding.ivClearStartDate.show()
                    }
                    binding.tvEndDate.id -> {
                        ordersViewModel.setEndDate(dateStr)
                        binding.ivClearEndDate.show()
                    }
                }
                textView.text = dateStr.formatDate(newFormat = "dd MMM yyyy")
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun clearStartDateFilter(){
        ordersViewModel.setStartDate(null)
        binding.run {
            tvStartDate.text = getString(R.string._start_date_)
            ivClearStartDate.invisible()
        }
    }

    private fun clearEndDateFilter(){
        ordersViewModel.setEndDate(null)
        binding.run {
            tvEndDate.text = getString(R.string._end_date_)
            ivClearEndDate.invisible()
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.tvStartDate.id -> showDatePickerDialogFor(binding.tvStartDate)
            binding.tvEndDate.id -> showDatePickerDialogFor(binding.tvEndDate)
            binding.ivClearStartDate.id -> clearStartDateFilter()
            binding.ivClearEndDate.id -> clearEndDateFilter()
        }
    }
}