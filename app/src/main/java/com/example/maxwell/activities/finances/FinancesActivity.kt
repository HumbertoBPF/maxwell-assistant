package com.example.maxwell.activities.finances

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.maxwell.databinding.ActivityFinancesBinding

class FinancesActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFinancesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val addFab = binding.addFab
        addFab.setOnClickListener {
            val intent = Intent(this@FinancesActivity, FinanceFormActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }
}