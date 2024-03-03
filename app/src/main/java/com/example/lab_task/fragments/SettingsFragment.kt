package com.example.lab_task.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.lab_task.R
import com.example.lab_task.databinding.FragmentSettingsBinding
import com.example.lab_task.viewmodels.SettingsViewModel
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
//    private val sharedPref: SharedPreferences = requireActivity().getSharedPreferences("tokens", Context.MODE_PRIVATE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]


//        val token = sharedPref.getString(getString(R.string.tag_api_token), null)
//
//
//        with (sharedPref.edit()) {
//            putString(getString(R.string.tag_api_token), "new")
//            apply()
//        }


        binding.registerButton.setOnClickListener {
            val customLayout = LayoutInflater.from(requireContext())
                .inflate(R.layout.register_account_dialog, null)
            val loginEditText = customLayout.findViewById<EditText>(R.id.editTextUsername_inputText)
            val passwordEditText =
                customLayout.findViewById<EditText>(R.id.editTextPassword_inputText)
            val builder = AlertDialog.Builder(requireContext())
                .setView(customLayout)
                .setTitle("Создать аккаунт")
                .setPositiveButton("Создать") { dialog, which ->
                    if (loginEditText.text.isNotEmpty() && passwordEditText.text.length >= 3) {
                        viewModel.createAccount(
                            loginEditText.text.toString(),
                            passwordEditText.text.toString()
                        )
                    }
                }.setNegativeButton("Отменить") { dialog, which ->
                    dialog.dismiss()
                }
            builder.create().show()

        }

        viewModel.snackbarText.observe(viewLifecycleOwner){
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
    }

}