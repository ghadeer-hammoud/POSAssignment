package com.ghadeer.posassignment.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.model.Cart
import com.ghadeer.posassignment.data.enums.CartActionType
import com.ghadeer.posassignment.data.enums.Status
import com.ghadeer.posassignment.databinding.FragmentCartBinding
import com.ghadeer.posassignment.ui.adapters.CartItemsRecyclerAdapter
import com.ghadeer.posassignment.ui.adapters.OnCartItemInteractListener
import com.ghadeer.posassignment.ui.states.MainState
import com.ghadeer.posassignment.util.*
import com.ghadeer.posassignment.viewmodel.CartViewModel
import com.ghadeer.posassignment.viewmodel.CheckoutViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CartFragment: Fragment(), OnCartItemInteractListener, View.OnClickListener {

    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by activityViewModels()
    private val checkoutViewModel: CheckoutViewModel by activityViewModels()

    private lateinit var cartAdapter: CartItemsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(getString(R.string.new_order))
        setupMenu()
        binding.initViews()
        observeStates()
    }

    private fun setupMenu(){

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_cart, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {

                        findNavController().navigate(
                            R.id.settingsFragment,
                            null,
                            Constants.NAV_OPTIONS
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun FragmentCartBinding.initViews() {

        cartAdapter = CartItemsRecyclerAdapter(mutableListOf(), this@CartFragment)

        cartItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }

        tvCustomerName.text = getString(R.string.customer_x, getString(R.string.visitor) )
        btnClearCart.setOnClickListener(this@CartFragment)
        btnApplyDiscount.setOnClickListener(this@CartFragment)
        btnCheckout.setOnClickListener(this@CartFragment)
        btnAssignCustomer.setOnClickListener(this@CartFragment)
        ivClearCustomer.setOnClickListener(this@CartFragment)
    }

    private fun observeStates() {

        cartViewModel.cartNotifier.observe(viewLifecycleOwner){ cartAction ->
            when (cartAction.action) {
                CartActionType.ACTION_NOTHING -> {}
                CartActionType.ACTION_ADD,
                CartActionType.ACTION_REMOVE,
                CartActionType.ACTION_UPDATE_ITEM,
                CartActionType.ACTION_UPDATE_DISCOUNT,
                CartActionType.ACTION_UPDATE_CUSTOMER,
                CartActionType.ACTION_CLEAR,
                CartActionType.ACTION_FILL -> {
                    updateCartState(cartViewModel.cart)
                }
            }

            binding.cartEmptyLayout.showIf(cartViewModel.cart?.isEmpty() ?: true)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                checkoutViewModel.checkoutState.collect {
                    updateCheckoutState(it)
                }
            }
        }
    }

    private fun updateCartState(cart: Cart?){
        cartAdapter.updateItems(cart?.items ?: listOf())
        binding.run {
            tvTotalItems.text = (cart?.getItemsCount() ?: 0).toString()
            tvQuantity.text = (cart?.getItemsQuantity() ?: 0.0).toString()
            tvSubtotal.text = (cart?.getNetTotal() ?: 0.0).asMoney()
            tvTax.text = (cart?.getTotalTax() ?: 0.0).asMoney()
            tvTotalDiscount.text = (cart?.getTotalDiscount() ?: 0.0).asMoney()
            tvTotal.text = (cart?.getGrandTotal() ?: 0.0).asMoney()


            if(cart?.customer != null){
                tvCustomerName.run {
                    text = getString(R.string.customer_x, cart.customer?.name ?: getString(R.string.unknown))
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green_600))
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    ivClearCustomer.show()
                }
            } else {
                tvCustomerName.run {
                    text = getString(R.string.customer_x, getString(R.string.visitor) )
                    backgroundTintList = null
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    ivClearCustomer.hide()
                }
            }

        }
    }

    private fun updateCheckoutState(state: MainState<String>){
        when (state.status) {
            Status.Success -> {
                cartViewModel.clearCurrentCart()
                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
            }
            Status.Failure -> {
                requireActivity().showErrorDialog(
                    getString(R.string.error),
                    state.message
                )
            }
            Status.Idle -> {}
        }
    }

    override fun onCartItemClicked(productId: Int) {
        val cartItem = cartViewModel.cart?.items?.find { it.productId == productId }
        cartItem?.let { item ->
            UpdateCartItemDialog.newInstance(item)
                .show(requireActivity().supportFragmentManager, ApplyDiscountDialog.TAG)
        }
    }

    override fun onCartItemDeleteClicked(productId: Int) {
        cartViewModel.removeCartItem(productId)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.btnClearCart.id -> {
                cartViewModel.clearCurrentCart()
            }
            binding.btnApplyDiscount.id -> {
                ApplyDiscountDialog().show(requireActivity().supportFragmentManager, ApplyDiscountDialog.TAG)
            }
            binding.btnCheckout.id -> {
                cartViewModel.cart?.let {
                    checkoutViewModel.doCheckout(it)
                }
            }
            binding.btnAssignCustomer.id -> {
                showPickCustomerDialog()
            }
            binding.ivClearCustomer.id -> {
                cartViewModel.assignCustomer(null)
            }
        }
    }

    private fun showPickCustomerDialog(){
        val dialog = CustomersFragment()
        dialog.setListener { customer ->
            cartViewModel.assignCustomer(customer)
            dialog.dismiss()
        }
        dialog.show(requireActivity().supportFragmentManager, CustomersFragment.TAG)
    }
}