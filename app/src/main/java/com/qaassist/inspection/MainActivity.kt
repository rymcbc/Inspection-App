package com.qaassist.inspection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.qaassist.inspection.adapters.MainPagerAdapter
import com.qaassist.inspection.databinding.ActivityMainBinding
import com.qaassist.inspection.settings.SettingsActivity
import com.qaassist.inspection.utils.SharedPrefsUtil
import com.qaassist.inspection.viewmodels.InspectionViewModel
import com.qaassist.inspection.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val inspectionViewModel: InspectionViewModel by viewModels() // Shared ViewModel

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                mainViewModel.setPermissionsGranted(true)
            } else {
                Toast.makeText(this, "All permissions are required for the app to function properly", Toast.LENGTH_LONG).show()
                mainViewModel.setPermissionsGranted(false)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val theme = SharedPrefsUtil.getTheme(this)
        SharedPrefsUtil.applyTheme(theme)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupPermissions()
        setupViewPager()
        setupObservers() // Add this call
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getRequiredPermissions(): Array<String> {
        return mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }.toTypedArray()
    }

    private fun allPermissionsGranted(): Boolean {
        return getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun setupPermissions() {
        if (!allPermissionsGranted()) {
            requestPermissionsLauncher.launch(getRequiredPermissions())
        } else {
            mainViewModel.setPermissionsGranted(true)
        }
    }

    private fun setupViewPager() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "INSPECTIONS"
                1 -> "HISTORY"
                else -> ""
            }
        }.attach()
    }

    private fun setupObservers() {
        inspectionViewModel.navigateToInspectionTab.observe(this) { navigate ->
            if (navigate) {
                binding.viewPager.currentItem = 0 // Switch to the Inspection tab
                inspectionViewModel.onNavigationHandled() // Reset the event
            }
        }
    }
}
