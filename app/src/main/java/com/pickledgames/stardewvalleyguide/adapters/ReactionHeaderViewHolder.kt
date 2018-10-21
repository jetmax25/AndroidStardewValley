package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.enums.Reaction
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_reaction_header.*

class ReactionHeaderViewHolder(
        override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val imageViews: List<ImageView?> = listOf(
            reaction_header_1_image_view,
            reaction_header_2_image_view,
            reaction_header_3_image_view,
            reaction_header_4_image_view,
            reaction_header_5_image_view,
            reaction_header_6_image_view
    )

    fun bindReaction(reaction: Reaction) {
        reaction_type_text_view?.text = reaction.toString()
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
