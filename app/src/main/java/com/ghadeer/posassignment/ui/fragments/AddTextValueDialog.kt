package com.ghadeer.posassignment.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.ghadeer.posassignment.databinding.DialogAddTextValueBinding
import com.ghadeer.posassignment.util.hideKeyboard

class AddTextValueDialog : DialogFragment() {

    companion object {
        const val TAG = "AddTextValueDialog"

        private const val ARG_TITLE = "arg_title"
        private const val ARG_HINT = "arg_hint"
        private const val ARG_DEFAULT_VALUE = "arg_default_value"
        private const val ARG_BUTTON_TEXT = "arg_button_text"
        private const val ARG_INPUT_TYPE = "arg_input_type"

        fun newInstance(title: String,
                        hint: String,
                        defaultValue: String = "",
                        buttonText: String = "Submit",
                        inputType: Int = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        ): AddTextValueDialog{
            return AddTextValueDialog().apply {
                arguments = bundleOf(
                    ARG_TITLE to title,
                    ARG_HINT to hint,
                    ARG_DEFAULT_VALUE to defaultValue,
                    ARG_BUTTON_TEXT to buttonText,
                    ARG_INPUT_TYPE to inputType,
                )
            }
        }
    }

    private lateinit var binding: DialogAddTextValueBinding

    private var onTextSubmittedListener: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddTextValueBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {

        binding.run {

            arguments?.let { arguments ->
                tvTitle.text = arguments.getString(ARG_TITLE)
                etText.hint = arguments.getString(ARG_HINT)
                etText.inputType = arguments.getInt(ARG_INPUT_TYPE)
                etText.setText(arguments.getString(ARG_DEFAULT_VALUE))
                btnAdd.text = arguments.getString(ARG_BUTTON_TEXT)
            }

            btnAdd.setOnClickListener {
                onSubmitClicked()
            }
            ivClose.setOnClickListener{
                dismiss()
            }
        }
    }

    private fun onSubmitClicked() {

        binding.etText.clearFocus()
        hideKeyboard()

        val text = binding.etText.text.toString().trim()

        onTextSubmittedListener?.invoke(text)
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            resources.configuration.screenWidthDp / 2,
            ConstraintLayout.LayoutParams.WRAP_CONTENT)
    }

    fun setListener(listener: (String) -> Unit){
        onTextSubmittedListener = listener
    }
}