package com.example.maxwell.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.R
import com.example.maxwell.data_store.Settings
import com.example.maxwell.databinding.ActivitySettingsBinding
import com.example.maxwell.utils.padWithZeros
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_12H
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

class SettingsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    private val settings by lazy {
        Settings(this@SettingsActivity)
    }

    private val calendar by lazy {
        Calendar.getInstance()
    }

    private val pickerBuilder by lazy {
        MaterialTimePicker.Builder()
            .setTimeFormat(CLOCK_12H)
            .setTitleText(getString(R.string.synchronization_time_picker_label))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        fillSettingsFormWithDefaults()

        configureDataSynchronizationSection()

        configureSaveButton()

        setContentView(binding.root)
    }

    private fun fillSettingsFormWithDefaults() {
        fillUsernameInputWithDefault()
        fillSynchronizationDataWithDefault()
    }

    private fun fillUsernameInputWithDefault() {
        val usernameTextInputEditText = binding.usernameTextInputEditText

        lifecycleScope.launch {
            settings.getUsername().collect { username ->
                usernameTextInputEditText.setText(username)
            }
        }
    }

    private fun fillSynchronizationDataWithDefault() {
        val dailySynchronizationSwitch = binding.dailySynchronizationSwitch
        val synchronizationTimeTextInputEditText = binding.synchronizationTimeTextInputEditText

        lifecycleScope.launch {
            settings.getDailySynchronizationTime().collect { dailySynchronizationTime ->
                val hasDailySynchronizationTimeSetting = (dailySynchronizationTime != null)

                dailySynchronizationSwitch.isChecked = hasDailySynchronizationTimeSetting

                if (hasDailySynchronizationTimeSetting) {
                    synchronizationTimeTextInputEditText.setText(dailySynchronizationTime)
                }
            }
        }
    }

    private fun configureDataSynchronizationSection() {
        val dailySynchronizationSwitch = binding.dailySynchronizationSwitch
        val synchronizationTimeTextInput = binding.synchronizationTimeTextInput
        val synchronizationTimeTextInputEditText = binding.synchronizationTimeTextInputEditText

        synchronizationTimeTextInput.isEnabled = dailySynchronizationSwitch.isChecked

        dailySynchronizationSwitch.setOnCheckedChangeListener { _, checked ->
            synchronizationTimeTextInput.isEnabled = checked
            synchronizationTimeTextInputEditText.setText("")
        }

        synchronizationTimeTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showSynchronizationTimePicker()
            } else {
                validateSynchronizationTimeFormat()
            }
        }
    }

    private fun showSynchronizationTimePicker() {
        val currentHour = calendar.get(HOUR_OF_DAY)
        val currentMinute = calendar.get(MINUTE)

        pickerBuilder.setHour(currentHour)
        pickerBuilder.setMinute(currentMinute)

        val picker = pickerBuilder.build()

        picker.addOnPositiveButtonClickListener {
            handleSynchronizationTimePickerSelection(picker)
        }

        picker.addOnNegativeButtonClickListener {
            validateSynchronizationTimeFormat()
        }

        picker.show(supportFragmentManager, "pickerTag")
    }

    private fun handleSynchronizationTimePickerSelection(picker: MaterialTimePicker) {
        val synchronizationTimeTextInputEditText = binding.synchronizationTimeTextInputEditText

        val hour = picker.hour
        val paddedMinute = padWithZeros(picker.minute)

        val synchronizationTime = if (hour > 12) {
            val paddedHour = padWithZeros(hour - 12)
            "${paddedHour}:$paddedMinute PM"
        } else {
            val paddedHour = padWithZeros(hour)
            "$paddedHour:$paddedMinute AM"
        }

        validateSynchronizationTimeFormat()

        synchronizationTimeTextInputEditText.setText(synchronizationTime)
        synchronizationTimeTextInputEditText.clearFocus()
    }

    private fun configureSaveButton() {
        val usernameTextInputEditText = binding.usernameTextInputEditText
        val dailySynchronizationSwitch = binding.dailySynchronizationSwitch
        val synchronizationTimeTextInputEditText = binding.synchronizationTimeTextInputEditText
        val saveButton = binding.saveButton

        saveButton.setOnClickListener { _ ->
            val usernameInput = usernameTextInputEditText.text.toString()
            val enableDailySynchronization = dailySynchronizationSwitch.isChecked
            val synchronizationTime = if (enableDailySynchronization) {
                synchronizationTimeTextInputEditText.text.toString()
            } else {
                null
            }

            if (!validateSynchronizationTimeFormat()) {
                return@setOnClickListener
            }

            lifecycleScope.launch {
                settings.setUsername(usernameInput)
                settings.setDailySynchronizationTime(synchronizationTime)
                finish()
            }
        }
    }

    private fun validateSynchronizationTimeFormat(): Boolean {
        val dailySynchronizationSwitch = binding.dailySynchronizationSwitch
        val synchronizationTimeTextInputEditText = binding.synchronizationTimeTextInputEditText
        val synchronizationTimeTextInput = binding.synchronizationTimeTextInput

        if (dailySynchronizationSwitch.isChecked) {
            val synchronizationTime = synchronizationTimeTextInputEditText.text.toString()

            val regexTime = Regex("^[0-1][0-9]:[0-5]\\d [A,P]M$")

            if (regexTime.matches(synchronizationTime)) {
                val hour = synchronizationTime.split(":")[0].toInt()

                if ((hour == 0) || (hour > 12)) {
                    synchronizationTimeTextInput.isErrorEnabled = true
                    synchronizationTimeTextInput.error = getString(R.string.time_format_instruction)
                    return false
                }

                synchronizationTimeTextInput.isErrorEnabled = false
                synchronizationTimeTextInput.error = ""
            } else {
                synchronizationTimeTextInput.isErrorEnabled = true
                synchronizationTimeTextInput.error = getString(R.string.time_format_instruction)
                return false
            }

            return true
        }

        return true
    }
}