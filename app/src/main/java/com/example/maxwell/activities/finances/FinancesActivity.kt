package com.example.maxwell.activities.finances

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.R
import com.example.maxwell.adapters.FinanceAdapter
import com.example.maxwell.databinding.ActivityFinancesBinding
import com.example.maxwell.databinding.DialogFilterFinancesBinding
import com.example.maxwell.models.Currency
import com.example.maxwell.models.FinanceType
import com.example.maxwell.repository.FinanceRepository
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getDatePicker
import com.example.maxwell.utils.hasValidDateFormat
import com.example.maxwell.utils.parseDate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.Date

class FinancesActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFinancesBinding.inflate(layoutInflater)
    }

    private val financeRepository by lazy {
        FinanceRepository(this@FinancesActivity)
    }

    private val adapter by lazy {
        FinanceAdapter(this@FinancesActivity, mutableListOf())
    }

    private var title = ""

    private var isBrlOptionChecked = true
    private var isEuroOptionChecked = true

    private var isIncomeOptionChecked = true
    private var isExpenseOptionChecked = true

    private var date: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureFab()
        configureRecyclerView()
        configureAppbarMenu()

        setContentView(binding.root)
    }

    private fun configureRecyclerView() {
        lifecycleScope.launch {
            financeRepository.getAll { finances ->
                val financesRecyclerView = binding.financesRecyclerView
                adapter.changeDataset(finances)
                financesRecyclerView.adapter = adapter
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

    private fun configureAppbarMenu() {
        val appbarMenu = binding.appbarMenu

        appbarMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.ic_filter) {
                displaySearchDialog()
                true
            } else {
                false
            }
        }
    }

    private fun displaySearchDialog() {
        val dialogBinding = DialogFilterFinancesBinding.inflate(layoutInflater)

        bindSearchDialogViews(dialogBinding)

        MaterialAlertDialogBuilder(this@FinancesActivity)
            .setTitle(getString(R.string.search_finances_dialog_title))
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.cancel_button, null)
            .setPositiveButton(R.string.search_button) { _, _ ->
                filterFinances(dialogBinding)
            }
            .show()
    }

    private fun bindSearchDialogViews(dialogBinding: DialogFilterFinancesBinding) {
        val titleTextInputEditText = dialogBinding.titleTextInputEditText
        titleTextInputEditText.setText(title)

        val brlCheckbox = dialogBinding.brlCheckbox
        val euroCheckbox = dialogBinding.euroCheckbox

        brlCheckbox.isChecked = isBrlOptionChecked
        euroCheckbox.isChecked = isEuroOptionChecked

        val incomeCheckbox= dialogBinding.incomeCheckbox
        val expenseCheckbox = dialogBinding.expenseCheckbox

        incomeCheckbox.isChecked = isIncomeOptionChecked
        expenseCheckbox.isChecked = isExpenseOptionChecked

        val dateTextInputEditText = dialogBinding.dateTextInputEditText

        date?.let { date ->
            dateTextInputEditText.setText(formatDateForInput(date))
        }

        dateTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val datePicker = getDatePicker(getString(R.string.date_picker_title)) { date ->
                    dateTextInputEditText.setText(formatDateForInput(date))
                }

                datePicker.show(supportFragmentManager, "datePicker")
            } else {
                validateDateFilter(dialogBinding)
            }
        }
    }

    private fun filterFinances(dialogBinding: DialogFilterFinancesBinding) {
        if (validateDateFilter(dialogBinding)) {
            val titleTextInputEditText = dialogBinding.titleTextInputEditText
            title = titleTextInputEditText.text.toString()

            val excludeCurrencies = mutableListOf<Currency>()

            val brlCheckbox = dialogBinding.brlCheckbox
            val euroCheckbox = dialogBinding.euroCheckbox

            isBrlOptionChecked = brlCheckbox.isChecked
            isEuroOptionChecked = euroCheckbox.isChecked

            if (!isBrlOptionChecked) {
                excludeCurrencies.add(Currency.BRL)
            }

            if (!isEuroOptionChecked) {
                excludeCurrencies.add(Currency.EUR)
            }

            val excludeFinanceTypes = mutableListOf<FinanceType>()

            val incomeCheckbox = dialogBinding.incomeCheckbox
            val expenseCheckbox = dialogBinding.expenseCheckbox

            isIncomeOptionChecked = incomeCheckbox.isChecked
            isExpenseOptionChecked = expenseCheckbox.isChecked

            if (!isIncomeOptionChecked) {
                excludeFinanceTypes.add(FinanceType.INCOME)
            }

            if (!isExpenseOptionChecked) {
                excludeFinanceTypes.add(FinanceType.EXPENSE)
            }

            val dateTextInputEditText = dialogBinding.dateTextInputEditText
            val dateString = dateTextInputEditText.text.toString()

            date = if (dateString.trim() != "") {
                parseDate(dateString)
            } else {
                null
            }

            lifecycleScope.launch {
                val filteredFinances = financeRepository.filter(
                    title = title,
                    excludeCurrencies = excludeCurrencies,
                    excludeFinanceTypes = excludeFinanceTypes,
                    date = date
                )
                adapter.changeDataset(filteredFinances)
            }
        }
    }

    private fun validateDateFilter(dialogBinding: DialogFilterFinancesBinding): Boolean {
        val dateTextInputLayout = dialogBinding.dateTextInputLayout
        val dateTextInputEditText = dialogBinding.dateTextInputEditText

        val dateString = dateTextInputEditText.text.toString()

        if (dateString.trim() != "" && !dateString.hasValidDateFormat()) {
            dateTextInputLayout.isErrorEnabled = true
            dateTextInputLayout.error = getString(R.string.data_format_instruction)
            return false
        }

        dateTextInputLayout.isErrorEnabled = false
        dateTextInputLayout.error = ""
        return true
    }
}