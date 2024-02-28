package com.pickledgames.stardewvalleyguide.adapters

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.databinding.ListItemReactionHeaderBinding
import com.pickledgames.stardewvalleyguide.enums.Reaction

class ReactionHeaderViewHolder(
    private val binding: ListItemReactionHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {

    private val imageViews: List<ImageView?> = listOf(
            binding.reactionHeader1ImageView,
            binding.reactionHeader2ImageView,
            binding.reactionHeader3ImageView,
            binding.reactionHeader4ImageView,
            binding.reactionHeader5ImageView,
            binding.reactionHeader6ImageView
    )

    fun bindReaction(reaction: Reaction) {
        binding.reactionTypeTextView.text = reaction.toString()
        setupReactionDrawables(reaction)
    }

    private fun setupReactionDrawables(reaction: Reaction) {
        var drawableIdContentDescriptionPairList: MutableList<Pair<Int, String>?> = mutableListOf()
        val pair: Pair<Int, String>

        when (reaction) {
            Reaction.Love -> {
                pair = Pair(R.drawable.ic_heart_red, Reaction.Love.type)
                drawableIdContentDescriptionPairList = mutableListOf(pair, pair, pair, pair, pair, pair)

            }
            Reaction.Like -> {
                pair = Pair(R.drawable.ic_heart_red, Reaction.Like.type)
                drawableIdContentDescriptionPairList = mutableListOf(null, pair, pair, pair, pair, null)
            }
            Reaction.Neutral -> {
                pair = Pair(R.drawable.ic_heart_red, Reaction.Neutral.type)
                drawableIdContentDescriptionPairList = mutableListOf(null, null, pair, pair, null, null)
            }
            Reaction.Dislike -> {
                pair = Pair(R.drawable.ic_heart_purple, Reaction.Dislike.type)
                drawableIdContentDescriptionPairList = mutableListOf(null, null, pair, pair, null, null)
            }
            Reaction.Hate -> {
                pair = Pair(R.drawable.ic_heart_purple, Reaction.Hate.type)
                drawableIdContentDescriptionPairList = mutableListOf(null, pair, pair, pair, pair, null)
            }
        }

        for ((index: Int, p: Pair<Int, String>?) in drawableIdContentDescriptionPairList.withIndex()) {
            if (p != null) {
                imageViews[index]?.setImageResource(p.first)
                imageViews[index]?.contentDescription = p.second
                imageViews[index]?.visibility = View.VISIBLE
            } else {
                imageViews[index]?.visibility = View.INVISIBLE
            }
        }
    }
}
