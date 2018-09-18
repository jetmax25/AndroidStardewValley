package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.fragments.VillagerFragment
import com.pickledgames.stardewvalleyguide.models.CalendarDay
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_calendar_day.*
import kotlinx.android.synthetic.main.list_item_calendar_day_header.*

class CalendarDaysAdapter(
        private var list: List<Any>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == HEADER_TYPE) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_calendar_day_header, parent, false)
            CalendarDayHeaderViewHolder(v)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_calendar_day, parent, false)
            CalendarDayViewHolder(v, mainActivity)
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

    class CalendarDayHeaderViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindDay(day: String) {
            calendar_day_header_text_view.text = day
        }
    }

    class CalendarDayViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindCalendarDay(calendarDay: CalendarDay) {
            calendar_day_text_view.text = calendarDay.day.toString()
            val villager = calendarDay.villager
            if (villager != null) {
                calendar_day_villager_image_view.visibility = View.VISIBLE
                calendar_day_villager_image_view.setImageResource(villager.getImageId(mainActivity))
                containerView.setOnClickListener {
                    mainActivity.pushFragment(VillagerFragment.newInstance(villager))
                }
            } else {
                calendar_day_villager_image_view.visibility = View.INVISIBLE
                containerView.setOnClickListener { }
            }
        }
    }
}
