package com.ryanchen01.nnrcontroller

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.ryanchen01.nnrcontroller.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        // Initialize the database helper
        val dbHelper = DatabaseHelper(this)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val rootView = findViewById<View>(android.R.id.content)
        if (dbHelper.getToken() == "") {
            Snackbar.make(rootView, "Cannot fetch server list, please set your token first", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        // Fetch server list
        val status = refreshServerContent(this) // 0: failed, 1: success
        if (status == 0) {
            Snackbar.make(rootView, "Cannot fetch server list, please check your network connection", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        binding.fab.setOnClickListener { view ->
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            if (navController.currentDestination?.id == R.id.FirstFragment) {
                navController.navigate(R.id.action_FirstFragment_to_NewFragment)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        //val item: MenuItem = menu.findItem(R.id.action_settings)
        //item.setVisible(false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        item.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_settings -> {
                    // User chose the "Settings" item, go to second fragment if currently on first fragment
                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    if (navController.currentDestination?.id == R.id.FirstFragment) {
                        navController.navigate(R.id.action_FirstFragment_to_SettingsFragment)
                    }
                    true
                }
                else -> false
            }
        }
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}