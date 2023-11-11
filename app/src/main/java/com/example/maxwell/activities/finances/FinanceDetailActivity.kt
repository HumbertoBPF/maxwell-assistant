package com.example.maxwell.activities.finances

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.R
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.databinding.ActivityFinanceDetailBinding
import com.example.maxwell.models.Finance
import com.example.maxwell.utils.formatDatePretty
import com.example.maxwell.utils.showConfirmDeletionDialog
import kotlinx.coroutines.launch

class FinanceDetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFinanceDetailBinding.inflate(layoutInflater)
    }

    private val id by lazy {
        intent.getLongExtra("id", 0)
    }

    private val financeDao by lazy {
        AppDatabase.instantiate(this@FinanceDetailActivity).financeDao()
    }

    private val financeCategoryDao by lazy {
        AppDatabase.instantiate(this@FinanceDetailActivity).financeCategoryDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            financeDao.getFinanceById(id).collect {finance ->
                if (finance == null) {
                    finish()
                } else {
                    bind(finance)
                    configureAppbarMenu(finance)
                }
            }
        }

        setContentView(binding.root)
    }

    private fun configureAppbarMenu(finance: Finance) {
        val appbarMenu = binding.appbarMenu

        appbarMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_item -> {
                    showConfirmDeletionDialog(this@FinanceDetailActivity) { _, _ ->
                        lifecycleScope.launch {
                            financeDao.delete(finance)
                            finish()
                        }
                    }
                    true
                }

                R.id.edit_item -> {
                    val intent = Intent(this@FinanceDetailActivity, FinanceFormActivity::class.java)
                    intent.putExtra("id", finance.id)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun bind(finance: Finance) {
        val titleTextView = binding.titleTextView
        titleTextView.text = finance.title

        lifecycleScope.launch {
            financeCategoryDao.getFinanceCategoryById(id).collect{financeCategory ->
                val categoryTextView = binding.categoryTextView
                categoryTextView.text = financeCategory?.name
            }
        }

        val type = finance.type

        type?.let {
            val valueTextView = binding.valueTextView
            valueTextView.setTextColor(getColor(type.color))
            valueTextView.text = finance.formatValue()
        }

        val date = finance.date

        date?.let {
            val dateTextView = binding.dateTextView
            dateTextView.text = formatDatePretty(date)
        }
    }
}