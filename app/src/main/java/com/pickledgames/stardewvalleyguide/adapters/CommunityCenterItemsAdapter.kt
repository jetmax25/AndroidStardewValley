package com.pickledgames.stardewvalleyguide.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.fragments.CommunityCenterItemFragment
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.CommunityCenterBundle
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import com.pickledgames.stardewvalleyguide.models.Farm
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_community_center_bundle_header.*
import kotlinx.android.synthetic.main.list_item_community_center_item.*

class CommunityCenterItemsAdapter(
        private var list: List<Any>,
        private var farm: Farm,
        private var showCompleted: Boolean,
        private val mainActivity: MainActivity,
        private val onItemCheckedListener: OnItemCheckedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == HEADER_TYPE) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_community_center_bundle_header, parent, false)
            CommunityCenterBundleHeaderViewHolder(v, mainActivity)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_community_center_item, parent, false)
            CommunityCenterItemViewHolder(v, mainActivity, onItemCheckedListener)
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
            val isCompleted = farm.communityCenterItems.contains(item.name)
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
    }

    companion object {
        private const val HEADER_TYPE = 0
        private const val ITEM_TYPE = 1
    }

    class CommunityCenterBundleHeaderViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindCommunityCenterBundle(communityCenterBundle: CommunityCenterBundle, completedItemsCount: Int) {
            community_center_bundle_image_view?.setImageResource(communityCenterBundle.getImageId(mainActivity))
            community_center_bundle_image_view?.contentDescription = communityCenterBundle.name
            community_center_bundle_name_text_view?.text = communityCenterBundle.name
            val quantityCompleted = String.format(
                    mainActivity.getString(R.string.bundle_quantity_completed_template),
                    completedItemsCount,
                    communityCenterBundle.needed
            )
            community_center_bundle_quantity_completed_text_view?.text = quantityCompleted
            if (completedItemsCount == communityCenterBundle.needed) {
                val green = ContextCompat.getColor(mainActivity, R.color.green)
                community_center_bundle_quantity_completed_text_view?.setTextColor(green)
            } else {
                val white = ContextCompat.getColor(mainActivity, android.R.color.white)
                community_center_bundle_quantity_completed_text_view?.setTextColor(white)
            }
        }
    }

    class CommunityCenterItemViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity,
            private val onItemCheckedListener: OnItemCheckedListener
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var isChecked: Boolean = false

        fun bindCommunityCenterItem(communityCenterItem: CommunityCenterItem, isCompleted: Boolean, showCompleted: Boolean) {
            if (isCompleted && !showCompleted) {
                containerView.visibility = View.GONE
                containerView.layoutParams = RecyclerView.LayoutParams(0, 0)
                return
            } else {
                containerView.visibility = View.VISIBLE
                containerView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            community_center_item_image_view?.setImageResource(communityCenterItem.getImageId(mainActivity))
            community_center_item_image_view?.contentDescription = communityCenterItem.name
            community_center_item_text_view?.text = communityCenterItem.name
            isChecked = isCompleted
            community_center_item_check_box?.isChecked = isChecked
            // use onClick instead of onCheckChanged to avoid initial firing
            community_center_item_check_box?.setOnClickListener { _ ->
                isChecked = !isChecked
                onItemCheckedListener.onItemChecked(communityCenterItem, isChecked)
            }
            containerView.setOnClickListener {
                mainActivity.pushFragment(CommunityCenterItemFragment.newInstance(communityCenterItem))
            }
        }
    }
}
