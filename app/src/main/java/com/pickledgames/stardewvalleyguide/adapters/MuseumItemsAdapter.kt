package com.pickledgames.stardewvalleyguide.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.fragments.MuseumItemFragment
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_museum_item.*
import kotlinx.android.synthetic.main.list_item_museum_item_collection_header.*

class MuseumItemsAdapter(
        private var list: List<Any>,
        private var farm: Farm,
        private var showCompleted: Boolean,
        private val mainActivity: MainActivity,
        private val onItemCheckedListener: OnItemCheckedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == HEADER_TYPE) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_museum_item_collection_header, parent, false)
            MuseumItemCollectionHeaderViewHolder(v, mainActivity)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_museum_item, parent, false)
            MuseumItemViewHolder(v, mainActivity, onItemCheckedListener)
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
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindMuseumItemCollection(museumItemCollection: MuseumItemCollection, completedItemsCount: Int) {
            museum_item_collection_header_image_view?.setImageResource(museumItemCollection.getImageId(mainActivity))
            museum_item_collection_header_image_view?.contentDescription = museumItemCollection.name
            museum_item_collection_header_name_text_view?.text = museumItemCollection.name
            val quantityCompleted = String.format(
                    mainActivity.getString(R.string.bundle_quantity_completed_template),
                    completedItemsCount,
                    museumItemCollection.numberOfItems
            )
            museum_item_collection_header_quantity_completed_text_view?.text = quantityCompleted
            if (completedItemsCount == museumItemCollection.numberOfItems) {
                val green = ContextCompat.getColor(mainActivity, R.color.green)
                museum_item_collection_header_quantity_completed_text_view?.setTextColor(green)
            } else {
                val white = ContextCompat.getColor(mainActivity, android.R.color.white)
                museum_item_collection_header_quantity_completed_text_view?.setTextColor(white)
            }
        }
    }

    class MuseumItemViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity,
            private val onItemCheckedListener: OnItemCheckedListener
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var isChecked: Boolean = false

        fun bindMuseumItem(museumItem: Any, isCompleted: Boolean, showCompleted: Boolean) {
            if (isCompleted && !showCompleted) {
                containerView.visibility = View.GONE
                containerView.layoutParams = RecyclerView.LayoutParams(0, 0)
                return
            } else {
                containerView.visibility = View.VISIBLE
                containerView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            (museumItem as MuseumItem).let {
                museum_item_image_view?.setImageResource(it.getImageId(mainActivity))
                museum_item_image_view?.contentDescription = it.name
                museum_item_text_view?.text = it.name
                isChecked = isCompleted
                museum_item_check_box?.isChecked = isChecked
                // use onClick instead of onCheckChanged to avoid initial firing
                museum_item_check_box?.setOnClickListener { _ ->
                    isChecked = !isChecked
                    onItemCheckedListener.onItemChecked(it, isChecked)
                }
            }
            containerView.setOnClickListener {
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
