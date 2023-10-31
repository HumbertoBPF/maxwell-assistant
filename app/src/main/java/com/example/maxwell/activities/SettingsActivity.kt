package com.example.maxwell.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.data_store.Settings
import com.example.maxwell.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    private val settings by lazy {
        Settings(this@SettingsActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usernameTextInputEditText = binding.usernameTextInputEditText

        lifecycleScope.launch {
            settings.getUsername().collect { username ->
                usernameTextInputEditText.setText(username)
            }
        }

        binding.saveButton.setOnClickListener { _ ->
            val usernameInput = binding.usernameTextInputEditText.text.toString()

            lifecycleScope.launch {
                settings.setUsername(usernameInput)
                finish()
            }
        }

        setContentView(binding.root)
    }
}