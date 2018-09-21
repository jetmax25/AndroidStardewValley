package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.models.CommunityCenterBundle
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_community_center_bundle_header.*
import kotlinx.android.synthetic.main.list_item_community_center_item.*

class CommunityCenterItemsAdapter(
        private val list: MutableList<Any>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == HEADER_TYPE) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_community_center_bundle_header, parent, false)
            CommunityCenterBundleHeaderViewHolder(v, mainActivity)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_community_center_item, parent, false)
            CommunityCenterItemViewHolder(v, mainActivity)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is CommunityCenterBundleHeaderViewHolder && item is CommunityCenterBundle) {
            holder.bindCommunityCenterBundle(item)
        } else if (holder is CommunityCenterItemViewHolder && item is CommunityCenterItem) {
            holder.bindCommunityCenterItem(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is CommunityCenterBundle) HEADER_TYPE else ITEM_TYPE
    }

    companion object {
        private const val HEADER_TYPE = 0
        private const val ITEM_TYPE = 1
    }

    class CommunityCenterBundleHeaderViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindCommunityCenterBundle(communityCenterBundle: CommunityCenterBundle) {
            community_center_bundle_image_view.setImageResource(communityCenterBundle.getImageId(mainActivity))
            community_center_bundle_image_view.contentDescription = communityCenterBundle.name
            community_center_bundle_name_text_view.text = communityCenterBundle.name
            val quantityCompleted = String.format(
                    mainActivity.getString(R.string.bundle_quantity_completed_template),
                    communityCenterBundle.completed,
                    communityCenterBundle.items.size
            )
            community_center_bundle_quantity_completed_text_view.text = quantityCompleted
        }
    }

    class CommunityCenterItemViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindCommunityCenterItem(communityCenterItem: CommunityCenterItem) {
            community_center_item_image_view.setImageResource(communityCenterItem.getImageId(mainActivity))
            community_center_item_image_view.contentDescription = communityCenterItem.name
            community_center_item_text_view.text = communityCenterItem.name
            community_center_item_check_box.setOnCheckedChangeListener { _, isChecked ->
                mainActivity.onItemChecked(communityCenterItem, isChecked)
            }
        }
    }
}
