package com.android.rahbar.advisor.loginsignup.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.android.rahbar.advisor.databinding.FragmentRegisterBinding
import com.android.rahbar.advisor.loginsignup.AuthViewModel
import com.android.rahbar.advisor.loginsignup.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {



    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val firstName = binding.firstnameEditText.text.toString()
            val lastName = binding.lastnameEditText.text.toString()

            if (firstName.isBlank()) {
                showToast(getString(R.string.valid_firstname))
                return@setOnClickListener
            }

            if (lastName.isBlank()) {
                showToast(getString(R.string.valid_lastname))
                return@setOnClickListener
            }

            if (email.isBlank()) {
                showToast(getString(R.string.valid_email))
                return@setOnClickListener
            }

            if (password.isBlank()) {
                showToast(getString(R.string.valid_password))
                return@setOnClickListener
            }

            viewModel.signUp(email, password)
        }

        viewModel.authState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> handleSignUpSuccess()
                is Resource.Error -> showError(resource.message)
                else -> {
                }
            }
        }

        binding.loginTextView.setOnClickListener {
            activity?.finish()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun handleSignUpSuccess() {
        binding.progressBar.visibility = View.GONE
        authenticateWithBiometrics()
    }

    private fun showError(message: String?) {
        binding.progressBar.visibility = View.GONE
        showToast(message ?: getString(R.string.unknown_error))
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
            onSuccess = { onBiometricSuccess() },
            onError = { error -> handleBiometricError(error) }
        )
    }

    private fun onBiometricSuccess() {
        showToast(getString(R.string.biometric_authentication_succeeded))
        saveUserDataAndNavigateToMain()
    }

    private fun saveUserDataAndNavigateToMain() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userData = hashMapOf(
                "firstname" to binding.firstnameEditText.text.toString(),
                "lastname" to binding.lastnameEditText.text.toString(),
                "email" to binding.emailEditText.text.toString(),
                "searchHistory" to ""
            )
            val userCoursesRef = db.collection("users").document(user.uid)
            userCoursesRef.set(userData)
                .addOnSuccessListener { Log.d(TAG, getString(R.string.user_data_added)) }
                .addOnFailureListener { e -> Log.w(TAG,
                    getString(R.string.error_adding_user_data), e) }
        }
        navigateToMainActivity()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(context, MainActivity::class.java))
        requireActivity().finish()
    }

    private fun handleBiometricError(error: String) {
        if (!error.contentEquals(getString(R.string.biometric_authentication_error_too_many_attempts_try_again_later))
            && !error.contentEquals(getString(R.string.biometric_authentication_error_fingerprint_operation_cancelled_by_user))
        ) {
            showToast(error)
        } else {
            FirebaseAuth.getInstance().currentUser?.delete()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}