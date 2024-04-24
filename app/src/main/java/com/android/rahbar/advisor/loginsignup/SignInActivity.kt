package com.android.rahbar.advisor.loginsignup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.course.MainActivity
import com.android.rahbar.advisor.databinding.ActivitySigninBinding
import com.android.rahbar.advisor.loginsignup.fragment.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initializeViews()
        initializeFirebaseAuth()
        checkUserLoggedIn()
    }

    private fun initializeViews() {
        initializeToolbar()
        initializeLoginFragment()
    }

    private fun initializeToolbar() {
        supportActionBar?.title = getString(R.string.login)
    }

    private fun initializeLoginFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containers, LoginFragment())
            .commitNow()
    }

    private fun initializeFirebaseAuth() {
        auth = FirebaseAuth.getInstance()
    }

    private fun checkUserLoggedIn() {
        if (auth.currentUser != null) {
            redirectToMainActivity()
        }
    }

    private fun redirectToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}