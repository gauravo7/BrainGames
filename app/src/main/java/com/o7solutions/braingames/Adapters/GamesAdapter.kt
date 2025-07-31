package com.o7solutions.braingames.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.R

class GamesAdapter(
    private val list: ArrayList<Games>,
    private val click: OnClick
) : RecyclerView.Adapter<GamesAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        var gameName = view.findViewById<TextView>(R.id.gameName)
        var gameCard = view.findViewById<CardView>(R.id.gameCard)
        var gameImage = view.findViewById<ImageView>(R.id.gameImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = list[position]

        holder.apply {
            // Set the game name
            gameName.text = game.name ?: "Unknown"

            // Load image using Glide (if url is provided)
            if (!game.url.isNullOrEmpty()) {
                Glide.with(gameImage)
                    .load(game.url)
                    .placeholder(R.drawable.logo)
                    .into(gameImage)
            } else {
                gameImage.setImageResource(R.drawable.logo) // fallback image
            }

            // Set background color if colorHex is valid
            try {
//                game.colorHex?.let {
//                    gameCard.setCardBackgroundColor(Color.parseColor(it))
//                    gameImage.setBackgroundColor(Color.parseColor(it))
//                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            gameImage.setOnClickListener {
                click.onGameClick(game)
            }
            // Set click listener
            view.setOnClickListener {
                click.onGameClick(game)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnClick {
        fun onGameClick(game: Games)
    }
}