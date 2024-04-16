package com.example.maxwell.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.maxwell.R
import com.example.maxwell.data_store.Settings
import com.example.maxwell.databinding.ActivitySettingsBinding
import com.example.maxwell.utils.BackupManager
import com.example.maxwell.utils.formatTimestamp
import com.example.maxwell.works.SyncWorker
import com.example.maxwell.works.SyncWorker.Companion.WORK_NAME
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit


class SettingsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    private val settings by lazy {
        Settings(this@SettingsActivity)
    }

    private val backupManager by lazy {
        BackupManager(this@SettingsActivity, lifecycleScope)
    }

    private val workManager by lazy {
        WorkManager.getInstance(this@SettingsActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        fillSettingsFormWithDefaults()

        configureExportButton()

        configureImportButton()

        configureSaveButton()

        setContentView(binding.root)
    }

    private fun configureExportButton() {
        binding.exportDataButton.setOnClickListener {
            val loadingDialog = MaterialAlertDialogBuilder(this@SettingsActivity)
                .setTitle(R.string.export_data_dialog_title)
                .setMessage(R.string.export_data_dialog_message)
                .setView(R.layout.dialog_export_data)
                .setCancelable(false)
                .show()

            createBackup(loadingDialog)
        }
    }

    private fun createBackup(loadingDialog: AlertDialog) {
        lifecycleScope.launch(IO) {
            backupManager.createBackup({
                loadingDialog.dismiss()

                MaterialAlertDialogBuilder(this@SettingsActivity)
                    .setTitle(R.string.successful_export_dialog_title)
                    .setMessage(R.string.successful_export_dialog_message)
                    .setNeutralButton(R.string.successful_export_close_button, null)
                    .setCancelable(false)
                    .show()
            },
            {
                loadingDialog.dismiss()

                MaterialAlertDialogBuilder(this@SettingsActivity)
                    .setTitle(R.string.error_export_dialog_title)
                    .setMessage(R.string.error_export_dialog_message)
                    .setNeutralButton(R.string.error_export_close_button, null)
                    .setCancelable(false)
                    .show()
            })
        }
    }

    private fun configureImportButton() {
        binding.importDataButton.setOnClickListener {
            MaterialAlertDialogBuilder(this@SettingsActivity)
                .setTitle(R.string.confirm_restore_backup_dialog_title)
                .setMessage(R.string.confirm_restore_backup_dialog_message)
                .setPositiveButton(R.string.confirm_restore_backup_dialog_positive_button) { dialog, _ ->
                    dialog.dismiss()

                    val loadingDialog = MaterialAlertDialogBuilder(this@SettingsActivity)
                        .setTitle(R.string.import_data_dialog_title)
                        .setMessage(R.string.import_data_dialog_message)
                        .setView(R.layout.dialog_export_data)
                        .setCancelable(false)
                        .show()

                    restoreBackup(loadingDialog)
                }
                .setNegativeButton(R.string.confirm_restore_backup_dialog_negative_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun restoreBackup(loadingDialog: AlertDialog) {
        lifecycleScope.launch(IO) {
            backupManager.restoreBackup({
                loadingDialog.dismiss()

                MaterialAlertDialogBuilder(this@SettingsActivity)
                    .setTitle(R.string.successful_import_dialog_title)
                    .setMessage(R.string.successful_import_dialog_message)
                    .setNeutralButton(R.string.successful_import_dialog_button, null)
                    .setCancelable(false)
                    .show()
            },
            {
                loadingDialog.dismiss()

                MaterialAlertDialogBuilder(this@SettingsActivity)
                    .setTitle(R.string.error_import_dialog_title)
                    .setMessage(R.string.error_import_dialog_message)
                    .setNeutralButton(R.string.error_import_dialog_button, null)
                    .setCancelable(false)
                    .show()
            })
        }
    }

    private fun fillSettingsFormWithDefaults() {
        fillUsernameInputWithDefault()
        fillSynchronizationData()
    }

    private fun fillUsernameInputWithDefault() {
        val usernameTextInputEditText = binding.usernameTextInputEditText

        lifecycleScope.launch {
            settings.getUsername().collect { username ->
                usernameTextInputEditText.setText(username)
            }
        }
    }

    private fun fillSynchronizationData() {
        val dailySynchronizationSwitch = binding.dailySynchronizationSwitch
        val lastBackupTimestampTextView = binding.lastBackupTimestampTextView

        lifecycleScope.launch {
            settings.isDailySyncEnabled().collect { dailySyncEnabled ->
                dailySynchronizationSwitch.isChecked = dailySyncEnabled?:false
            }
        }

        lifecycleScope.launch {
            settings.getLastBackupTimestamp().collect { lastBackupTimestamp ->
                val formattedLastBackupTimestamp = if (lastBackupTimestamp != null) {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = lastBackupTimestamp
                    formatTimestamp(calendar.time)
                } else {
                    "-"
                }

                lastBackupTimestampTextView.text = getString(
                    R.string.last_backup_timestamp,
                    formattedLastBackupTimestamp
                )
            }
        }
    }

    private fun configureSaveButton() {
        val usernameTextInputEditText = binding.usernameTextInputEditText
        val dailySynchronizationSwitch = binding.dailySynchronizationSwitch
        val saveButton = binding.saveButton

        saveButton.setOnClickListener { _ ->
            val usernameInput = usernameTextInputEditText.text.toString()
            val enableDailySynchronization = dailySynchronizationSwitch.isChecked
            lifecycleScope.launch {
                settings.setUsername(usernameInput)
                settings.setDailySyncEnabled(enableDailySynchronization)

                if (enableDailySynchronization) {
                    val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                        1,
                        TimeUnit.DAYS
                    ).build()

                    workManager.enqueueUniquePeriodicWork(
                        WORK_NAME,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        syncRequest
                    )
                } else {
                    workManager.cancelUniqueWork(WORK_NAME)
                }

                finish()
            }
        }
    }
}