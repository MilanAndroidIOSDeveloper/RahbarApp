package com.android.rahbar.advisor.loginsignup

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.databinding.ActivitySignupBinding
import com.android.rahbar.advisor.loginsignup.fragment.RegisterFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initializeViews()
    }

    private fun initializeViews() {
        initializeToolbar()
        initializeRegisterFragment()
    }

    private fun initializeToolbar() {
        supportActionBar?.title = getString(R.string.sign_up)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun initializeRegisterFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containers, RegisterFragment())
            .commitNow()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}