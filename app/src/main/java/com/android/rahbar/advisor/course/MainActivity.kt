package com.android.rahbar.advisor.course

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.Utils
import com.android.rahbar.advisor.databinding.ActivityMainBinding
import com.android.rahbar.advisor.loginsignup.SignInActivity
import com.android.rahbar.advisor.mycourses.MyCoursesActivity
import com.android.rahbar.advisor.profile.MyProfileActivity
import com.android.rahbar.advisor.searchcourse.SearchCourseActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CourseViewModel by viewModels()
    private lateinit var adapter: CourseListAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initViews()
        setupDrawerNavigation()
        initRecyclerView()
        observeViewModel()
        setListeners()
    }

    private fun initViews() {
        drawerLayout = binding.drawerLayout


        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = true

        supportActionBar?.setTitle(getString(R.string.courses))

    }

    private fun setupDrawerNavigation() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_my_courses -> {
                    startActivity(Intent(this, MyCoursesActivity::class.java))
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.my_profile -> {
                    startActivity(Intent(this, MyProfileActivity::class.java))
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.menu_logout -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    logoutConfirmationDialog()
                    true
                }

                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(this, SearchCourseActivity::class.java))
                true
            }

            else -> {
                if (toggle.onOptionsItemSelected(item)) {
                    true
                } else {
                    super.onOptionsItemSelected(item)
                }
            }
        }
    }

    private fun initRecyclerView() {
        adapter = CourseListAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.course.observe(this) { courses ->
            adapter.addNewCourse(courses)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setListeners() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!viewModel.isLoading.value!! && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    Utils.isInternetAvailable(this@MainActivity) { isConnected ->
                        if (isConnected) {
                            viewModel.getMoreCourse()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.no_internet_available),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    private fun logoutConfirmationDialog() {
        AlertDialog.Builder(this@MainActivity, R.style.AlertDialogCustom)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                logoutUser()
                finish()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        startActivity(
            Intent(
                this@MainActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_main, menu)
        return true
    }
}