package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.ListItemCategoryHeaderBinding
import com.pickledgames.stardewvalleyguide.databinding.ListItemGiftBinding
import com.pickledgames.stardewvalleyguide.fragments.GiftFragment
import com.pickledgames.stardewvalleyguide.models.Gift
import kotlinx.android.extensions.LayoutContainer

class GiftsAdapter(
        private var list: List<Any>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
            val binding = ListItemCategoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CategoryViewHolder(binding)
        } else {
            val binding = ListItemGiftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GiftViewHolder(binding, mainActivity)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is CategoryViewHolder && item is String) {
            holder.bindCategory(item)
        } else if (holder is GiftViewHolder && item is Gift) {
            holder.bindGift(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is String) HEADER_TYPE else ITEM_TYPE
    }

    fun updateList(l: List<Any>) {
        list = l
        notifyDataSetChanged()
    }

    companion object {
        private const val HEADER_TYPE = 0
        private const val ITEM_TYPE = 1
    }

    class CategoryViewHolder(private val binding: ListItemCategoryHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindCategory(category: String) {
            binding.categoryTextView.text = category
        }
    }

    class GiftViewHolder(
            private val binding: ListItemGiftBinding,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGift(gift: Gift) {
            binding.giftImageView.setImageResource(gift.getImageId(mainActivity))
            binding.giftImageView.contentDescription = gift.name
            binding.root.setOnClickListener {
                mainActivity.pushFragment(GiftFragment.newInstance(gift))
            }
        }
    }
}
