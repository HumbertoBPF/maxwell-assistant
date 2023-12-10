package com.example.maxwell.activities.finances

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SimpleSQLiteQuery
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
            financeRepository.getFinances { finances ->
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
            val query = getFilteringQuery(dialogBinding)

            lifecycleScope.launch {
                val filteredFinances = financeRepository.filterFinances(query)
                adapter.changeDataset(filteredFinances)
            }
        }
    }

    private fun getFilteringQuery(dialogBinding: DialogFilterFinancesBinding): SimpleSQLiteQuery {
        var filter = getBaseFilteringQuery(dialogBinding)
        val args = mutableListOf<Any>(title)

        filter = addCurrencyFilter(dialogBinding, filter, args)
        filter = addTypeFilter(dialogBinding, filter, args)
        filter = addDateFilter(dialogBinding, filter, args)

        filter = """
            SELECT original.id, original.title, original.categoryId, original.value, original.currency, original.type, grouped.date FROM 
            (SELECT * FROM Finance WHERE $filter ORDER BY date DESC) AS original 
            LEFT JOIN (SELECT * FROM Finance WHERE $filter GROUP BY date) AS grouped
            ON original.id = grouped.id;
        """.trimIndent()
        args.addAll(args)

        return SimpleSQLiteQuery(filter, args.toTypedArray())
    }

    private fun getBaseFilteringQuery(dialogBinding: DialogFilterFinancesBinding): String {
        val titleTextInputEditText = dialogBinding.titleTextInputEditText
        title = titleTextInputEditText.text.toString()
        return "title LIKE '%' || ? || '%'"
    }

    private fun addCurrencyFilter(
        dialogBinding: DialogFilterFinancesBinding,
        filter: String,
        args: MutableList<Any>
    ): String {
        var queryWithFilter = filter

        val brlCheckbox = dialogBinding.brlCheckbox
        val euroCheckbox = dialogBinding.euroCheckbox

        isBrlOptionChecked = brlCheckbox.isChecked
        isEuroOptionChecked = euroCheckbox.isChecked

        if (!isBrlOptionChecked || !isEuroOptionChecked) {
            if (!isBrlOptionChecked) {
                queryWithFilter += " AND currency != ?"
                args.add(Currency.BRL.text)
            }

            if (!isEuroOptionChecked) {
                queryWithFilter += " AND currency != ?"
                args.add(Currency.EUR.text)
            }
        }

        return queryWithFilter
    }

    private fun addTypeFilter(
        dialogBinding: DialogFilterFinancesBinding,
        filter: String,
        args: MutableList<Any>
    ): String {
        var queryWithFilter = filter

        val incomeCheckbox = dialogBinding.incomeCheckbox
        val expenseCheckbox = dialogBinding.expenseCheckbox

        isIncomeOptionChecked = incomeCheckbox.isChecked
        isExpenseOptionChecked = expenseCheckbox.isChecked

        if (!isIncomeOptionChecked || !isExpenseOptionChecked) {
            if (!isIncomeOptionChecked) {
                queryWithFilter += " AND type != ?"
                args.add(FinanceType.INCOME.text)
            }

            if (!isExpenseOptionChecked) {
                queryWithFilter += " AND type != ?"
                args.add(FinanceType.EXPENSE.text)
            }
        }

        return queryWithFilter
    }

    private fun addDateFilter(
        dialogBinding: DialogFilterFinancesBinding,
        filter: String,
        args: MutableList<Any>
    ): String {
        val dateTextInputEditText = dialogBinding.dateTextInputEditText
        val dateString = dateTextInputEditText.text.toString()

        date = null

        if (dateString.trim() != "") {
            date = parseDate(dateString)

            date?.let { date ->
                args.add(date.time)
                return "$filter AND date = ?"
            }
        }

        return filter
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