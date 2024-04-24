package com.android.rahbar.advisor.loginsignup.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.frogtest.BioMetricManager
import com.android.frogtest.BioMetricPrompt
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.course.MainActivity
import com.android.rahbar.advisor.databinding.FragmentLoginBinding
import com.android.rahbar.advisor.loginsignup.AuthViewModel
import com.android.rahbar.advisor.loginsignup.Resource
import com.android.rahbar.advisor.loginsignup.SignupActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isBlank()) {
                showToast(getString(R.string.valid_email))
                return@setOnClickListener
            }

            if (password.isBlank()) {
                showToast(getString(R.string.valid_password))
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        viewModel.authState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> handleLoginSuccess()
                is Resource.Error -> showError(resource.message)
                else -> {
                }
            }
        }

        binding.signupTextView.setOnClickListener {
            navigateToSignUp()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun handleLoginSuccess() {
        binding.progressBar.visibility = View.GONE
        authenticateWithBiometrics()
    }

    private fun showError(message: String?) {
        binding.progressBar.visibility = View.GONE
        showToast(message ?: getString(R.string.unknown_error))
    }

    private fun navigateToSignUp() {
        startActivity(Intent(context, SignupActivity::class.java))
    }


    private fun authenticateWithBiometrics() {
        val biometricManager = BioMetricManager(requireContext())

        if (biometricManager.canAuthenticate()) {
            showBiometricPrompt()
        } else {
            showToast(getString(R.string.fingerprint_hardware_not_available))
        }
    }

    private fun showBiometricPrompt() {
        BioMetricPrompt(this).authenticate(
            onSuccess = {
                showToast(getString(R.string.biometric_authentication_succeeded))
                navigateToMainActivity()
            },
            onError = { error ->
                showToast(error)
            }
        )
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(context, MainActivity::class.java))
        requireActivity().finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }



}