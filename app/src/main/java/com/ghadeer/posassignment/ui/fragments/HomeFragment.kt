package com.ghadeer.posassignment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.databinding.FragmentHomeBinding
import com.ghadeer.posassignment.data.model.view.HomeMenuAction
import com.ghadeer.posassignment.data.model.view.HomeMenuItem
import com.ghadeer.posassignment.ui.adapters.HomeMenuItemsRecyclerAdapter
import com.ghadeer.posassignment.ui.adapters.OnMenuItemClickListener
import com.ghadeer.posassignment.util.AppVersionUtils
import com.ghadeer.posassignment.util.Constants
import com.ghadeer.posassignment.util.setToolbarTitle
import com.ghadeer.posassignment.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment: Fragment(), OnMenuItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val menuSpanCount = 3

    private lateinit var menuAdapter: HomeMenuItemsRecyclerAdapter

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(getString(R.string.home))
        binding.initViews()
        observeStates()

        homeViewModel.observeMenu()
        homeViewModel.observeUsername()
    }

    private fun FragmentHomeBinding.initViews() {

        menuAdapter = HomeMenuItemsRecyclerAdapter(mutableListOf(), this@HomeFragment)

        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), menuSpanCount)
            adapter = menuAdapter
        }

        tvVersion.text = getString(R.string.version_x, AppVersionUtils.getAppVersionName(requireContext()))
        checkPrinterStatus()
    }

    private fun observeStates(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    homeViewModel.homeMenuState.collectLatest { state ->
                        state.data?.let { menuAdapter.updateItems(it) }
                    }
                }
                launch {
                    homeViewModel.usernameState.collectLatest { state ->
                        state.data?.let { binding.tvLoggedAs.text = getString(R.string.logged_in_as_x, it) }
                    }
                }
            }
        }
    }

    private fun checkPrinterStatus(){
        val isPrinterConfigured = false
        binding.run {
            when(isPrinterConfigured){
                true -> {
                    tvPrinterText.run {
                        text = getString(R.string.printer_configured_text)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.green_600))
                    }
                }
                false -> {
                    tvPrinterText.run {
                        text = getString(R.string.printer_not_configured_text)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.red_600))
                    }
                }
            }
        }
    }

    override fun onMenuItemClicked(homeMenuAction: HomeMenuAction) {
        val destination = homeViewModel.decideDestination(homeMenuAction)
        findNavController().navigate(destination, null, Constants.NAV_OPTIONS)
    }
}