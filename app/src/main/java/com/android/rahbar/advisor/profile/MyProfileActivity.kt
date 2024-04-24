package com.android.rahbar.advisor.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.databinding.ActivityProfileBinding
import com.android.rahbar.advisor.mycourses.MyCoursesActivity
import com.android.rahbar.advisor.mysearchedcourse.MySearchCoursesActivity
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileActivity : AppCompatActivity() {


    private lateinit var binding: ActivityProfileBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initializeToolbar()
        fetchUserProfile()

        binding.myCourses.setOnClickListener {
            startActivity(Intent(this, MyCoursesActivity::class.java))
            finish()
        }
    }

    private fun initializeToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.my_profile)
    }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userCoursesRef = db.collection("users").document(userId)
            userCoursesRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val data = document.data
                        if (data != null) {
                            updateUIWithData(data)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, getString(R.string.error_getting_user_data), e)
                }
        }
    }




    private fun updateUIWithData(data: Map<String, Any>) {
        binding.fnameTv.text = buildString {
        append("First Name : ")
        append(data["firstname"] as? String ?: "")
    }
        binding.lnameTv.text = buildString {
        append("Last Name : ")
        append(data["lastname"] as? String ?: "")
    }
        binding.emailTv.text = buildString {
        append("Email : ")
        append(data["email"] as? String ?: "")
    }

        val searchTerms = data["searchHistory"] as? String ?: ""
        displaySearchTerms(searchTerms.split(","))
    }



    private fun displaySearchTerms(searchTerms: List<String>) {

        binding.chipGroup.removeAllViews()

        for (term in searchTerms) {
            val chip = Chip(binding.chipGroup.context).apply {
                text = term
                isClickable = true
                isCheckable = false
                setOnClickListener { navigateToMySearchCoursesActivity(term) }
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun navigateToMySearchCoursesActivity(searchTerm: String) {
        val intent = Intent(this, MySearchCoursesActivity::class.java).apply {
            putExtra("searchTerm", searchTerm)
        }
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "MyProfileActivity"
    }
}