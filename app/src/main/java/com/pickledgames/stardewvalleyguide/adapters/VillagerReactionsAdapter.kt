package com.pickledgames.stardewvalleyguide.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.databinding.ListItemReactionHeaderBinding
import com.pickledgames.stardewvalleyguide.databinding.ListItemVillagerReactionBinding
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.models.GiftReaction
import com.pickledgames.stardewvalleyguide.models.Villager
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip

class VillagerReactionsAdapter(
    private var list: List<Any>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == HEADER_TYPE) {
            val binding = ListItemReactionHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ReactionHeaderViewHolder(binding)
        } else {
            val binding = ListItemVillagerReactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            VillagerReactionViewHolder(binding, parent.context)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is ReactionHeaderViewHolder && item is Reaction) {
            holder.bindReaction(item)
        } else if (holder is VillagerReactionViewHolder && item is GiftReaction) {
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

    class VillagerReactionViewHolder(
        private val binding: ListItemVillagerReactionBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGiftReaction(giftReaction: GiftReaction) {
            val imageId = Villager(giftReaction.villagerName).getImageId(context)
            binding.villagerReactionImageView.setImageResource(imageId)
            binding.villagerReactionImageView.contentDescription = giftReaction.villagerName
            binding.root.setOnClickListener {
                SimpleTooltip.Builder(context)
                    .anchorView(binding.root)
                    .text("${giftReaction.villagerName} - ${giftReaction.reaction}")
                    .animated(true)
                    .transparentOverlay(false)
                    .build()
                    .show()
            }
        }
    }
}
