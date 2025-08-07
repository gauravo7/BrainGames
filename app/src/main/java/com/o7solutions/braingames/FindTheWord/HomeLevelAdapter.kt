package com.example.zigzag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zigzag.Level
import com.o7solutions.braingames.R

class HomeLevelAdapter(
    private val levels: List<Level>,
    private val onLevelClicked: (Level) -> Unit
) : RecyclerView.Adapter<HomeLevelAdapter.LevelViewHolder>() {

    class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val levelNumberText: TextView = itemView.findViewById(R.id.tv_level_item_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_level, parent, false)
        return LevelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val level = levels[position]
        holder.levelNumberText.text = level.levelNumber.toString()
        holder.itemView.setOnClickListener {
            onLevelClicked(level)
        }
    }

    override fun getItemCount() = levels.size
}