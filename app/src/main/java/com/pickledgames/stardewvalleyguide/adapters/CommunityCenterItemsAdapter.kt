package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.ListItemCommunityCenterBundleHeaderBinding
import com.pickledgames.stardewvalleyguide.databinding.ListItemCommunityCenterItemBinding
import com.pickledgames.stardewvalleyguide.fragments.CommunityCenterItemFragment
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.CommunityCenterBundle
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import com.pickledgames.stardewvalleyguide.models.Farm

class CommunityCenterItemsAdapter(
        private var list: List<Any>,
        private var farm: Farm,
        private var showCompleted: Boolean,
        private val mainActivity: MainActivity,
        private val onItemCheckedListener: OnItemCheckedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
            val binding = ListItemCommunityCenterBundleHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CommunityCenterBundleHeaderViewHolder(binding, mainActivity)
        } else {
            val binding = ListItemCommunityCenterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CommunityCenterItemViewHolder(binding, mainActivity, onItemCheckedListener)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is CommunityCenterBundleHeaderViewHolder && item is CommunityCenterBundle) {
            val completedItemsCount = farm.getCompletedItemsCount(item)
            holder.bindCommunityCenterBundle(item, completedItemsCount)
        } else if (holder is CommunityCenterItemViewHolder && item is CommunityCenterItem) {
            val isCompleted = farm.communityCenterItems.contains(item.uniqueId)
            holder.bindCommunityCenterItem(item, isCompleted, showCompleted)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is CommunityCenterBundle) HEADER_TYPE else ITEM_TYPE
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

    class CommunityCenterBundleHeaderViewHolder(
        private val binding: ListItemCommunityCenterBundleHeaderBinding,
        private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindCommunityCenterBundle(communityCenterBundle: CommunityCenterBundle, completedItemsCount: Int) {
            binding.communityCenterBundleImageView.setImageResource(communityCenterBundle.getImageId(mainActivity))
            binding.communityCenterBundleImageView.contentDescription = communityCenterBundle.name
            binding.communityCenterBundleNameTextView.text = communityCenterBundle.name
            val quantityCompleted = String.format(
                    mainActivity.getString(R.string.bundle_quantity_completed_template),
                    completedItemsCount,
                    communityCenterBundle.needed
            )
            binding.communityCenterBundleQuantityCompletedTextView.text = quantityCompleted
            if (completedItemsCount == communityCenterBundle.needed) {
                val green = ContextCompat.getColor(mainActivity, R.color.green)
                binding.communityCenterBundleQuantityCompletedTextView.setTextColor(green)
            } else {
                val white = ContextCompat.getColor(mainActivity, android.R.color.white)
                binding.communityCenterBundleQuantityCompletedTextView.setTextColor(white)
            }
        }
    }

    class CommunityCenterItemViewHolder(
            private val binding: ListItemCommunityCenterItemBinding,
            private val mainActivity: MainActivity,
            private val onItemCheckedListener: OnItemCheckedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private var isChecked: Boolean = false

        fun bindCommunityCenterItem(communityCenterItem: CommunityCenterItem, isCompleted: Boolean, showCompleted: Boolean) {
            if (isCompleted && !showCompleted) {
                binding.root.visibility = View.GONE
                binding.root.layoutParams = RecyclerView.LayoutParams(0, 0)
                return
            } else {
                binding.root.visibility = View.VISIBLE
                binding.root.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            binding.communityCenterItemImageView.setImageResource(communityCenterItem.getImageId(mainActivity))
            binding.communityCenterItemImageView.contentDescription = communityCenterItem.name
            binding.communityCenterItemTextView.text = communityCenterItem.name
            isChecked = isCompleted
            binding.communityCenterItemCheckBox.isChecked = isChecked
            // use onClick instead of onCheckChanged to avoid initial firing
            binding.communityCenterItemCheckBox.setOnClickListener { _ ->
                isChecked = !isChecked
                onItemCheckedListener.onItemChecked(communityCenterItem, isChecked)
            }
            binding.root.setOnClickListener {
                mainActivity.pushFragment(CommunityCenterItemFragment.newInstance(communityCenterItem))
            }
        }
    }
}
