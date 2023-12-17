package com.ghadeer.posassignment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.enums.TaxType
import com.ghadeer.posassignment.data.enums.Status
import com.ghadeer.posassignment.databinding.FragmentSettingsBinding
import com.ghadeer.posassignment.util.setToolbarTitle
import com.ghadeer.posassignment.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment: Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentSettingsBinding

    private val settingsViewModel: SettingsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(getString(R.string.settings))
        binding.initViews()
        observeState()
        settingsViewModel.observeSettings()
    }

    private fun FragmentSettingsBinding.initViews() {

        spTaxType.apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                TaxType.labels()
            )
        }

        spTaxType.onItemSelectedListener = this@SettingsFragment
        layoutTaxValue.setOnClickListener(this@SettingsFragment)

    }

    private fun observeState(){

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    settingsViewModel.taxTypeState.collectLatest{ state ->
                        if(state.status == Status.Success){
                            state.data?.let { binding.spTaxType.setSelection(TaxType.values().indexOf(it)) }
                        }
                    }
                }
                launch {
                    settingsViewModel.taxValueState.collectLatest{ state ->
                        if(state.status == Status.Success){
                            state.data?.let { binding.tvTaxValue.text = it.toString() }
                        }
                    }
                }
            }
        }

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.layoutTaxValue.id -> {
                onTaxValueClicked()
            }
        }
    }

    private fun onTaxValueClicked(){

        val dialog = AddTextValueDialog.newInstance(
            title = getString(R.string.enter_tax_value),
            hint = getString(R.string.tax),
            defaultValue = settingsViewModel.taxValueState.value.data?.toString() ?: "",
        )
        dialog.setListener { value ->
            val taxValue = try { value.toDouble() } catch (e: Exception) { 0.0 }
            settingsViewModel.updateTaxValue(taxValue)
            dialog.dismiss()
        }
        dialog.show(requireActivity().supportFragmentManager, AddTextValueDialog.TAG)
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        when(parent?.id){
            binding.spTaxType.id -> {
                val taxType = TaxType.values()[position]
                settingsViewModel.updateTaxType(taxType)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

}