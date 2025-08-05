package com.o7solutions.braingames.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.o7solutions.braingames.R

class LevelsAdapter(var list: ArrayList<String>,var size: Int,var unlocked: Int): RecyclerView.Adapter<LevelsAdapter.ViewHolder>() {

    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        var image = view.findViewById<ImageView>(R.id.level_image)
        var levelTV = view.findViewById<TextView>(R.id.level_TV)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LevelsAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_levels,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelsAdapter.ViewHolder, position: Int) {

        holder.apply {

            if(position <= unlocked) {
                image.visibility = View.GONE
                levelTV.text = "${position+1}"
            } else {
                image.visibility = View.VISIBLE
                levelTV.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int {
        return size
    }
}