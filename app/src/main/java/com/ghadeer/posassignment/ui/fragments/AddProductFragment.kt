package com.ghadeer.posassignment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.enums.AddProductFormAttribute
import com.ghadeer.posassignment.data.enums.Status
import com.ghadeer.posassignment.data.model.Category
import com.ghadeer.posassignment.databinding.FragmentAddProductBinding
import com.ghadeer.posassignment.ui.states.AddProductState
import com.ghadeer.posassignment.ui.states.MainState
import com.ghadeer.posassignment.util.*
import com.ghadeer.posassignment.viewmodel.AddProductViewModel
import com.ghadeer.posassignment.viewmodel.CategoriesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddProductFragment : DialogFragment(), AdapterView.OnItemSelectedListener,
    OnEditTextChangeListener, View.OnClickListener {

    companion object {
        const val TAG = "AddProductFragment"
    }

    private lateinit var binding: FragmentAddProductBinding

    private val addProductViewModel: AddProductViewModel by viewModels()
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!showsDialog){
            setToolbarTitle(getString(R.string.add_new_product))
        }
        binding.initViews()
        observeStates()
        categoriesViewModel.observeCategories()

        // For edit mode
        val productId = arguments?.getInt("productId", 0) ?: 0
        if (productId != 0) {
            addProductViewModel.isEditMode = true
            addProductViewModel.setProductState(productId)
            binding.btnAddProduct.text = getString(R.string.update_product)
        }
        addProductViewModel.getProductState()
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
    }

    private fun FragmentAddProductBinding.initViews() {

        titleSection.showIf(showsDialog)
        etBarcode.addTextChangedListener(CustomTextWatcher(etBarcode, this@AddProductFragment))
        etName.addTextChangedListener(CustomTextWatcher(etName, this@AddProductFragment))
        etPrice.addTextChangedListener(CustomTextWatcher(etPrice, this@AddProductFragment))

        spCategory.onItemSelectedListener = this@AddProductFragment

        btnAddCategory.setOnClickListener(this@AddProductFragment)
        btnAddProduct.setOnClickListener(this@AddProductFragment)
        ivClose.setOnClickListener(this@AddProductFragment)
    }


    private fun observeStates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    addProductViewModel.productState.collect {
                        updateProductDetailsState(it)
                    }
                }
                launch {
                    addProductViewModel.submitState.collect {
                        updateSubmitState(it)
                    }
                }

                launch {
                    categoriesViewModel.categoriesState.collect {
                        updateCategoriesState(it)
                    }
                }
            }
        }
    }

    private fun updateProductDetailsState(state: MainState<AddProductState>) {

        when (state.status) {
            Status.Success -> {

                state.data?.let { updateFormFields(it) }
            }

            Status.Failure -> {
                requireActivity().showErrorDialog(
                    title = getString(R.string.error),
                    message = state.message,
                )
                addProductViewModel.clearMessage()
            }

            Status.Idle -> {
            }
        }

    }

    private fun updateSubmitState(state: MainState<Nothing>) {

        binding.btnAddProduct.isEnabled = !state.progress
        when (state.status) {
            Status.Success -> {
                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                when(showsDialog){
                    true -> dismiss()
                    false -> findNavController().navigateUp()
                }
            }

            Status.Failure -> {
                if (state.message.isNotEmpty()) {
                    requireActivity().showErrorDialog(
                        title = getString(R.string.error),
                        message = state.message,
                    )
                }
            }

            Status.Idle -> {
            }
        }
    }

    private fun updateCategoriesState(state: MainState<List<Category>>){
        if (state.status == Status.Success) {
            val categoryNamesList =
                (state.data ?: emptyList()).map { item -> item.name }.toMutableList()
                    .apply {
                        add(0, "-- Select Category --")
                    }
            binding.spCategory.apply {
                adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    categoryNamesList
                )
            }
        }
    }

    private fun updateFormFields(state: AddProductState) {

        binding.apply {

            etBarcode.setText(state.barcode)
            etName.setText(state.name)
            etPrice.setText(state.price.toString())
            val category = categoriesViewModel.categoriesState.value.data?.find { it.id == state.category.id }
            val categoryPosition = categoriesViewModel.categoriesState.value.data?.indexOf(category) ?: 0
            spCategory.setSelection(categoryPosition)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            binding.btnAddCategory.id -> onAddCategoryClicked()
            binding.btnAddProduct.id -> onSubmitClicked()
            binding.ivClose.id -> onCancelClicked()
        }
    }

    private fun onSubmitClicked() {
        val validationResult = addProductViewModel.validateValues()
        validationResult.let {
            if (!it.first) {
                when (it.third) {

                    AddProductFormAttribute.BARCODE -> {
                        binding.etBarcode.error = it.second
                    }

                    AddProductFormAttribute.NAME -> {
                        binding.etBarcode.error = it.second
                    }

                    AddProductFormAttribute.PRICE -> {
                        binding.etBarcode.error = it.second
                    }

                    AddProductFormAttribute.CATEGORY -> {
                        (binding.spCategory.selectedView as TextView).error = it.second
                    }

                    else -> {
                        toast(it.second)
                    }
                }
            } else {
                clearFocus()
                hideKeyboard()
                addProductViewModel.submitForm()
            }

        }
    }

    private fun onAddCategoryClicked() {
        val dialog = AddTextValueDialog.newInstance(
            title = getString(R.string.add_new_category),
            hint = getString(R.string.category_name),
            defaultValue = "",
        )
        dialog.setListener { name ->
            categoriesViewModel.addCategory(name)
            dialog.dismiss()
        }
        dialog.show(requireActivity().supportFragmentManager, AddTextValueDialog.TAG)
    }

    private fun onCancelClicked() {
        dismiss()
    }

    override fun onTextChanged(editText: EditText) {
        when (editText.id) {
            binding.etBarcode.id -> {
                addProductViewModel.setValueOf(
                    AddProductFormAttribute.BARCODE,
                    binding.etBarcode.text.toString()
                )
                binding.etBarcode.error = null
            }

            binding.etName.id -> {
                addProductViewModel.setValueOf(
                    AddProductFormAttribute.NAME,
                    binding.etName.text.toString()
                )
                binding.etName.error = null
            }

            binding.etPrice.id -> {
                val vat = try {
                    binding.etPrice.text.toString().toDouble()
                } catch (e: Exception) {
                    0.0
                }
                addProductViewModel.setValueOf(AddProductFormAttribute.PRICE, vat.toString())
                binding.etPrice.error = null
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        when (parent?.id) {
            binding.spCategory.id -> {

                categoriesViewModel.categoriesState.value.data?.let {

                    val category = Category(
                        when (position) {
                            0 -> -1
                            else -> it[position - 1].id
                        },
                        when (position) {
                            0 -> ""
                            else -> it[position - 1].name
                        }
                    )

                    addProductViewModel.setCategory(category)
                    (binding.spCategory.selectedView as TextView?)?.error = null
                }

            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }


    private fun clearFocus() {
        binding.apply {
            etBarcode.clearFocus()
            etName.clearFocus()
            etPrice.clearFocus()
        }
    }
}