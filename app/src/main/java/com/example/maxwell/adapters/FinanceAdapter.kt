package com.example.maxwell.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maxwell.activities.finances.FinanceDetailActivity
import com.example.maxwell.databinding.FinanceItemBinding
import com.example.maxwell.models.Finance
import com.example.maxwell.utils.formatDatePretty

class FinanceAdapter(
    private val context: Context,
    private val finances: MutableList<Finance>
): RecyclerView.Adapter<FinanceAdapter.ViewHolder>(){
    inner class ViewHolder(binding: FinanceItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val dateTextView = binding.dateTextView
        private val financeContainer = binding.financeContainer
        private val titleTextView = binding.titleTextView
        private val valueTextView = binding.valueTextView

        fun bind(finance: Finance) {
            val date = finance.date

            if (date == null) {
                dateTextView.visibility = GONE
            } else {
                dateTextView.visibility = VISIBLE
                dateTextView.text = formatDatePretty(date)
            }

            titleTextView.text = finance.title

            val type = finance.type

            type?.let {
                val formattedValue = finance.formatValue()
                valueTextView.setTextColor(context.getColor(type.color))
                valueTextView.text = formattedValue
            }

            financeContainer.setOnClickListener {
                val intent = Intent(context, FinanceDetailActivity::class.java)
                intent.putExtra("id", finance.id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = FinanceItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = finances.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val finance = finances[position]
        holder.bind(finance)
    }

    fun changeDataset(newDataset: List<Finance>) {
        finances.clear()
        finances.addAll(newDataset)
        notifyDataSetChanged()
    }
}