package com.o7solutions.braingames.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.o7solutions.braingames.R

import java.util.ArrayList
// Define an interface for click events
interface OnLevelClickListener {
    fun onLevelClicked(levelNumber: Int)
}

class LevelsAdapter(
    private val list: ArrayList<String>,
    private val size: Int,
    private val unlocked: Int,
    private val listener: OnLevelClickListener
) : RecyclerView.Adapter<LevelsAdapter.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.level_image)
        var levelTV: TextView = view.findViewById(R.id.level_TV)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LevelsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_levels, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelsAdapter.ViewHolder, position: Int) {
        holder.apply {
            if (position <= unlocked) {
                // Level is unlocked
                levelTV.visibility = View.VISIBLE
                levelTV.text = "${position + 1}"

                // Show play icon if selected
                if (position == selectedPosition) {
                    image.setImageResource(R.drawable.ic_play_arrow)
                    image.visibility = View.VISIBLE
                    levelTV.visibility = View.GONE
                } else {
                    image.visibility = View.GONE
                }
            } else {
                // Level is locked
                image.setImageResource(R.drawable.lock)
                image.visibility = View.VISIBLE
                levelTV.visibility = View.GONE
            }
        }

        // Set the click listener on the entire item view
        holder.view.setOnClickListener {
            // Get the current position of the item in the adapter
            val currentPosition = holder.getAdapterPosition()

            // Check if the position is valid and the level is unlocked
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition <= unlocked) {
                val oldPosition = selectedPosition
                selectedPosition = currentPosition

                // Refresh the old and new items to update their views efficiently
                if (oldPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldPosition)
                }
                notifyItemChanged(selectedPosition)

                // Notify the listener that a level has been clicked
                listener.onLevelClicked(currentPosition + 1) // Pass the 1-based level number
            }
        }
    }

    override fun getItemCount(): Int {
        return size
    }
}