package com.pickledgames.stardewvalleyguide.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.models.GiftReaction
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_gift_reaction.*
import kotlinx.android.synthetic.main.list_item_reaction_header.*


class GiftReactionsAdapter(
        private var list: List<Any>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == HEADER_TYPE) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_reaction_header, parent, false)
            ReactionHeaderViewHolder(v)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_gift_reaction, parent, false)
            GiftReactionViewHolder(v, parent.context)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is ReactionHeaderViewHolder && item is Reaction) {
            holder.bindReaction(item)
        } else if (holder is GiftReactionViewHolder && item is GiftReaction) {
            holder.bindGiftReaction(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is Reaction) HEADER_TYPE else ITEM_TYPE
    }

    fun updateList(l: List<Any>) {
        list = l
        notifyDataSetChanged()
    }

    companion object {
        private const val HEADER_TYPE = 0
        private const val ITEM_TYPE = 1
    }

    class ReactionHeaderViewHolder(
            override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val imageViews: List<ImageView> = listOf(
                reaction_header_1_image_view,
                reaction_header_2_image_view,
                reaction_header_3_image_view,
                reaction_header_4_image_view,
                reaction_header_5_image_view,
                reaction_header_6_image_view
        )

        fun bindReaction(reaction: Reaction) {
            reaction_type_text_view.text = reaction.toString()
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
                    pair = Pair(R.drawable.ic_heart_grey, Reaction.Dislike.type)
                    drawableIdContentDescriptionPairList = mutableListOf(null, null, pair, pair, null, null)
                }
                Reaction.Hate -> {
                    pair = Pair(R.drawable.ic_heart_grey, Reaction.Hate.type)
                    drawableIdContentDescriptionPairList = mutableListOf(null, pair, pair, pair, pair, null)
                }
            }

            for ((index: Int, p: Pair<Int, String>?) in drawableIdContentDescriptionPairList.withIndex()) {
                if (p != null) {
                    imageViews[index].setImageResource(p.first)
                    imageViews[index].contentDescription = p.second
                    imageViews[index].visibility = View.VISIBLE
                } else {
                    imageViews[index].visibility = View.INVISIBLE
                }
            }
        }
    }

    class GiftReactionViewHolder(
            override val containerView: View,
            private val context: Context
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindGiftReaction(giftReaction: GiftReaction) {
            gift_reaction_image_view.setImageResource(giftReaction.getImageId(context))
            containerView.setOnClickListener {
                SimpleTooltip.Builder(context)
                        .anchorView(containerView)
                        .text("${giftReaction.itemName} - ${giftReaction.reaction}")
                        .animated(true)
                        .transparentOverlay(false)
                        .build()
                        .show()
            }
        }
    }
}
