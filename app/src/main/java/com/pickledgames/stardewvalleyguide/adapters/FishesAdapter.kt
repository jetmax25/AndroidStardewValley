package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.fragments.FishFragment
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.models.Fish
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_fish.*

class FishesAdapter(
        private var fishes: List<Fish>,
        private var farm: Farm,
        private var showCompleted: Boolean,
        private val mainActivity: MainActivity,
        private val onItemCheckedListener: OnItemCheckedListener
) : RecyclerView.Adapter<FishesAdapter.FishViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishesAdapter.FishViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_fish, parent, false)
        return FishViewHolder(v, mainActivity, onItemCheckedListener)
    }

    override fun getItemCount(): Int {
        return fishes.size
    }

    override fun onBindViewHolder(holder: FishesAdapter.FishViewHolder, position: Int) {
        val fish = fishes[position]
        val isCompleted = farm.fishes.contains(fish.name)
        holder.bindFish(fish, isCompleted, showCompleted)
    }

    fun updateFarm(f: Farm) {
        farm = f
        notifyDataSetChanged()
    }

    fun updateFishes(f: List<Fish>) {
        fishes = f
        notifyDataSetChanged()
    }

    fun updateShowCompleted(s: Boolean) {
        showCompleted = s
        notifyDataSetChanged()
    }

    class FishViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity,
            private val onItemCheckedListener: OnItemCheckedListener
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var isChecked: Boolean = false

        fun bindFish(fish: Fish, isCompleted: Boolean, showCompleted: Boolean) {
            if (isCompleted && !showCompleted) {
                containerView.visibility = View.GONE
                containerView.layoutParams = RecyclerView.LayoutParams(0, 0)
                return
            } else {
                containerView.visibility = View.VISIBLE
                containerView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            fish_image_view?.setImageResource(fish.getImageId(mainActivity))
            fish_image_view?.contentDescription = fish.name
            fish_text_view?.text = fish.name
            isChecked = isCompleted
            fish_check_box?.isChecked = isChecked
            // use onClick instead of onCheckChanged to avoid initial firing
            fish_check_box?.setOnClickListener { _ ->
                isChecked = !isChecked
                onItemCheckedListener.onItemChecked(fish, isChecked)
            }
            containerView.setOnClickListener {
                mainActivity.pushFragment(FishFragment.newInstance(fish))
            }
        }
    }
}
