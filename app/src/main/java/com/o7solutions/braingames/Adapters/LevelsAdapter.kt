package com.o7solutions.braingames.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.o7solutions.braingames.R

class LevelsAdapter(var list: ArrayList<String>): RecyclerView.Adapter<LevelsAdapter.ViewHolder>() {

    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        var image = view.findViewById<ImageView>(R.id.level_image)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LevelsAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_levels,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelsAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }
}