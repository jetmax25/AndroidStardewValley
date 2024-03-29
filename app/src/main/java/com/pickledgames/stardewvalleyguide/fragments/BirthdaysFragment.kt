package com.pickledgames.stardewvalleyguide.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.CalendarDaysAdapter
import com.pickledgames.stardewvalleyguide.adapters.VillagerBirthdaysAdapter
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.models.CalendarDay
import com.pickledgames.stardewvalleyguide.models.Villager
import com.pickledgames.stardewvalleyguide.repositories.VillagerRepository
import com.pickledgames.stardewvalleyguide.views.GridDividerDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_birthdays.*
import kotlinx.android.synthetic.main.header_calendar.*
import javax.inject.Inject

class BirthdaysFragment : BaseFragment(), View.OnClickListener {

    @Inject lateinit var villagerRepository: VillagerRepository
    @Inject lateinit var sharedPreferences: SharedPreferences
    private var villagers: MutableList<Villager> = mutableListOf()
    private val list: MutableList<Any> = mutableListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var initializedList: Boolean = false
    private var seasons: Array<Season> = Season.values()
    private var seasonIndex: Int = 0
    private lateinit var calendarDaysAdapter: CalendarDaysAdapter
    private lateinit var villagerBirthdaysAdapter: VillagerBirthdaysAdapter

    init {
        for (i in 1..28) {
            list.add(CalendarDay(i))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_birthdays
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onClick(v: View?) {
        seasonIndex = if (v == header_calendar_left_arrow_image_view) {
            if (seasonIndex == 0) seasons.size - 1 else seasonIndex - 1
        } else {
            if (seasonIndex == seasons.size - 1) 0 else seasonIndex + 1
        }

        sharedPreferences.edit().putInt(SEASON_INDEX, seasonIndex).apply()
        val season = seasons[seasonIndex]
        header_calendar_season_text_view?.text = season.type
        header_calendar_season_text_view?.setCompoundDrawablesWithIntrinsicBounds(
                season.getImageId(activity as MainActivity),
                0, 0, 0
        )

        for (i in 7..34) {
            (list[i] as CalendarDay).villager = null
        }

        villagers.filter { it.birthday.season == season }
                .forEach {
                    (list[6 + it.birthday.day] as CalendarDay).villager = it
                }

        calendarDaysAdapter.updateList(list)

        val filteredVillagers = villagers.filter { it.birthday.season == season }.sortedBy { it.birthday.day }
        villagerBirthdaysAdapter.updateVillagers(filteredVillagers)
    }

    override fun setup() {
        seasonIndex = sharedPreferences.getInt(SEASON_INDEX, 0)
        val season = seasons[seasonIndex]
        header_calendar_season_text_view?.text = season.type
        header_calendar_season_text_view?.setCompoundDrawablesWithIntrinsicBounds(
                season.getImageId(activity as MainActivity),
                0, 0, 0
        )

        if (initializedList) {
            header_calendar_left_arrow_image_view?.setOnClickListener(this)
            header_calendar_right_arrow_image_view?.setOnClickListener(this)
            setupCalendarDaysAdapter()
            setupVillagerBirthdaysAdapter()
        } else {
            val disposable = villagerRepository.getVillagers()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { v ->
                        initializedList = true
                        villagers.addAll(v)
                        villagers.filter { it.birthday.season == season }
                                .forEach {
                                    (list[6 + it.birthday.day] as CalendarDay).villager = it
                                }

                        header_calendar_left_arrow_image_view?.setOnClickListener(this)
                        header_calendar_right_arrow_image_view?.setOnClickListener(this)
                        setupCalendarDaysAdapter()
                        setupVillagerBirthdaysAdapter()
                    }

            compositeDisposable.add(disposable)
        }
    }

    private fun setupCalendarDaysAdapter() {
        calendarDaysAdapter = CalendarDaysAdapter(list, activity as MainActivity)
        calendar_days_recycler_view?.adapter = calendarDaysAdapter
        calendar_days_recycler_view?.layoutManager = GridLayoutManager(activity, 7)
        calendar_days_recycler_view?.addItemDecoration(GridDividerDecoration(5, 7))
    }

    private fun setupVillagerBirthdaysAdapter() {
        val season = seasons[seasonIndex]
        val filteredVillagers = villagers.filter { it.birthday.season == season }.sortedBy { it.birthday.day }
        villagerBirthdaysAdapter = VillagerBirthdaysAdapter(filteredVillagers, activity as MainActivity)
        villager_birthdays_recycler_view?.adapter = villagerBirthdaysAdapter
        villager_birthdays_recycler_view?.layoutManager = LinearLayoutManager(activity)
        villager_birthdays_recycler_view?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    companion object {
        private val SEASON_INDEX = "${BirthdaysFragment::class.java.simpleName}_SEASON_INDEX"

        fun newInstance(): BirthdaysFragment {
            return BirthdaysFragment()
        }
    }
}
