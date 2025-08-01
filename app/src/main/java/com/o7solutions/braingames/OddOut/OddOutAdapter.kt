package com.o7solutions.braingames.OddOut

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.o7solutions.braingames.R
import com.o7solutions.braingames.utils.AppFunctions

class OddOutAdapter(
    var list: List<Int>,
    var answerIndex: Int,
    var count: Int,
    var answerIndexImage: Int,
    var onClick: OnClick): RecyclerView.Adapter<OddOutAdapter.ViewHolder>() {

    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById<ImageView>(R.id.itemImage)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OddOutAdapter.ViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_odd_out,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: OddOutAdapter.ViewHolder, position: Int) {


        val images = listOf(
            R.drawable.rectangle, R.drawable.circle, R.drawable.star,R.drawable.cone
        )
        val otherImages = images.filter { it != images[answerIndexImage] }.toMutableList()
        Log.d("Other Images",otherImages.toString())

        holder.apply {
            if(list.contains(position) && answerIndex != position) {
                val randomImage = otherImages.random()
                image.setImageResource(randomImage)
            } else {
                holder.image.setImageDrawable(null)
            }

            if(answerIndex == position) {
                val drawable = ContextCompat.getDrawable(holder.itemView.context, images[answerIndexImage])
                image.setImageDrawable(drawable)
            }
            holder.image.setOnClickListener {
                onClick.onImageClick(position == answerIndex)
            }
        }
    }

    override fun getItemCount(): Int {
        return count
    }


    interface OnClick {
        fun onImageClick(isCorrect: Boolean)
    }
}