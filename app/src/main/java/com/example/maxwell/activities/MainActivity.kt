package com.example.maxwell.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.databinding.ActivityMainBinding
import com.example.maxwell.models.MenuItem

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var keep = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keep }

        Handler(Looper.getMainLooper()).postDelayed({ keep = false }, 2000)

        super.onCreate(savedInstanceState)

        configureMenuRecyclerView()
        configureAppbarMenu()

        setContentView(binding.root)
    }

    private fun configureAppbarMenu() {
        val appbarMenu = binding.appbarMenu

        appbarMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.settings_item) {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            } else {
                false
            }
        }
    }

    private fun configureMenuRecyclerView() {
        val recyclerView = binding.menuRecyclerView

        val taskItem = MenuItem(
            R.color.color_state_list_tasks,
            R.drawable.ic_tasks,
            getString(R.string.tasks_title),
            TasksActivity::class.java
        )

        val studyItem = MenuItem(
            R.color.color_state_list_studies,
            R.drawable.ic_studies,
            getString(R.string.studies_title),
            StudiesActivity::class.java
        )

        val financeItem = MenuItem(
            R.color.color_state_list_finances,
            R.drawable.ic_finances,
            getString(R.string.finances_title),
            FinancesActivity::class.java
        )

        val menuItems = listOf(taskItem, studyItem, financeItem)

        recyclerView.adapter = MenuAdapter(this, menuItems)
    }
}