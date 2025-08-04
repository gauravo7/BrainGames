package com.o7solutions.braingames.OddOut

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.o7solutions.braingames.R

class OddOutAdapter(
    var list: List<Int>,
    var answerIndex: Int,
    var count: Int,
    var answerIndexImage: Int,
    var onClick: OnClick): RecyclerView.Adapter<OddOutAdapter.ViewHolder>() {

//    var imageZero = false
//    var imageOne = false
//    var imageTwo = false
//    var imageThree = false
//    var imageFour = false

    val images = listOf(
        R.drawable.rectangle, R.drawable.circle, R.drawable.star,R.drawable.cone,
        R.drawable.pentagon,R.drawable.heptagon
    )

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
        val otherImages = images.filter { it != images[answerIndexImage] }.toMutableList()
        Log.d("Other Images", otherImages.toString())

        // Get the list of indices excluding the answer index
        val nonAnswerIndices = list.filter { it != answerIndex }

        // Map non-answer indices to images (2 positions per image)
        val positionToImage = mutableMapOf<Int, Int>()
        var imageIndex = 0
        for (i in nonAnswerIndices.indices) {
            positionToImage[nonAnswerIndices[i]] = otherImages[imageIndex]
            if ((i + 1) % 2 == 0 && imageIndex < otherImages.size - 1) {
                imageIndex++
            }
        }

        holder.apply {
            when (position) {
                answerIndex -> {
                    // Set correct answer image
                    image.setImageResource(images[answerIndexImage])
                }
                in positionToImage -> {
                    // Set the mapped other image
                    image.setImageResource(positionToImage[position]!!)
                }
                else -> {
                    image.setImageDrawable(null)
                }
            }

            image.setOnClickListener {

                if(list.contains(position)) {


                    if (position == answerIndex) {
                        image.setImageResource(R.drawable.right_tick)
                    } else {
                        image.setImageResource(R.drawable.red_cross)
                    }
                    image.postDelayed({
                        onClick.onImageClick(position == answerIndex)
                    }, 500)
                } else {
//                    perform nothing
                }
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