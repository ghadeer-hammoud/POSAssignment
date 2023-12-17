package com.ghadeer.posassignment.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ghadeer.posassignment.data.enums.Status
import com.ghadeer.posassignment.databinding.ActivityBaseBinding
import com.ghadeer.posassignment.util.toast
import com.ghadeer.posassignment.viewmodel.CheckoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BaseActivity: AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    private val checkoutViewModel: CheckoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        observeState()
    }

    fun setToolbarTitle(title: String) {
        binding.toolbar.title = title
    }

    private fun observeState(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                checkoutViewModel.checkoutState.collect{ state ->
                    if(state.status == Status.Success){
                        print()
                    }
                }
            }
        }
    }

    private fun print(){
        try {
            throw Exception("Printer is not configured")
        } catch (e: Exception){
            toast(e.localizedMessage)
        }
    }

}