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
import com.example.maxwell.repository.StudySubjectRepository
import com.example.maxwell.utils.createChipView
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getDatePicker
import com.example.maxwell.utils.hasValidDateFormat
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale

class StudyFormActivity : FormActivity() {
    private var study: Study? = null

    private val binding by lazy {
        ActivityStudyFormBinding.inflate(layoutInflater)
    }

    private val id by lazy {
        intent.getLongExtra("id", 0)
    }

    private val studyDao by lazy {
        AppDatabase.instantiate(this@StudyFormActivity).studyDao()
    }

    private val studySubjectRepository by lazy {
        StudySubjectRepository(this@StudyFormActivity)
    }

    private val converters by lazy {
        Converters()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            studyDao.getStudyById(id).collect {studyFromDb ->
                study = studyFromDb

                configureAppBar()
                configureTitleTextInput()
                configureDescriptionTextInput()
                configureDurationTextInput()
                configureSubjectTextInput()
                configureStudySubjectsManagement()
                configureLinksTextInput()
                configureStatusTextInput()
                configureStartingDateInput()
                configureSaveButton()
            }
        }

        setContentView(binding.root)
    }

    private fun configureAppBar() {
        val appbarMenu = binding.appbarMenu

        study?.let {
            appbarMenu.title = getString(R.string.edit_study_title)
        }
    }

    private fun configureTitleTextInput() {
        val titleTextInputEditText = binding.titleTextInputEditText

        titleTextInputEditText.setText(study?.title)

        titleTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateTitle()
            }
        }
    }

    private fun configureDescriptionTextInput() {
        val descriptionTextInputEditText = binding.descriptionTextInputEditText

        descriptionTextInputEditText.setText(study?.description)
    }

    private fun configureDurationTextInput() {
        val durationTextInputEditText = binding.durationTextInputEditText

        val defaultDuration = study?.duration

        defaultDuration?.let {
            durationTextInputEditText.setText("$defaultDuration")
        }

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
            studySubjectRepository.getStudySubjects { studySubjects ->
                val studySubjectOptions = studySubjects
                    .map { studySubject -> studySubject.name }.toTypedArray()
                subjectAutoComplete?.setSimpleItems(studySubjectOptions)
            }
        }

        lifecycleScope.launch {
            val subjectId = study?.subjectId ?: 0

            studySubjectRepository.getStudySubjectById(subjectId) { studySubject ->
                subjectAutoComplete?.setText(studySubject?.name, false)
            }
        }
    }

    private fun configureStudySubjectsManagement() {
        val manageStudySubjectConstraintLayout = binding.manageStudySubjectTextView

        manageStudySubjectConstraintLayout.setOnClickListener {
            displayStudySubjectManagementDialog()
        }
    }

    private fun displayStudySubjectManagementDialog() {
        val dialogBinding = DialogStudySubjectFormBinding.inflate(layoutInflater)

        lifecycleScope.launch {
            val studySubjectsChipGroup = dialogBinding.studySubjectsChipGroup

            studySubjectRepository.getStudySubjects { studySubjects ->
                studySubjectsChipGroup.removeAllViews()

                studySubjects.forEach { studySubject ->
                    createSubjectChipView(studySubject, studySubjectsChipGroup)
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
                if (validateSubjectNameTextInput(dialogBinding)) {
                    val nameTextInputEditText = dialogBinding.nameTextInputEditText
                    val name = nameTextInputEditText.text.toString()

                    val studySubject = StudySubject(name = name)
                    studySubjectRepository.insert(studySubject)
                    nameTextInputEditText.setText("")
                }
            }
        }
    }

    private fun createSubjectChipView(
        studySubject: StudySubject,
        studySubjectsChipGroup: ChipGroup
    ) {
        val chip = createChipView(this@StudyFormActivity, studySubject.name)

        studySubjectsChipGroup.addView(chip)
        chip.setOnCloseIconClickListener {
            lifecycleScope.launch {
                studySubjectRepository.delete(studySubject)
                studySubjectsChipGroup.removeView(chip)
            }
        }
    }

    private suspend fun validateSubjectNameTextInput(binding: DialogStudySubjectFormBinding): Boolean {
        val nameTextInputLayout = binding.nameTextInputLayout
        val nameTextInputEditText = binding.nameTextInputEditText

        val name = nameTextInputEditText.text.toString()

        if (name.trim() == "") {
            markFieldAsRequired(nameTextInputLayout)
            return false
        }

        val nameAvailable = studySubjectRepository.getStudySubjectByName(name) == null

        if (!nameAvailable) {
            nameTextInputLayout.isErrorEnabled = true
            nameTextInputLayout.error =
                getString(R.string.error_study_subject_name_unavailable)
            return false
        }

        clearErrors(nameTextInputLayout)
        return true
    }

    private fun configureLinksTextInput() {
        val linksTextInputEditText = binding.linksTextInputEditText

        linksTextInputEditText.setText(study?.links)
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

        statusAutoComplete?.setText(study?.status?.text, false)
    }

    private fun configureStartingDateInput() {
        val startingDateTextInputEditText = binding.startingDateTextInputEditText

        val startingDate = study?.startingDate

        startingDate?.let {
            val formattedDate = formatDateForInput(startingDate)
            startingDateTextInputEditText.setText(formattedDate)
        }

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

        val datePicker = getDatePicker(getString(R.string.starting_date_picker_title)) { date ->
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

                    val subject = studySubjectRepository.getStudySubjectByName(subjectString)

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
            id = id,
            title = title,
            description = description,
            duration = duration,
            subjectId = subject.id,
            links = links,
            status = status,
            startingDate = startingDate
        )
    }

    private fun validateAllFields(): Boolean {
        // It's necessary to use cache variables to force all the validation methods to be called
        val isTitleValid = validateTitle()
        val isDurationValid = validateDuration()
        val isStudySubjectValid = validateStudySubject()
        val isStatusValid = validateStatus()
        val isStartingDateValid = validateStartingDate()

        return isTitleValid && isDurationValid && isStudySubjectValid && isStatusValid && isStartingDateValid
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