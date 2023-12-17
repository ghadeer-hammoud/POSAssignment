package com.ghadeer.posassignment.ui.fragments

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.enums.TaxType
import com.ghadeer.posassignment.data.model.CartItem
import com.ghadeer.posassignment.databinding.DialogUpdateCartItemBinding
import com.ghadeer.posassignment.util.*
import com.ghadeer.posassignment.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UpdateCartItemDialog : DialogFragment(), View.OnClickListener, OnEditTextChangeListener, AdapterView.OnItemSelectedListener {

    companion object {
        private const val TAG = "UpdateCartItemDialog"
        fun newInstance(cartItem: CartItem): UpdateCartItemDialog {
            val args = Bundle()
            args.putParcelable("cartItem", cartItem)
            return UpdateCartItemDialog().apply {
                arguments = args
            }
        }
    }

    private lateinit var binding: DialogUpdateCartItemBinding

    private val cartViewModel: CartViewModel by activityViewModels()

    private lateinit var cartItem: CartItem

    private var barcode: String = ""
    private var name: String = ""
    private var price: Double = 0.0
    private var qty: Double = 0.0
    private var tax: Double = 0.0
    private var taxType: Int = 0
    private var discountAmount: Double = 0.0
    private var discountPercentage: Double = 0.0
    private var total: Double = 0.0
    private var totalAfterDiscount: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogUpdateCartItemBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartItem = if (Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable("cartItem", CartItem::class.java)
                ?: Constants.defaultCartItem
        } else {
            arguments?.getParcelable("cartItem") ?: Constants.defaultCartItem
        }

        initViews()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
    }

    private fun initViews() {

        binding.spTaxType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, TaxType.labels())

        cartItem.let {
            barcode = it.barcode
            name = it.productName
            price = it.price
            qty = it.quantity
            tax = it.tax
            taxType = it.taxType
            total = it.lineTotal
            totalAfterDiscount = it.netAmount
            updateDiscountValues(amount = it.discount)
        }

        updateUI()


        binding.apply {
            setupWatchers()
            setupSelectionListeners()

            ivPlus.setOnClickListener(this@UpdateCartItemDialog)
            ivMinus.setOnClickListener(this@UpdateCartItemDialog)
            btnUpdate.setOnClickListener(this@UpdateCartItemDialog)
            ivClose.setOnClickListener(this@UpdateCartItemDialog)
        }
    }


    private fun DialogUpdateCartItemBinding.setupWatchers(){

        listOf(
            etBarcode,
            etName,
            etPrice,
            etTaxPercentage,
            etDiscountAmount,
            etDiscountPercentage,
        ).onEach { editText ->
            editText.addTextChangedListener(CustomTextWatcher(editText, this@UpdateCartItemDialog))
        }
    }

    private fun DialogUpdateCartItemBinding.setupSelectionListeners(){
        spTaxType.onItemSelectedListener = this@UpdateCartItemDialog
    }

    private fun updateDiscountValues(amount: Double = -1.0, percentage: Double = -1.0) {
        when {
            amount != -1.0 -> {
                this.discountAmount = amount
                this.discountPercentage = when (total == 0.0) {
                    true -> 0.0
                    false -> (this.discountAmount * 100 / this.total)
                }

            }

            percentage != -1.0 -> {
                this.discountPercentage = percentage
                this.discountAmount = (this.total * this.discountPercentage / 100)
            }
        }
    }

    private fun revaluateValues() {
        total = qty * price

        total += when(taxType){
            TaxType.TAX_EXCLUDED -> total.times(tax).div(100)
            else -> 0.0
        }
        updateDiscountValues(amount = this.discountAmount)
        totalAfterDiscount = total.minus(discountAmount)
    }


    private fun updateUI() {


        binding.apply {

            etDiscountAmount.filters = arrayOf(InputFilterMinMax(0.0, total))
            etDiscountPercentage.filters = arrayOf(InputFilterMinMax(0.0, 100.0))

            etBarcode.setText(barcode)
            etName.setText(name)
            tvQty.text = qty.toString()
            tvTotal.text = total.asMoney()


            if (etPrice.text.toString() != price.toString() && !etPrice.hasFocus())
                etPrice.setText(price.toString())
            if (etTaxPercentage.text.toString() != tax.toString() && !etTaxPercentage.hasFocus())
                etTaxPercentage.setText(tax.toString())
            if (etDiscountAmount.text.toString() != discountAmount.toString() && !etDiscountAmount.hasFocus())
                etDiscountAmount.setText(discountAmount.toString())
            if (etDiscountPercentage.text.toString() != discountPercentage.toString() && !etDiscountPercentage.hasFocus())
                etDiscountPercentage.setText(discountPercentage.toString())

            spTaxType.setSelection(TaxType.values().indexOf(taxType))

            when (discountAmount > 0.0) {
                true -> {
                    tvTotal.apply {
                        if (paintFlags and Paint.STRIKE_THRU_TEXT_FLAG != Paint.STRIKE_THRU_TEXT_FLAG) {
                            paintFlags = paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG
                        }
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_400))
                    }
                    tvTotalAfterDiscount.apply {
                        show()
                        text = totalAfterDiscount.asMoney()
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.red_900))
                    }
                }

                false -> {
                    tvTotal.apply {
                        if (paintFlags and Paint.STRIKE_THRU_TEXT_FLAG == Paint.STRIKE_THRU_TEXT_FLAG) {
                            paintFlags = paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG
                        }
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    }
                    tvTotalAfterDiscount.hide()
                }
            }

        }
    }

    private fun clearDiscount() {
        this.discountAmount = 0.0
        this.discountPercentage = 0.0
    }

    private fun clearFocus() {
        binding.apply {
            etPrice.clearFocus()
            etTaxPercentage.clearFocus()
            etDiscountAmount.clearFocus()
            etDiscountPercentage.clearFocus()
            etBarcode.clearFocus()
            etName.clearFocus()
        }
    }

    private fun validateValues(): Boolean {
        return when {
            name.isBlank() -> {
                toast("Product name cannot be empty.")
                false
            }
            barcode.isBlank() -> {
                toast("Product barcode cannot be empty.")
                false
            }

            qty <= 0.0 -> {
                toast("Please enter a valid quantity value.")
                false
            }


            else -> true
        }
    }


    private fun onPlusClicked() {
        clearFocus()
        qty = qty.plus(1.0)
        clearDiscount()
        revaluateValues()
        updateUI()
    }

    private fun onMinusClicked() {
        clearFocus()
        qty = when (qty > 0) {
            true -> qty.minus(1.0)
            false -> qty
        }
        clearDiscount()
        revaluateValues()
        updateUI()
    }

    private fun onApplyClicked() {

        if (!validateValues()) {
            return
        }

        if (cartViewModel.hasTotalDiscount()) {
            // ITEM'S DISCOUNT HAS BEEN UPDATED
            // -> CLEAR TOTAL DISCOUNT AND KEEP THIS ITEM DISCOUNT
            cartViewModel.clearDiscount()
        }

        cartItem.apply {
            barcode = this@UpdateCartItemDialog.barcode
            productName = this@UpdateCartItemDialog.name
            price = this@UpdateCartItemDialog.price
            quantity = this@UpdateCartItemDialog.qty
            tax = this@UpdateCartItemDialog.tax
            taxType = this@UpdateCartItemDialog.taxType
            discount = this@UpdateCartItemDialog.discountAmount
            calculateValues()
        }


        when (cartItem.quantity > 0) {
            true -> cartViewModel.notifyUpdateItem(
                cartItem.productId,
            )

            false -> cartViewModel.removeCartItem(
                cartItem.productId,
            )
        }
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {

            binding.ivPlus.id -> {
                onPlusClicked()
            }

            binding.ivMinus.id -> {
                onMinusClicked()
            }

            binding.btnUpdate.id -> {
                onApplyClicked()
            }

            binding.ivClose.id -> {
                onCancelClicked()
            }
        }
    }

    /**
     * EditText watchers
     */
    override fun onTextChanged(editText: EditText) {

        val editable = editText.text

        binding.run {
            when(editText.id){
                etBarcode.id -> { barcode = editable.castToString() }
                etName.id -> { name = editable.castToString() }
                etPrice.id -> {
                    price = editable.castToDouble()
                    clearDiscount()
                    revaluateValues()
                    updateUI()
                }
                etTaxPercentage.id -> {
                    tax = editable.castToDouble()
                    clearDiscount()
                    revaluateValues()
                    updateUI()
                }
                etDiscountAmount.id -> {
                    val newValue = editable.castToDouble()
                    updateDiscountValues(amount = newValue, percentage = -1.0)
                    revaluateValues()
                    updateUI()
                }
                etDiscountPercentage.id -> {
                    val newValue = editable.castToDouble()
                    updateDiscountValues(amount = -1.0, percentage = newValue)
                    revaluateValues()
                    updateUI()
                }
            }
        }
    }

    /**
     * Spinner select listener
     */
    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

        when(parent?.id){

            binding.spTaxType.id -> {

                taxType = TaxType.values()[position]
                revaluateValues()
                updateUI()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}