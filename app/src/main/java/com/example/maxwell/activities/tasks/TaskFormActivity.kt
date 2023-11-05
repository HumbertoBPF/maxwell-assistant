package com.example.maxwell.activities.tasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.R
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.database.Converters
import com.example.maxwell.databinding.ActivityTaskFormBinding
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.models.Task
import com.example.maxwell.utils.padWithZeros
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import java.util.Locale.US

class TaskFormActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityTaskFormBinding.inflate(layoutInflater)
    }

    private val converters by lazy {
        Converters()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureTitleTextInput()
        configureDurationTextInput()
        configureDueDateInput()
        configurePriorityTextInput()
        configureStatusTextInput()
        configureSaveButton()

        setContentView(binding.root)
    }

    private fun configureDurationTextInput() {
        val durationTextInputEditText = binding.durationTextInputEditText

        durationTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateDuration()
            }
        }
    }

    private fun configureTitleTextInput() {
        val titleTextInputEditText = binding.titleTextInputEditText

        titleTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateTitle()
            }
        }
    }

    private fun configureDueDateInput() {
        val dueDateTextInputEditText = binding.dueDateTextInputEditText

        dueDateTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDueDatePicker()
            } else {
                validateDueDate()
            }
        }
    }

    private fun showDueDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.due_date_picker_title))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            handleDueDateSelection(datePicker)
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun handleDueDateSelection(datePicker: MaterialDatePicker<Long>) {
        val dueDateTextInputEditText = binding.dueDateTextInputEditText
        val selection = datePicker.selection

        selection?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection

            val day = padWithZeros(calendar.get(DAY_OF_MONTH) + 1)
            val month = padWithZeros(calendar.get(MONTH) + 1)
            val year = calendar.get(YEAR)

            dueDateTextInputEditText.setText("$month-$day-$year")
        }
    }

    private fun configurePriorityTextInput() {
        val priorityTextInputEditText = binding.priorityTextInputAutoComplete
        val priorityTextInputAutoComplete = binding.priorityTextInputAutoComplete

        priorityTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePriority()
            }
        }

        val priorityOptions = arrayOf(Priority.LOW.text, Priority.MEDIUM.text, Priority.HIGH.text)
        val priorityAutoComplete = priorityTextInputAutoComplete as? MaterialAutoCompleteTextView
        priorityAutoComplete?.setSimpleItems(priorityOptions)
    }

    private fun configureStatusTextInput() {
        val statusTextInputEditText = binding.statusTextInputAutoComplete
        val statusTextInputAutoComplete = binding.statusTextInputAutoComplete

        statusTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateStatus()
            }
        }

        val statusOptions = arrayOf(Status.PENDING.text, Status.IN_PROGRESS.text, Status.DONE.text)
        val statusAutoComplete = statusTextInputAutoComplete as? MaterialAutoCompleteTextView
        statusAutoComplete?.setSimpleItems(statusOptions)
    }

    private fun configureSaveButton() {
        val saveButton = binding.saveButton

        saveButton.setOnClickListener {
            val task = getTaskFromFormInputs()

            task?.let {
                lifecycleScope.launch {
                    val taskDao = AppDatabase.instantiate(this@TaskFormActivity).taskDao()
                    taskDao.insert(task)
                    finish()
                }
            }
        }
    }

    private fun getTaskFromFormInputs(): Task? {
        if (!validateTitle() ||
            !validateDuration() ||
            !validateDueDate() ||
            !validatePriority() ||
            !validateStatus()) {
            return null
        }

        val titleTextInputEditText = binding.titleTextInputEditText
        val descriptionTextInputEditText = binding.descriptionTextInputEditText
        val durationTextInputEditText = binding.durationTextInputEditText
        val dueDateTextInputEditText = binding.dueDateTextInputEditText
        val priorityTextInputAutoComplete = binding.priorityTextInputAutoComplete
        val statusTextInputAutoComplete = binding.statusTextInputAutoComplete

        val title = titleTextInputEditText.text.toString()
        val description = descriptionTextInputEditText.text.toString()
        val duration = durationTextInputEditText.text.toString()
        val dueDateString = dueDateTextInputEditText.text.toString()
        val priorityString = priorityTextInputAutoComplete.text.toString()
        val statusString = statusTextInputAutoComplete.text.toString()
        // TODO properly format dates
        val format = SimpleDateFormat("MM-dd-yyyy", US)
        val dueDate = format.parse(dueDateString)

        val priority = converters.fromStringToPriority(priorityString)
        val status = converters.fromStringToStatus(statusString)

        return Task(
            title = title,
            description = description,
            duration = BigDecimal(duration),
            dueDate = dueDate,
            priority = priority,
            status = status
        )
    }

    private fun validateTitle(): Boolean {
        val titleTextInput = binding.titleTextInput
        val titleTextInputEditText = binding.titleTextInputEditText

        val title = titleTextInputEditText.text.toString()

        if (title.trim() == "") {
            markFieldAsRequired(titleTextInput)
            return false
        }

        clearErrors(titleTextInput)
        return true
    }

    private fun validateDuration(): Boolean {
        val durationTextInput = binding.durationTextInput
        val durationTextInputEditText = binding.durationTextInputEditText

        val duration = durationTextInputEditText.text.toString()

        if (duration.trim() == "") {
            markFieldAsRequired(durationTextInput)
            return false
        }

        clearErrors(durationTextInput)
        return true
    }

    private fun validateDueDate(): Boolean {
        // TODO properly validate dates
        val dueDateTextInput = binding.dueDateTextInput
        val dueDateTextInputEditText = binding.dueDateTextInputEditText

        val dueDateString = dueDateTextInputEditText.text.toString()

        val format = SimpleDateFormat("MM-dd-yyyy", US)

        try {
            format.parse(dueDateString)
        } catch (e: ParseException) {
            dueDateTextInput.isErrorEnabled = true
            dueDateTextInput.error = getString(R.string.due_date_text_input_helper_text)
            return false
        }

        clearErrors(dueDateTextInput)
        return true
    }

    private fun validatePriority(): Boolean {
        val priorityTextInput = binding.priorityTextInput
        val priorityTextInputAutoComplete = binding.priorityTextInputAutoComplete

        val priorityString = priorityTextInputAutoComplete.text.toString()

        val priority = converters.fromStringToPriority(priorityString)

        if (priority == null) {
            markFieldAsRequired(priorityTextInput)
            return false
        }

        clearErrors(priorityTextInput)
        return true
    }

    private fun validateStatus(): Boolean {
        val statusTextInput = binding.statusTextInput
        val statusTextInputAutoComplete = binding.statusTextInputAutoComplete

        val statusString = statusTextInputAutoComplete.text.toString()

        val status = converters.fromStringToStatus(statusString)

        if (status == null) {
            markFieldAsRequired(statusTextInput)
            return false
        }

        clearErrors(statusTextInput)
        return true
    }

    private fun markFieldAsRequired(field: TextInputLayout) {
        field.isErrorEnabled = true
        field.error = getString(R.string.required_field_error)
    }

    private fun clearErrors(field: TextInputLayout) {
        field.isErrorEnabled = false
        field.error = ""
    }
}