package com.android.rahbar.advisor.searchcourse

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.course.CourseListAdapter
import com.android.rahbar.advisor.databinding.ActivitySearchCourseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchCourseActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySearchCourseBinding
    private val viewModel: SearchCourseViewModel by viewModels()
    private lateinit var adapter: CourseListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initializeToolbar()
        initRecyclerView()
        observeViewModel()
    }

    private fun initializeToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.search_courses)
    }

    private fun initRecyclerView() {
        adapter = CourseListAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.courses.observe(this) { courses ->
            adapter.addSearchCourse(courses)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_course_menu, menu)
        setupSearchView(menu)
        return true
    }

    private fun setupSearchView(menu: Menu): Boolean {
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.apply {
            findViewById<EditText>(androidx.appcompat.R.id.search_src_text).apply {
                setHintTextColor(Color.WHITE)
            }
            val searchEditText = findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchEditText.setHintTextColor(Color.WHITE)

            setIconifiedByDefault(false)
            requestFocus()
            queryHint = getString(R.string.search_courses)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    hideKeyboard()
                    searchItem.collapseActionView()
                    setIconifiedByDefault(true)
                    performSearch(query, searchView)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return true
                }
            })


            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    hideKeyboard()
                }
            }
        }
        return true
    }


    fun updateSearchHistory(searchString: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId!!)

        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                var currentHistory = documentSnapshot.getString("searchHistory") ?: ""

                currentHistory += if (currentHistory.isEmpty()) {
                    searchString
                } else {
                    ", $searchString"
                }

                userDocRef.update("searchHistory", currentHistory)
                    .addOnSuccessListener {
                        println("Search history updated successfully")
                    }
                    .addOnFailureListener { e ->
                        println("Error updating search history: ${e.message}")
                    }
            }
        }
    }

    private fun performSearch(query: String, searchView: SearchView) {
        viewModel.fetchSearchCourseList(query)
        updateSearchHistory(query)
        searchView.setQuery("", false)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}