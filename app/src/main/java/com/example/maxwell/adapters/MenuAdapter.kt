package com.example.maxwell.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.maxwell.databinding.MenuItemBinding
import com.example.maxwell.models.MenuItem

class MenuAdapter(
    private val context: Context,
    private val menuItems: List<MenuItem>
): Adapter<MenuAdapter.ViewHolder>() {
    inner class ViewHolder(binding: MenuItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val icon: ImageView = binding.iconImageView
        private val name: TextView = binding.nameTextView

        fun bind(menuItem: MenuItem) {
            itemView.setBackgroundResource(menuItem.color)
            icon.setImageResource(menuItem.icon)
            name.text = menuItem.name

            itemView.setOnClickListener {
                val intent = Intent(context, menuItem.nextActivity)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = MenuItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = menuItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)
    }
}