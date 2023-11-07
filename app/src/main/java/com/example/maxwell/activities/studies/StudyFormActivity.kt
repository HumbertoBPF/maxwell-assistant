package com.example.maxwell.activities.studies

import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.R
import com.example.maxwell.activities.FormActivity
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.database.Converters
import com.example.maxwell.databinding.ActivityStudyFormBinding
import com.example.maxwell.databinding.DialogStudySubjectFormBinding
import com.example.maxwell.models.Status
import com.example.maxwell.models.Study
import com.example.maxwell.models.StudySubject
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getDatePicker
import com.example.maxwell.utils.hasValidDateFormat
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale

class StudyFormActivity : FormActivity() {
    private val binding by lazy {
        ActivityStudyFormBinding.inflate(layoutInflater)
    }

    private val studySubjectDao by lazy {
        AppDatabase.instantiate(this@StudyFormActivity).studySubjectDao()
    }

    private val converters by lazy {
        Converters()
    }

    private val studyDao by lazy {
        AppDatabase.instantiate(this@StudyFormActivity).studyDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureTitleTextInput()
        configureDurationTextInput()
        configureSubjectTextInput()
        configureStudySubjectsManagement()
        configureStatusTextInput()
        configureStartingDateInput()
        configureSaveButton()

        setContentView(binding.root)
    }

    private fun configureTitleTextInput() {
        val titleTextInputEditText = binding.titleTextInputEditText

        titleTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateTitle()
            }
        }
    }

    private fun configureDurationTextInput() {
        val durationTextInputEditText = binding.durationTextInputEditText

        durationTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateDuration()
            }
        }
    }

    private fun configureSubjectTextInput() {
        val subjectTextInputAutoComplete = binding.subjectTextInputAutoComplete
        val subjectAutoComplete = subjectTextInputAutoComplete as? MaterialAutoCompleteTextView

        subjectAutoComplete?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateStudySubject()
            }
        }

        lifecycleScope.launch {
            studySubjectDao.getStudySubjects().collect { studySubjects ->
                val studySubjectOptions = studySubjects
                    .map { studySubject -> studySubject.name }.toTypedArray()
                subjectAutoComplete?.setSimpleItems(studySubjectOptions)
            }
        }
    }

    private fun configureStudySubjectsManagement() {
        val manageStudySubjectConstraintLayout = binding.manageStudySubjectTextView

        manageStudySubjectConstraintLayout.setOnClickListener {
            displayCreateStudySubjectDialog()
        }
    }

    private fun displayCreateStudySubjectDialog() {
        val dialogBinding = DialogStudySubjectFormBinding.inflate(layoutInflater)

        lifecycleScope.launch {
            val studySubjectsChipGroup = dialogBinding.studySubjectsChipGroup

            studySubjectDao.getStudySubjects().collect {studySubjects ->
                studySubjectsChipGroup.removeAllViews()

                studySubjects.forEach { studySubject ->
                    val chip = Chip(this@StudyFormActivity)
                    chip.text = studySubject.name
                    chip.isChipIconVisible = false
                    chip.isCloseIconVisible = true
                    chip.isClickable = true
                    chip.isCheckable = false
                    studySubjectsChipGroup.addView(chip)
                    chip.setOnCloseIconClickListener {
                        lifecycleScope.launch {
                            studySubjectDao.delete(studySubject)
                            studySubjectsChipGroup.removeView(chip)
                        }
                    }
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(this@StudyFormActivity)
            .setTitle(R.string.study_subject_dialog_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.study_subject_dialog_negative_button) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.study_subject_dialog_positive_button) { _, _ -> }
            .show()

        dialog.getButton(BUTTON_POSITIVE).setOnClickListener {
            lifecycleScope.launch {
                val nameTextInputLayout = dialogBinding.nameTextInputLayout
                val nameTextInputEditText = dialogBinding.nameTextInputEditText

                val name = nameTextInputEditText.text.toString()

                if (name.trim() == "") {
                    markFieldAsRequired(nameTextInputLayout)
                    return@launch
                }

                val nameAvailable = studySubjectDao.getStudySubjectsByName(name) != null

                if (nameAvailable) {
                    val studySubject = StudySubject(name = name)
                    studySubjectDao.insert(studySubject)
                    nameTextInputEditText.setText("")
                } else {
                    nameTextInputLayout.isErrorEnabled = true
                    nameTextInputLayout.error =
                        getString(R.string.error_study_subject_name_unavailable)
                }
            }
        }
    }

    private fun configureStatusTextInput() {
        val statusTextInputAutoComplete = binding.statusTextInputAutoComplete
        val statusAutoComplete = statusTextInputAutoComplete as? MaterialAutoCompleteTextView

        statusAutoComplete?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateStatus()
            }
        }

        val statusOptions = arrayOf(Status.PENDING.text, Status.IN_PROGRESS.text, Status.DONE.text)
        statusAutoComplete?.setSimpleItems(statusOptions)
    }

    private fun configureStartingDateInput() {
        val startingDateTextInputEditText = binding.startingDateTextInputEditText

        startingDateTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDueDatePicker()
            } else {
                validateStartingDate()
            }
        }
    }

    private fun showDueDatePicker() {
        val startingDateTextInputEditText = binding.startingDateTextInputEditText

        val datePicker = getDatePicker("Select the starting date") {date ->
            startingDateTextInputEditText.setText(formatDateForInput(date))
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun configureSaveButton() {
        val saveButton = binding.saveButton

        saveButton.setOnClickListener {
            if (validateAllFields()) {
                lifecycleScope.launch {
                    val subjectTextInputAutoComplete = binding.subjectTextInputAutoComplete
                    val subjectString = subjectTextInputAutoComplete.text.toString()

                    val subject = studySubjectDao.getStudySubjectsByName(subjectString)

                    subject?.let {
                        val study = getStudyFromFormInputs(subject)
                        studyDao.insert(study)
                        finish()
                    }
                }
            }
        }
    }

    private fun getStudyFromFormInputs(subject: StudySubject): Study {
        val titleTextInputEditText = binding.titleTextInputEditText
        val descriptionTextInputEditText = binding.descriptionTextInputEditText
        val durationTextInputEditText = binding.durationTextInputEditText
        val linksTextInputEditText = binding.linksTextInputEditText
        val statusTextInputAutoComplete = binding.statusTextInputAutoComplete
        val startingDateTextInputEditText = binding.startingDateTextInputEditText

        val title = titleTextInputEditText.text.toString()
        val description = descriptionTextInputEditText.text.toString()
        val duration = BigDecimal(durationTextInputEditText.text.toString())
        val links = linksTextInputEditText.text.toString()
        val statusString = statusTextInputAutoComplete.text.toString()
        val startingDateString = startingDateTextInputEditText.text.toString()

        val status = converters.fromStringToStatus(statusString)

        val sdf = SimpleDateFormat("MM-dd-yyyy", Locale.US)
        val startingDate = sdf.parse(startingDateString)

        return Study(
            title = title,
            description = description,
            duration = duration,
            subjectId = subject.id,
            links = links,
            status = status,
            startingDate = startingDate
        )
    }

    private fun validateAllFields() = validateTitle() &&
            validateDuration() &&
            validateStudySubject() &&
            validateStatus() &&
            validateStartingDate()

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

    private fun validateDuration(): Boolean {
        val durationTextInputLayout = binding.durationTextInputLayout
        val durationTextInputEditText = binding.durationTextInputEditText

        val duration = durationTextInputEditText.text.toString()

        if (duration.trim() == "") {
            markFieldAsRequired(durationTextInputLayout)
            return false
        }

        clearErrors(durationTextInputLayout)
        return true
    }

    private fun validateStudySubject(): Boolean {
        val subjectTextInput = binding.subjectTextInput
        val subjectTextInputAutoComplete = binding.subjectTextInputAutoComplete

        val subject = subjectTextInputAutoComplete.text.toString()

        if (subject == "") {
            markFieldAsRequired(subjectTextInput)
            return false
        }

        clearErrors(subjectTextInput)
        return true
    }

    private fun validateStatus(): Boolean {
        val statusTextInput = binding.statusTextInput
        val statusTextInputAutoComplete = binding.statusTextInputAutoComplete

        val status = statusTextInputAutoComplete.text.toString()

        if (status == "") {
            markFieldAsRequired(statusTextInput)
            return false
        }

        clearErrors(statusTextInput)
        return true
    }

    private fun validateStartingDate(): Boolean {
        val startingDateTextInputLayout = binding.startingDateTextInputLayout
        val startingDateTextInputEditText = binding.startingDateTextInputEditText

        val startingDate = startingDateTextInputEditText.text.toString()

        if (startingDate.hasValidDateFormat()) {
            clearErrors(startingDateTextInputLayout)
            return true
        }

        startingDateTextInputLayout.isErrorEnabled = true
        startingDateTextInputLayout.error = getString(R.string.data_format_instruction)
        return false
    }
}