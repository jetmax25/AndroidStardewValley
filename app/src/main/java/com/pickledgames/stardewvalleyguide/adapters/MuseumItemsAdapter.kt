package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.ListItemMuseumItemBinding
import com.pickledgames.stardewvalleyguide.databinding.ListItemMuseumItemCollectionHeaderBinding
import com.pickledgames.stardewvalleyguide.fragments.MuseumItemFragment
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.*

class MuseumItemsAdapter(
        private var list: List<Any>,
        private var farm: Farm,
        private var showCompleted: Boolean,
        private val mainActivity: MainActivity,
        private val onItemCheckedListener: OnItemCheckedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
            val binding = ListItemMuseumItemCollectionHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MuseumItemCollectionHeaderViewHolder(binding, mainActivity)
        } else {
            val binding = ListItemMuseumItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MuseumItemViewHolder(binding, mainActivity, onItemCheckedListener)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is MuseumItemCollectionHeaderViewHolder && item is MuseumItemCollection) {
            val completedItemsCount = farm.getCompletedItemsCount(item)
            holder.bindMuseumItemCollection(item, completedItemsCount)
        } else if (holder is MuseumItemViewHolder && item is MuseumItem) {
            val isCompleted = farm.museumItems.contains(item.uniqueId)
            holder.bindMuseumItem(item, isCompleted, showCompleted)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is MuseumItemCollection) HEADER_TYPE else ITEM_TYPE
    }

    fun updateFarm(f: Farm) {
        farm = f
        notifyDataSetChanged()
    }

    fun updateList(l: List<Any>) {
        list = l
        notifyDataSetChanged()
    }

    fun updateShowCompleted(s: Boolean) {
        showCompleted = s
        notifyDataSetChanged()
    }

    companion object {
        private const val HEADER_TYPE = 0
        private const val ITEM_TYPE = 1
    }

    class MuseumItemCollectionHeaderViewHolder(
            private val binding: ListItemMuseumItemCollectionHeaderBinding,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindMuseumItemCollection(museumItemCollection: MuseumItemCollection, completedItemsCount: Int) {
            binding.museumItemCollectionHeaderImageView.setImageResource(museumItemCollection.getImageId(mainActivity))
            binding.museumItemCollectionHeaderImageView.contentDescription = museumItemCollection.name
            binding.museumItemCollectionHeaderNameTextView.text = museumItemCollection.name
            val quantityCompleted = String.format(
                    mainActivity.getString(R.string.bundle_quantity_completed_template),
                    completedItemsCount,
                    museumItemCollection.numberOfItems
            )
            binding.museumItemCollectionHeaderQuantityCompletedTextView.text = quantityCompleted
            if (completedItemsCount == museumItemCollection.numberOfItems) {
                val green = ContextCompat.getColor(mainActivity, R.color.green)
                binding.museumItemCollectionHeaderQuantityCompletedTextView.setTextColor(green)
            } else {
                val white = ContextCompat.getColor(mainActivity, android.R.color.white)
                binding.museumItemCollectionHeaderQuantityCompletedTextView.setTextColor(white)
            }
        }
    }

    class MuseumItemViewHolder(
            private val binding: ListItemMuseumItemBinding,
            private val mainActivity: MainActivity,
            private val onItemCheckedListener: OnItemCheckedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private var isChecked: Boolean = false

        fun bindMuseumItem(museumItem: Any, isCompleted: Boolean, showCompleted: Boolean) {
            if (isCompleted && !showCompleted) {
                binding.root.visibility = View.GONE
                binding.root.layoutParams = RecyclerView.LayoutParams(0, 0)
                return
            } else {
                binding.root.visibility = View.VISIBLE
                binding.root.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            (museumItem as MuseumItem).let {
                binding.museumItemImageView.setImageResource(it.getImageId(mainActivity))
                binding.museumItemImageView.contentDescription = it.name
                binding.museumItemTextView.text = it.name
                isChecked = isCompleted
                binding.museumItemCheckBox.isChecked = isChecked
                // use onClick instead of onCheckChanged to avoid initial firing
                binding.museumItemCheckBox.setOnClickListener { _ ->
                    isChecked = !isChecked
                    onItemCheckedListener.onItemChecked(it, isChecked)
                }
            }
            binding.root.setOnClickListener {
                val museumItemFragment = when (museumItem) {
                    is Artifact -> MuseumItemFragment.newInstance(museumItem)
                    is LostBook -> MuseumItemFragment.newInstance(museumItem)
                    is Mineral -> MuseumItemFragment.newInstance(museumItem)
                    else -> throw Exception("Invalid MuseumItem Type")
                }
                mainActivity.pushFragment(museumItemFragment)
            }
        }
    }
}
