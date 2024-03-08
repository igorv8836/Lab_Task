package com.example.lab_task.view.fragments

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
import com.example.lab_task.viewmodel.SettingsViewModel
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var sharedPref: SharedPreferences

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
        sharedPref = requireActivity().getSharedPreferences("tokens", Context.MODE_PRIVATE)
        viewModel.getErrorMessage()





        viewModel.snackbarText.observe(viewLifecycleOwner){
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }

        binding.registerButton.setOnClickListener {
            showAuthDialog(true)
        }
        binding.loginButton.setOnClickListener {
            showAuthDialog(false)
        }
        binding.exitButton.setOnClickListener {
            viewModel.logOut()
        }

        viewModel.token.observe(viewLifecycleOwner){
            if (it != null)
                with (sharedPref.edit()) {
                    putString(getString(R.string.tag_api_token), it)
                    apply()
                }
            else {
                with(sharedPref.edit()) {
                    remove(getString(R.string.tag_api_token))
                    remove(getString(R.string.username))
                    apply()
                }
                showLogInLayout()
            }
        }

        viewModel.isAuthed.observe(viewLifecycleOwner){
            if (it) {
                binding.mainText.text = "Авторизован"
                showExitLayout()
            } else {
                binding.mainText.text = "Не авторизован"
                showLogInLayout()
            }
        }
    }

    private fun showExitLayout(){
        binding.authButtonsLayout.visibility = View.GONE
        binding.exitAccLayout.visibility = View.VISIBLE
    }

    private fun showLogInLayout(){
        binding.authButtonsLayout.visibility = View.VISIBLE
        binding.exitAccLayout.visibility = View.GONE
    }

    private fun showAuthDialog(isRegister: Boolean){
        val customLayout = LayoutInflater.from(requireContext())
            .inflate(R.layout.auth_dialog, null)
        val loginEditText = customLayout.findViewById<EditText>(R.id.editTextUsername_inputText)
        val passwordEditText =
            customLayout.findViewById<EditText>(R.id.editTextPassword_inputText)
        val builder = AlertDialog.Builder(requireContext()).setView(customLayout)

        if (isRegister){
            builder.setTitle("Создать аккаунт")
                .setPositiveButton("Создать") { dialog, which ->
                    if (loginEditText.text.isNotEmpty() && passwordEditText.text.length >= 3) {
                        viewModel.createAccount(
                            loginEditText.text.toString(),
                            passwordEditText.text.toString()
                        )
                    }
                }
        } else{
            builder.setTitle("Войти")
                .setPositiveButton("Войти") { dialog, which ->
                    if (loginEditText.text.isNotEmpty() && passwordEditText.text.length >= 3)
                        viewModel.authAccount(
                            loginEditText.text.toString(),
                            passwordEditText.text.toString()
                        )
                }
        }

        builder.setNegativeButton("Отменить") { dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

}