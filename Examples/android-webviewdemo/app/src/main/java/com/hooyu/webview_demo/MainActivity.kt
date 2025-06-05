package com.hooyu.webview_demo

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.hooyu.webview_demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // disable landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.content_view) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph) //configure nav controller
        setupActionBarWithNavController(navController, appBarConfiguration) // setup action bar
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun hideLogo() = runOnUiThread { binding.logoImage.visibility = View.GONE }
    fun showLogo() = runOnUiThread { binding.logoImage.visibility = View.VISIBLE }
    fun hideToolbar() = runOnUiThread { binding.toolbar.visibility = View.GONE }
    fun showToolbar() = runOnUiThread { binding.toolbar.visibility = View.VISIBLE }
}