package com.pickledgames.stardewvalleyguide.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.models.GiftReaction
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_gift_reaction.*

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

    class GiftReactionViewHolder(
            override val containerView: View,
            private val context: Context
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindGiftReaction(giftReaction: GiftReaction) {
            gift_reaction_image_view.setImageResource(giftReaction.getImageId(context))
            gift_reaction_image_view.contentDescription = giftReaction.itemName
            containerView.setOnClickListener {
                SimpleTooltip.Builder(context)
                        .anchorView(containerView)
                        .text(giftReaction.itemName)
                        .animated(true)
                        .transparentOverlay(false)
                        .build()
                        .show()
            }
        }
    }
}
