package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.fragments.GiftFragment
import com.pickledgames.stardewvalleyguide.models.Gift
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_category_header.*
import kotlinx.android.synthetic.main.list_item_gift.*

class GiftsAdapter(
        private var list: List<Any>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == HEADER_TYPE) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_category_header, parent, false)
            CategoryViewHolder(v)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_gift, parent, false)
            GiftViewHolder(v, mainActivity)
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

    class CategoryViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindCategory(category: String) {
            category_text_view.text = category
        }
    }

    class GiftViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindGift(gift: Gift) {
            gift_image_view.setImageResource(gift.getImageId(mainActivity))
            gift_image_view.contentDescription = gift.name
            containerView.setOnClickListener {
                mainActivity.pushFragment(GiftFragment.newInstance(gift))
            }
        }
    }
}
