package com.android.rahbar.advisor.mysearchedcourse

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.course.CourseListAdapter
import com.android.rahbar.advisor.databinding.ActivityMyCoursesBinding
import com.android.rahbar.advisor.searchcourse.SearchCourseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MySearchCoursesActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMyCoursesBinding
    private val viewModel: SearchCourseViewModel by viewModels()
    private lateinit var adapter: CourseListAdapter
    var searchTerm: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCoursesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        searchTerm = intent.getStringExtra("searchTerm").toString()


        initializeToolbar()
        initRecyclerView()
        observeViewModel()
    }

    private fun initializeToolbar() {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = searchTerm  + " " + getString(R.string.courses)
    }


    private fun initRecyclerView() {
        adapter = CourseListAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.fetchSearchCourseList(searchTerm)
        viewModel.courses.observe(this) { courses ->
            if (courses.size != 0) {
                binding.emptyData.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.addNewCourse(courses)
            } else {
                binding.emptyData.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
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

}