package com.ghadeer.posassignment.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ghadeer.posassignment.databinding.DialogApplyDiscountBinding
import com.ghadeer.posassignment.util.InputFilterMinMax
import com.ghadeer.posassignment.util.asMoney
import com.ghadeer.posassignment.viewmodel.CartViewModel

class ApplyDiscountDialog: DialogFragment(), View.OnClickListener {

    companion object{
        const val TAG = "ApplyDiscountDialog"
    }

    private lateinit var binding: DialogApplyDiscountBinding
    private val cartViewModel: CartViewModel by activityViewModels()

    private var discountAmount: Double = 0.0
    private var discountPercentage: Double = 0.0
    private var total: Double = 0.0
    private var newTotal: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogApplyDiscountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT)
    }

    private fun initViews(){

        cartViewModel.cart?.let { cart ->
            this.discountAmount = cart.discountAmount
            this.discountPercentage = cart.discountPercentage
            this.total = cart.getLineTotal()
            this.newTotal = cart.getNetTotal()
        }
        updateUI()

        binding.apply {
            
            etDiscountAmount.filters = arrayOf(InputFilterMinMax(0.0, total))
            etDiscountPercentage.filters = arrayOf(InputFilterMinMax(0.0, 100.0))

            setupAmountValueWatcher()
            setupPercentageValueWatcher()

            btnApply.setOnClickListener(this@ApplyDiscountDialog)
            btnCancel.setOnClickListener(this@ApplyDiscountDialog)
            ivClose.setOnClickListener(this@ApplyDiscountDialog)

        }
    }

    private fun DialogApplyDiscountBinding.setupAmountValueWatcher(){

        etDiscountAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val newValue = when(p0?.toString()?.isEmpty() ?: true){
                    true -> 0.0
                    false -> {
                        try {
                            p0?.toString()?.toDouble() ?: 0.0
                        } catch (e: Exception){
                            0.0
                        }
                    }
                }
                updateDiscountValues(amount = newValue, percentage = -1.0)
            }

        })
    }

    private fun DialogApplyDiscountBinding.setupPercentageValueWatcher(){

        etDiscountPercentage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val newValue = when(p0?.toString()?.isEmpty() ?: true){
                    true -> 0.0
                    false -> {
                        try {
                           p0?.toString()?.toDouble() ?: 0.0
                        } catch (e: Exception){
                            0.0
                        }
                    }
                }
                updateDiscountValues(amount = -1.0, percentage = newValue)
            }

        })
    }

    private fun updateDiscountValues(amount: Double = -1.0, percentage: Double = -1.0){
        when{
            amount      != -1.0 ->  {
                this.discountAmount = amount
                this.discountPercentage = (this.discountAmount * 100 / (cartViewModel.cart?.getLineTotal() ?: 0.0))

            }
            percentage  != -1.0 ->  {
                this.discountPercentage = percentage
                this.discountAmount = ((cartViewModel.cart?.getLineTotal() ?: 0.0) * this.discountPercentage / 100)
            }
        }
        updateTotals()
    }

    private fun updateTotals(){
        this.total = cartViewModel.cart?.getLineTotal() ?: 0.0
        this.newTotal = this.total - this.discountAmount

        updateUI()
    }


    private fun updateUI(){

        binding.apply {
            if(binding.etDiscountAmount.text.toString() != discountAmount.toString() && !binding.etDiscountAmount.hasFocus())
                binding.etDiscountAmount.setText(discountAmount.toString())
            if(binding.etDiscountPercentage.text.toString() != discountPercentage.toString() && !binding.etDiscountPercentage.hasFocus())
                binding.etDiscountPercentage.setText(discountPercentage.toString())
            tvTotal.text = total.asMoney()
            tvNewTotal.text = newTotal.asMoney()
        }
    }

    private fun onApplyClicked(){

        cartViewModel.updateDiscountAmount(this.discountAmount)
        cartViewModel.updateDiscountPercentage(this.discountPercentage)
        dismiss()
    }

    private fun onCancelClicked(){
        dismiss()
    }


    override fun onClick(p0: View?) {
        when(p0?.id){

            binding.btnApply.id -> { onApplyClicked() }
            binding.btnCancel.id -> { onCancelClicked() }
            binding.ivClose.id -> { onCancelClicked() }
        }
    }
}