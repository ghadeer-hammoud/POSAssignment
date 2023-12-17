package com.ghadeer.posassignment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.databinding.FragmentCartBinding
import com.ghadeer.posassignment.databinding.FragmentHomeBinding
import com.ghadeer.posassignment.databinding.FragmentNewOrderBinding
import com.ghadeer.posassignment.data.model.view.HomeMenuAction
import com.ghadeer.posassignment.data.model.view.HomeMenuItem
import com.ghadeer.posassignment.ui.adapters.HomeMenuItemsRecyclerAdapter
import com.ghadeer.posassignment.ui.adapters.OnMenuItemClickListener
import com.ghadeer.posassignment.util.Constants

class NewOrderFragment: Fragment() {

    private lateinit var binding: FragmentNewOrderBinding

    private lateinit var cartAdapter: HomeMenuItemsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initViews()
    }

    private fun FragmentNewOrderBinding.initViews() {


    }

}