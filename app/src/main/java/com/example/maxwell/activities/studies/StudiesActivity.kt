package com.example.maxwell.activities.studies

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.maxwell.R
import com.example.maxwell.adapters.StudyAdapter
import com.example.maxwell.database.Converters
import com.example.maxwell.databinding.ActivityStudiesBinding
import com.example.maxwell.databinding.DialogFilterStudiesBinding
import com.example.maxwell.models.Status
import com.example.maxwell.models.StudySubject
import com.example.maxwell.repository.StudyRepository
import com.example.maxwell.repository.StudySubjectRepository
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getDatePicker
import com.example.maxwell.utils.hasValidDateFormat
import com.example.maxwell.utils.parseDate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import java.util.Date

class StudiesActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityStudiesBinding.inflate(layoutInflater)
    }

    private val studyRepository by lazy {
        StudyRepository(this@StudiesActivity)
    }

    private val studySubjectRepository by lazy {
        StudySubjectRepository(this@StudiesActivity)
    }

    private val adapter by lazy {
        StudyAdapter(this@StudiesActivity, mutableListOf())
    }

    private val converters by lazy {
        Converters()
    }

    private var title = ""
    private var studySubject: StudySubject? = null
    private var status: Status? = null
    private var startingDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureRecyclerView()
        configureFab()
        configureAppbarMenu()

        setContentView(binding.root)
    }

    private fun configureRecyclerView() {
        lifecycleScope.launch {
            studyRepository.getStudies { studies ->
                val studiesRecyclerView = binding.studiesRecyclerView
                adapter.changeDataset(studies)
                studiesRecyclerView.adapter = adapter
            }
        }
    }

    private fun configureFab() {
        val addFab = binding.addFab

        addFab.setOnClickListener {
            val intent = Intent(this@StudiesActivity, StudyFormActivity::class.java)
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
        val dialogBinding = DialogFilterStudiesBinding.inflate(layoutInflater)

        bindSearchDialogViews(dialogBinding)

        MaterialAlertDialogBuilder(this@StudiesActivity)
            .setTitle(R.string.search_studies_dialog_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.cancel_button, null)
            .setPositiveButton(R.string.search_button) { _, _ ->
                filterStudies(dialogBinding)
            }
            .show()
    }

    private fun bindSearchDialogViews(dialogBinding: DialogFilterStudiesBinding) {
        val titleTextInputEditText = dialogBinding.titleTextInputEditText
        titleTextInputEditText.setText(title)

        lifecycleScope.launch {
            studySubjectRepository.getStudySubjects { studySubjects ->
                val subjectTextInputAutoComplete =
                    dialogBinding.subjectTextInputAutoComplete as? MaterialAutoCompleteTextView
                val studySubjectOptionsFromDb =
                    studySubjects.map { studySubject -> studySubject.name }
                val studySubjectOptions = mutableListOf(getString(R.string.all_subjects))
                studySubjectOptions.addAll(studySubjectOptionsFromDb)
                subjectTextInputAutoComplete?.setSimpleItems(studySubjectOptions.toTypedArray())

                val defaultStudySubject = studySubject?.name ?: getString(R.string.all_subjects)
                subjectTextInputAutoComplete?.setText(defaultStudySubject, false)
            }
        }

        val statusTextInputAutoComplete =
            dialogBinding.statusTextInputAutoComplete as? MaterialAutoCompleteTextView
        val statusOptions = arrayOf(
            getString(R.string.all_status),
            Status.PENDING.text,
            Status.IN_PROGRESS.text,
            Status.DONE.text
        )
        statusTextInputAutoComplete?.setSimpleItems(statusOptions)

        val defaultStatus = status?.text ?: getString(R.string.all_status)
        statusTextInputAutoComplete?.setText(defaultStatus, false)

        val startingDateTextInputEditText = dialogBinding.startingDateTextInputEditText

        startingDate?.let { startingDate ->
            startingDateTextInputEditText.setText(formatDateForInput(startingDate))
        }

        startingDateTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val datePicker = getDatePicker(getString(R.string.starting_date_picker_title)) { date ->
                        startingDateTextInputEditText.setText(formatDateForInput(date))
                    }

                datePicker.show(supportFragmentManager, "datePicker")
            } else {
                validateStartingDateFilter(dialogBinding)
            }
        }
    }

    private fun filterStudies(dialogBinding: DialogFilterStudiesBinding) {
        if (validateStartingDateFilter(dialogBinding)) {
            val subjectTextInputAutoComplete = dialogBinding.subjectTextInputAutoComplete
            val studySubjectName = subjectTextInputAutoComplete.text.toString()

            lifecycleScope.launch {
                studySubject = studySubjectRepository.getStudySubjectByName(studySubjectName)

                val query = getFilteringQuery(dialogBinding)

                lifecycleScope.launch {
                    val filteredStudies = studyRepository.filterStudies(query)
                    adapter.changeDataset(filteredStudies)
                }
            }
        }
    }

    private fun getFilteringQuery(dialogBinding: DialogFilterStudiesBinding): SimpleSQLiteQuery {
        var filter = getBaseFilteringQuery(dialogBinding)
        val args = mutableListOf<Any>(title)

        filter = addStatusFilter(dialogBinding, filter, args)
        filter = addStartingDateFilter(dialogBinding, filter, args)
        filter = addStudySubjectFilter(filter, args)

        filter = """
            SELECT original.id, original.title, original.duration, original.description, original.subjectId, original.links, original.status, grouped.startingDate FROM 
            (SELECT * FROM Study WHERE $filter ORDER BY startingDate DESC) AS original 
            LEFT JOIN (SELECT * FROM Study WHERE $filter GROUP BY startingDate) AS grouped
            ON original.id = grouped.id;
        """.trimIndent()
        args.addAll(args)

        return SimpleSQLiteQuery(filter, args.toTypedArray())
    }

    private fun getBaseFilteringQuery(dialogBinding: DialogFilterStudiesBinding): String {
        val titleTextInputEditText = dialogBinding.titleTextInputEditText
        title = titleTextInputEditText.text.toString()
        return "title LIKE '%' || ? || '%'"
    }

    private fun addStatusFilter(
        dialogBinding: DialogFilterStudiesBinding,
        filter: String,
        args: MutableList<Any>
    ): String {
        val statusTextInputAutoComplete = dialogBinding.statusTextInputAutoComplete
        val statusString = statusTextInputAutoComplete.text.toString()

        status = converters.fromStringToStatus(statusString)

        status?.let { status ->
            args.add(status.text)
            return "$filter AND status = ?"
        }

        return filter
    }

    private fun addStartingDateFilter(
        dialogBinding: DialogFilterStudiesBinding,
        filter: String,
        args: MutableList<Any>
    ): String {
        val startingDateTextInputEditText = dialogBinding.startingDateTextInputEditText
        val startingDateString = startingDateTextInputEditText.text.toString()

        startingDate = null

        if (startingDateString.trim() != "") {
            startingDate = parseDate(startingDateString)

            startingDate?.let { startingDate ->
                args.add(startingDate.time)
                return "$filter AND startingDate = ?"
            }
        }

        return filter
    }

    private fun addStudySubjectFilter(
        filter: String,
        args: MutableList<Any>
    ): String {
        studySubject?.let { studySubject ->
            args.add(studySubject.id)
            return "$filter AND subjectId = ?"
        }

        return filter
    }

    private fun validateStartingDateFilter(dialogBinding: DialogFilterStudiesBinding): Boolean {
        val startingDateTextInputLayout = dialogBinding.startingDateTextInputLayout
        val startingDateTextInputEditText = dialogBinding.startingDateTextInputEditText

        val startingDateString = startingDateTextInputEditText.text.toString()

        if (startingDateString.trim() != "" && !startingDateString.hasValidDateFormat()) {
            startingDateTextInputLayout.isErrorEnabled = true
            startingDateTextInputLayout.error = getString(R.string.data_format_instruction)
            return false
        }

        startingDateTextInputLayout.isErrorEnabled = false
        startingDateTextInputLayout.error = ""
        return true
    }
}