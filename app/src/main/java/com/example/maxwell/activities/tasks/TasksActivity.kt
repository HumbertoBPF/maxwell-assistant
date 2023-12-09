package com.example.maxwell.activities.tasks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.maxwell.R
import com.example.maxwell.adapters.TaskAdapter
import com.example.maxwell.database.Converters
import com.example.maxwell.databinding.ActivityTasksBinding
import com.example.maxwell.databinding.DialogFilterTasksBinding
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.repository.TaskRepository
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getDatePicker
import com.example.maxwell.utils.hasValidDateFormat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TasksActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityTasksBinding.inflate(layoutInflater)
    }

    private val taskRepository by lazy {
        TaskRepository(this@TasksActivity)
    }

    private val adapter by lazy {
        TaskAdapter(this@TasksActivity, mutableListOf())
    }

    private val converters by lazy {
        Converters()
    }

    private var title = ""
    private var dueDate: Date? = null
    private var priority: Priority? = null
    private var status: Status? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureRecyclerView()
        configureFab()
        configureAppbarMenu()

        setContentView(binding.root)
    }

    private fun configureFab() {
        val addFab = binding.addFab

        addFab.setOnClickListener {
            val intent = Intent(this@TasksActivity, TaskFormActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configureRecyclerView() {
        lifecycleScope.launch {
            taskRepository.getTasks { tasks ->
                val tasksRecyclerView = binding.tasksRecyclerView
                adapter.changeDataset(tasks)
                tasksRecyclerView.adapter = adapter
            }
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
        val dialogBinding = DialogFilterTasksBinding.inflate(layoutInflater)

        bindSearchDialogViews(dialogBinding)

        MaterialAlertDialogBuilder(this@TasksActivity)
            .setTitle(R.string.search_tasks_dialog_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.cancel_button, null)
            .setPositiveButton(R.string.search_button) { _, _ ->
                filterTasks(dialogBinding)
            }
            .show()
    }

    private fun bindSearchDialogViews(dialogBinding: DialogFilterTasksBinding) {
        val titleTextInputEditText = dialogBinding.titleTextInputEditText
        titleTextInputEditText.setText(title)

        val dueDateTextInputEditText = dialogBinding.dueToTextInputEditText

        dueDate?.let {dueDate ->
            dueDateTextInputEditText.setText(formatDateForInput(dueDate))
        }

        dueDateTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val datePicker = getDatePicker(getString(R.string.due_date_picker_title)) { date ->
                    dueDateTextInputEditText.setText(formatDateForInput(date))
                }

                datePicker.show(supportFragmentManager, "datePicker")
            } else {
                validateDueDateFilter(dialogBinding)
            }
        }

        val priorityTextInputAutoComplete =
            dialogBinding.priorityTextInputAutoComplete as? MaterialAutoCompleteTextView
        val priorityOptions = arrayOf(getString(R.string.all_priorities), Priority.LOW.text, Priority.MEDIUM.text, Priority.HIGH.text)
        priorityTextInputAutoComplete?.setSimpleItems(priorityOptions)

        val defaultPriorityString = priority?.text ?: getString(R.string.all_priorities)
        priorityTextInputAutoComplete?.setText(defaultPriorityString, false)

        val statusTextInputAutoComplete =
            dialogBinding.statusTextInputAutoComplete as? MaterialAutoCompleteTextView
        val statusOptions = arrayOf(getString(R.string.all_status), Status.PENDING.text, Status.IN_PROGRESS.text, Status.DONE.text)
        statusTextInputAutoComplete?.setSimpleItems(statusOptions)

        val defaultStatusString = status?.text ?: getString(R.string.all_status)
        statusTextInputAutoComplete?.setText(defaultStatusString, false)
    }

    private fun filterTasks(dialogBinding: DialogFilterTasksBinding) {
        if (validateDueDateFilter(dialogBinding)) {
            val query = getFilteringQuery(dialogBinding)

            lifecycleScope.launch {
                val filteredTasks = taskRepository.filterTasks(query)
                adapter.changeDataset(filteredTasks)
            }
        }
    }

    private fun getFilteringQuery(dialogBinding: DialogFilterTasksBinding): SimpleSQLiteQuery {
        var filter = getBaseFilter(dialogBinding)
        val args = mutableListOf<Any>(title)

        filter = addDueDateFilter(dialogBinding, filter, args)
        filter = addPriorityFilter(dialogBinding, filter, args)
        filter = addStatusFilter(dialogBinding, filter, args)

        filter = """
            SELECT original.id, original.title, original.duration, grouped.dueDate, original.priority, original.status FROM 
            (SELECT * FROM Task WHERE $filter ORDER BY dueDate DESC) AS original 
            LEFT JOIN (SELECT * FROM Task WHERE $filter GROUP BY dueDate) AS grouped 
            ON original.id = grouped.id;
        """.trimIndent()
        args.addAll(args)

        return SimpleSQLiteQuery(filter, args.toTypedArray())
    }

    private fun getBaseFilter(dialogBinding: DialogFilterTasksBinding): String {
        val titleTextInputEditText = dialogBinding.titleTextInputEditText
        title = titleTextInputEditText.text.toString()
        return "title LIKE '%' || ? || '%'"
    }

    private fun addDueDateFilter(
        dialogBinding: DialogFilterTasksBinding,
        filter: String,
        args: MutableList<Any>
    ): String {
        val dueToTextInputEditText = dialogBinding.dueToTextInputEditText
        val dueDateString = dueToTextInputEditText.text.toString()

        dueDate = null

        if (dueDateString.trim() != "") {
            val sdf = SimpleDateFormat("MM-dd-yyyy", Locale.US)
            dueDate = sdf.parse(dueDateString)

            dueDate?.let { dueDate ->
                args.add(dueDate.time)
                return "$filter AND dueDate = ?"
            }
        }

        return filter
    }

    private fun addPriorityFilter(
        dialogBinding: DialogFilterTasksBinding,
        filter: String,
        args: MutableList<Any>
    ): String {
        val priorityTextInputAutoComplete = dialogBinding.priorityTextInputAutoComplete
        val priorityString = priorityTextInputAutoComplete.text.toString()

        priority = converters.fromStringToPriority(priorityString)

        priority?.let { priority ->
            args.add(priority.text)
            return "$filter AND priority = ?"
        }

        return filter
    }

    private fun addStatusFilter(
        dialogBinding: DialogFilterTasksBinding,
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

    private fun validateDueDateFilter(dialogBinding: DialogFilterTasksBinding): Boolean {
        val dueDateTextInputLayout = dialogBinding.dueToTextInputLayout
        val dueToTextInputEditText = dialogBinding.dueToTextInputEditText

        val dueDateString = dueToTextInputEditText.text.toString()

        if (dueDateString.trim() != "" && !dueDateString.hasValidDateFormat()) {
            dueDateTextInputLayout.isErrorEnabled = true
            dueDateTextInputLayout.error = getString(R.string.data_format_instruction)
            return false
        }

        dueDateTextInputLayout.isErrorEnabled = false
        dueDateTextInputLayout.error = ""
        return true
    }
}