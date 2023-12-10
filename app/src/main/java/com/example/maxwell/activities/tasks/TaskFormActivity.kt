package com.example.maxwell.activities.tasks

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.R
import com.example.maxwell.activities.FormActivity
import com.example.maxwell.database.Converters
import com.example.maxwell.databinding.ActivityTaskFormBinding
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.models.Task
import com.example.maxwell.repository.TaskRepository
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getDatePicker
import com.example.maxwell.utils.hasValidDateFormat
import com.example.maxwell.utils.parseDate
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import java.math.BigDecimal

class TaskFormActivity : FormActivity() {
    private var task: Task? = null

    private val binding by lazy {
        ActivityTaskFormBinding.inflate(layoutInflater)
    }

    private val id by lazy {
        intent.getLongExtra("id", 0)
    }

    private val taskRepository by lazy {
        TaskRepository(this@TaskFormActivity)
    }

    private val converters by lazy {
        Converters()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            taskRepository.getTaskById(id) {taskFromDb ->
                task = taskFromDb

                configureAppBar()
                configureTitleTextInput()
                configureDescriptionTextInput()
                configureDurationTextInput()
                configureDueDateInput()
                configurePriorityTextInput()
                configureStatusTextInput()
                configureSaveButton()
            }
        }

        setContentView(binding.root)
    }

    private fun configureAppBar() {
        val appbarMenu = binding.appbarMenu

        task?.let {
            appbarMenu.title = getString(R.string.edit_task_title)
        }
    }

    private fun configureTitleTextInput() {
        val titleTextInputEditText = binding.titleTextInputEditText

        titleTextInputEditText.setText(task?.title)

        titleTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateTitle()
            }
        }
    }

    private fun configureDescriptionTextInput() {
        val descriptionTextInputEditText = binding.descriptionTextInputEditText

        descriptionTextInputEditText.setText(task?.description)
    }

    private fun configureDurationTextInput() {
        val durationTextInputEditText = binding.durationTextInputEditText

        val defaultDuration = task?.duration

        defaultDuration?.let {
            durationTextInputEditText.setText("$defaultDuration")
        }

        durationTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateDuration()
            }
        }
    }

    private fun configureDueDateInput() {
        val dueDateTextInputEditText = binding.dueDateTextInputEditText

        val dueDate = task?.dueDate

        dueDate?.let {
            val formattedDate = formatDateForInput(dueDate)
            dueDateTextInputEditText.setText(formattedDate)
        }

        dueDateTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDueDatePicker()
            } else {
                validateDueDate()
            }
        }
    }

    private fun showDueDatePicker() {
        val dueDateTextInputEditText = binding.dueDateTextInputEditText

        val datePicker = getDatePicker(getString(R.string.due_date_picker_title)) { date ->
            dueDateTextInputEditText.setText(formatDateForInput(date))
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun configurePriorityTextInput() {
        val priorityTextInputAutoComplete = binding.priorityTextInputAutoComplete
        val priorityAutoComplete = priorityTextInputAutoComplete as? MaterialAutoCompleteTextView

        priorityTextInputAutoComplete.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePriority()
            }
        }

        val priorityOptions = arrayOf(Priority.LOW.text, Priority.MEDIUM.text, Priority.HIGH.text)
        priorityAutoComplete?.setSimpleItems(priorityOptions)

        priorityAutoComplete?.setText(task?.priority?.text, false)
    }

    private fun configureStatusTextInput() {
        val statusTextInputAutoComplete = binding.statusTextInputAutoComplete

        statusTextInputAutoComplete.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateStatus()
            }
        }

        val statusOptions = arrayOf(Status.PENDING.text, Status.IN_PROGRESS.text, Status.DONE.text)
        val statusAutoComplete = statusTextInputAutoComplete as? MaterialAutoCompleteTextView
        statusAutoComplete?.setSimpleItems(statusOptions)

        statusAutoComplete?.setText(task?.status?.text, false)
    }

    private fun configureSaveButton() {
        val saveButton = binding.saveButton

        saveButton.setOnClickListener {
            if (validateAllFields()) {
                val task = getTaskFromFormInputs()

                lifecycleScope.launch {
                    taskRepository.insert(task)
                    finish()
                }
            }
        }
    }

    private fun getTaskFromFormInputs(): Task {
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

        val priority = converters.fromStringToPriority(priorityString)
        val status = converters.fromStringToStatus(statusString)

        return Task(
            id = id,
            title = title,
            description = description,
            duration = BigDecimal(duration),
            dueDate = parseDate(dueDateString),
            priority = priority,
            status = status
        )
    }

    private fun validateAllFields(): Boolean {
        // It's necessary to use cache variables to force all the validation methods to be called
        val isTitleValid = validateTitle()
        val isDurationValid = validateDuration()
        val isDueDateValid = validateDueDate()
        val isPriorityValid = validatePriority()
        val isStatusValid = validateStatus()

        return isTitleValid && isDurationValid && isDueDateValid && isPriorityValid && isStatusValid
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
        val dueDateTextInput = binding.dueDateTextInput
        val dueDateTextInputEditText = binding.dueDateTextInputEditText

        val dueDateString = dueDateTextInputEditText.text.toString()

        if (dueDateString.hasValidDateFormat()) {
            clearErrors(dueDateTextInput)
            return true
        }

        dueDateTextInput.isErrorEnabled = true
        dueDateTextInput.error = getString(R.string.data_format_instruction)
        return false
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
}