package com.o7solutions.braingames.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private val listener: OnLevelClickListener,
    private val context: Context
) : RecyclerView.Adapter<LevelsAdapter.ViewHolder>() {

    private var selectedPosition: Int = if (0 <= unlocked) unlocked else RecyclerView.NO_POSITION
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

                // Show play icon only for selected (latest unlocked by default)
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

        // Set the click listener
        holder.view.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition <= unlocked) {
                val oldPosition = selectedPosition
                selectedPosition = currentPosition

                if (oldPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldPosition)
                }
                notifyItemChanged(selectedPosition)

                listener.onLevelClicked(currentPosition + 1)
                Toast.makeText(context, "Level ${currentPosition + 1}  selected,click Start Game", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, "The selected Level is locked!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return size
    }
}