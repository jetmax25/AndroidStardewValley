package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.ListItemCalendarDayBinding
import com.pickledgames.stardewvalleyguide.databinding.ListItemCalendarDayHeaderBinding
import com.pickledgames.stardewvalleyguide.fragments.VillagerFragment
import com.pickledgames.stardewvalleyguide.models.CalendarDay

class CalendarDaysAdapter(
    private var list: List<Any>,
    private val mainActivity: MainActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
            val binding =
                ListItemCalendarDayHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            CalendarDayHeaderViewHolder(binding)
        } else {
            val binding = ListItemCalendarDayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CalendarDayViewHolder(binding, mainActivity)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is CalendarDayHeaderViewHolder && item is String) {
            holder.bindDay(item)
        } else if (holder is CalendarDayViewHolder && item is CalendarDay) {
            holder.bindCalendarDay(item)
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

    class CalendarDayHeaderViewHolder(private val binding: ListItemCalendarDayHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindDay(day: String) {
            binding.calendarDayHeaderTextView.text = day
        }
    }

    class CalendarDayViewHolder(
        private val binding: ListItemCalendarDayBinding,
        private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindCalendarDay(calendarDay: CalendarDay) {
            binding.calendarDayTextView.text = calendarDay.day.toString()
            val villager = calendarDay.villager
            if (villager != null) {
                binding.calendarDayVillagerImageView.visibility = View.VISIBLE
                binding.calendarDayVillagerImageView.setImageResource(villager.getImageId(mainActivity))
                binding.root.setOnClickListener {
                    mainActivity.pushFragment(VillagerFragment.newInstance(villager))
                }
            } else {
                binding.calendarDayVillagerImageView.visibility = View.INVISIBLE
                binding.root.setOnClickListener { }
            }
        }
    }
}
