package com.example.maxwell.activities.finances

import android.content.DialogInterface
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.R
import com.example.maxwell.activities.FormActivity
import com.example.maxwell.databinding.ActivityFinanceFormBinding
import com.example.maxwell.databinding.DialogFinanceCategoryFormBinding
import com.example.maxwell.models.Currency
import com.example.maxwell.models.Finance
import com.example.maxwell.models.FinanceCategory
import com.example.maxwell.models.FinanceType
import com.example.maxwell.repository.FinanceCategoryRepository
import com.example.maxwell.repository.FinanceRepository
import com.example.maxwell.utils.createChipView
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getDatePicker
import com.example.maxwell.utils.hasValidDateFormat
import com.example.maxwell.utils.parseDate
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class FinanceFormActivity : FormActivity() {
    private var finance: Finance? = null

    private val binding by lazy {
        ActivityFinanceFormBinding.inflate(layoutInflater)
    }

    private val id by lazy {
        intent.getLongExtra("id", 0)
    }

    private val financeRepository by lazy {
        FinanceRepository(this@FinanceFormActivity)
    }

    private val financeCategoryRepository by lazy {
        FinanceCategoryRepository(this@FinanceFormActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            financeRepository.getById(id) { financeFromDb ->
                finance = financeFromDb

                configureTitleTextInput()
                configureCategoryTextInput()
                configureFinanceCategoriesManagement()
                configureValueTextInput()
                configureCurrencyRadioGroup()
                configureTypeRadioGroup()
                configureDateTextInput()
                configureSaveButton()
            }
        }

        setContentView(binding.root)
    }

    private fun configureTitleTextInput() {
        val titleTextInputEditText = binding.titleTextInputEditText

        titleTextInputEditText.setText(finance?.title)

        titleTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateTitle()
            }
        }
    }

    private fun configureCategoryTextInput() {
        val categoryTextInput = binding.categoryTextInput

        val categoryInputTextAutoComplete = categoryTextInput.editText
        val categoryAutoComplete = categoryInputTextAutoComplete as? MaterialAutoCompleteTextView

        lifecycleScope.launch {
            financeCategoryRepository.getAll { financeCategories ->
                val financeCategoryOptions = financeCategories
                    .map { category -> category.name }.toTypedArray()
                categoryAutoComplete?.setSimpleItems(financeCategoryOptions)
            }
        }

        lifecycleScope.launch {
            val categoryId = finance?.categoryId ?: 0

            financeCategoryRepository.getById(categoryId) { financeCategory ->
                categoryAutoComplete?.setText(financeCategory?.name, false)
            }
        }
    }

    private fun configureFinanceCategoriesManagement() {
        val financeCategoryManagement = binding.manageFinanceCategoriesTextView

        financeCategoryManagement.setOnClickListener {
            displayFinanceCategoryManagementDialog()
        }
    }

    private fun displayFinanceCategoryManagementDialog() {
        val dialogBinding = DialogFinanceCategoryFormBinding.inflate(layoutInflater)

        lifecycleScope.launch {
            val financeCategoryChipGroup = dialogBinding.financeCategoriesChipGroup

            financeCategoryRepository.getAll { financeCategories ->
                financeCategoryChipGroup.removeAllViews()

                financeCategories.forEach { financeCategory ->
                    createCategoryChipView(financeCategory, financeCategoryChipGroup)
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(this@FinanceFormActivity)
            .setTitle(getString(R.string.finance_category_dialog_title))
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.finance_category_dialog_negative_button) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.finance_category_dialog_positive_button) { _, _ -> }
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            lifecycleScope.launch {
                if (validateCategoryNameTextInput(dialogBinding)) {
                    val nameTextInputEditText = dialogBinding.nameTextInputEditText
                    val name = nameTextInputEditText.text.toString()

                    val financeCategory = FinanceCategory(name = name)
                    financeCategoryRepository.insert(financeCategory)
                    nameTextInputEditText.setText("")
                }
            }
        }
    }

    private fun createCategoryChipView(
        financeCategory: FinanceCategory,
        financeCategoryChipGroup: ChipGroup
    ) {
        val chip = createChipView(this@FinanceFormActivity, financeCategory.name)

        financeCategoryChipGroup.addView(chip)
        chip.setOnCloseIconClickListener {
            lifecycleScope.launch {
                financeCategoryRepository.delete(financeCategory)
            }
            financeCategoryChipGroup.removeView(chip)
        }
    }

    private suspend fun validateCategoryNameTextInput(binding: DialogFinanceCategoryFormBinding): Boolean {
        val nameTextInputLayout = binding.nameTextInputLayout
        val nameTextInputEditText = binding.nameTextInputEditText

        val name = nameTextInputEditText.text.toString()

        if (name.trim() == "") {
            markFieldAsRequired(nameTextInputLayout)
            return false
        }

        val nameAvailable = financeCategoryRepository.getByName(name) == null

        if (!nameAvailable) {
            nameTextInputLayout.isErrorEnabled = true
            nameTextInputLayout.error = getString(R.string.error_finance_category_name_unavailable)
            return false
        }

        clearErrors(nameTextInputLayout)
        return true
    }

    private fun configureValueTextInput() {
        val valueTextInputEditText = binding.valueTextInputEditText

        val value = finance?.value

        value?.let {
            valueTextInputEditText.setText("$value")
        }

        valueTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateValue()
            }
        }
    }

    private fun configureCurrencyRadioGroup() {
        val currency = finance?.currency

        currency?.let {
            val brlRadioButton = binding.brlRadioButton
            val euroRadioButton = binding.euroRadioButton

            brlRadioButton.isChecked = (currency == Currency.BRL)
            euroRadioButton.isChecked = (currency == Currency.EUR)
        }
    }

    private fun configureTypeRadioGroup() {
        val type = finance?.type

        type?.let {
            val incomeRadioButton = binding.incomeRadioButton
            val expenseRadioButton = binding.expenseRadioButton

            incomeRadioButton.isChecked = (type == FinanceType.INCOME)
            expenseRadioButton.isChecked = (type == FinanceType.EXPENSE)
        }
    }

    private fun configureDateTextInput() {
        val dateTextInputLayout = binding.dateTextInputEditText

        val date = finance?.date

        date?.let {
            val formattedDate = formatDateForInput(date)
            dateTextInputLayout.setText(formattedDate)
        }

        dateTextInputLayout.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePicker()
            } else {
                validateDate()
            }
        }
    }

    private fun configureSaveButton() {
        val saveButton = binding.saveButton

        saveButton.setOnClickListener {
            if (validateAllFields()) {
                lifecycleScope.launch {
                    val categoryTextInputAutoComplete = binding.categoryTextInputAutoComplete
                    val categoryString = categoryTextInputAutoComplete.text.toString()

                    val category = financeCategoryRepository.getByName(categoryString)

                    category?.let {
                        val finance = getFinanceFromInputs(category)
                        financeRepository.insert(finance)
                        finish()
                    }
                }
            }
        }
    }

    private fun getFinanceFromInputs(category: FinanceCategory): Finance {
        val titleTextInputEditText = binding.titleTextInputEditText
        val valueTextInputEditText = binding.valueTextInputEditText
        val dateTextInputEditText = binding.dateTextInputEditText

        val title = titleTextInputEditText.text.toString()
        val value = BigDecimal(valueTextInputEditText.text.toString()).setScale(2, RoundingMode.CEILING)
        val currency = getCurrencyInput()
        val type = getTypeInput()
        val dateString = dateTextInputEditText.text.toString()

        return Finance(
            id = id,
            title = title,
            categoryId = category.id,
            value = value,
            currency = currency,
            type = type,
            date = parseDate(dateString)
        )
    }

    private fun getCurrencyInput(): Currency {
        val brlRadioButton = binding.brlRadioButton

        return if (brlRadioButton.isChecked) {
            Currency.BRL
        } else {
            Currency.EUR
        }
    }

    private fun getTypeInput(): FinanceType {
        val incomeRadioButton = binding.incomeRadioButton

        return if (incomeRadioButton.isChecked) {
            FinanceType.INCOME
        } else {
            FinanceType.EXPENSE
        }
    }

    private fun showDatePicker() {
        val dateTextInputEditText = binding.dateTextInputEditText

        val datePicker = getDatePicker(getString(R.string.date_picker_title)) { date ->
            dateTextInputEditText.setText(formatDateForInput(date))
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun validateAllFields(): Boolean {
        // It's necessary to use cache variables to force all the validation methods to be called
        val isTitleValid = validateTitle()
        val isCategoryValid = validateFinanceCategory()
        val isValueValid = validateValue()
        val isDateValid = validateDate()

        return isTitleValid && isCategoryValid && isValueValid && isDateValid
    }

    private fun validateTitle(): Boolean {
        val titleTextInputLayout = binding.titleTextInputLayout
        val titleTextInputEditText = binding.titleTextInputEditText

        val title = titleTextInputEditText.text.toString()

        if (title.trim() == "") {
            markFieldAsRequired(titleTextInputLayout)
            return false
        }

        clearErrors(titleTextInputLayout)
        return true
    }

    private fun validateFinanceCategory(): Boolean {
        val categoryTextInput = binding.categoryTextInput
        val categoryTextInputAutoComplete = binding.categoryTextInputAutoComplete

        val category = categoryTextInputAutoComplete.text.toString()

        if (category.trim() == "") {
            markFieldAsRequired(categoryTextInput)
            return false
        }

        clearErrors(categoryTextInput)
        return true
    }

    private fun validateValue(): Boolean {
        val valueTextInputLayout = binding.valueTextInputLayout
        val valueTextInputEditText = binding.valueTextInputEditText

        val value = valueTextInputEditText.text.toString()

        if (value.trim() == "") {
            markFieldAsRequired(valueTextInputLayout)
            return false
        }

        clearErrors(valueTextInputLayout)
        return true
    }

    private fun validateDate(): Boolean {
        val dateTextInputLayout = binding.dateTextInputLayout
        val dateTextInputEditText = binding.dateTextInputEditText

        val date = dateTextInputEditText.text.toString()

        if (date.hasValidDateFormat()) {
            clearErrors(dateTextInputLayout)
            return true
        }

        dateTextInputLayout.isErrorEnabled = true
        dateTextInputLayout.error = getString(R.string.data_format_instruction)
        return false
    }
}