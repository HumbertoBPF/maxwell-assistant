package com.example.maxwell.activities.finances

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.adapters.FinanceAdapter
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.databinding.ActivityFinancesBinding
import kotlinx.coroutines.launch

class FinancesActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFinancesBinding.inflate(layoutInflater)
    }

    private val financeDao by lazy {
        AppDatabase.instantiate(this@FinancesActivity).financeDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureFab()
        configureRecyclerView()

        setContentView(binding.root)
    }

    private fun configureRecyclerView() {
        lifecycleScope.launch {
            financeDao.getFinances().collect { finances ->
                val financesRecyclerView = binding.financesRecyclerView
                financesRecyclerView.adapter = FinanceAdapter(this@FinancesActivity, finances)
            }
        }
    }

    private fun configureFab() {
        val addFab = binding.addFab

        addFab.setOnClickListener {
            val intent = Intent(this@FinancesActivity, FinanceFormActivity::class.java)
            startActivity(intent)
        }
    }
}