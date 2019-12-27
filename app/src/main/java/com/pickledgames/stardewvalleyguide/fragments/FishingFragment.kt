package com.pickledgames.stardewvalleyguide.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.FishesAdapter
import com.pickledgames.stardewvalleyguide.enums.FishingLocationType
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.models.Fish
import com.pickledgames.stardewvalleyguide.repositories.FarmRepository
import com.pickledgames.stardewvalleyguide.repositories.FishRepository
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.filter_fishing.*
import kotlinx.android.synthetic.main.fragment_fishing.*
import kotlinx.android.synthetic.main.header_farm.*
import kotlinx.android.synthetic.main.loading.*
import javax.inject.Inject

class FishingFragment : BaseFragment(), View.OnClickListener, OnItemCheckedListener, SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var farmRepository: FarmRepository
    @Inject lateinit var fishRepository: FishRepository
    @Inject lateinit var sharedPreferences: SharedPreferences
    private var farm: Farm? = null
    private var fishes: MutableList<Fish> = mutableListOf()
    private var adapter: FishesAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var searchTerm: String = ""
    private var seasonFilterBy: String = ""
    private var locationFilterBy: String = ""
    private var weatherFilterBy: String = ""
    private var startTime: Int = MIN_START_TIME
    private var endTime: Int = MAX_END_TIME
    private var showCompleted: Boolean = false
    private var hasAdapterBeenSetup: Boolean = false
    private var adapterPosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_fishing
        menuId = R.menu.fishing
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onClick(view: View?) {
        val direction = if (view?.id == R.id.header_farm_left_arrow_image_view) FarmRepository.LEFT else FarmRepository.RIGHT
        val disposable = farmRepository.toggleSelectedFarm(direction).subscribe()
        compositeDisposable.add(disposable)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.fishing_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, View.OnFocusChangeListener { _, b ->
            fishing_header_group?.visibility = if (b) View.GONE else View.VISIBLE
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fishing_edit_farms -> {
                (activity as MainActivity).pushFragment(EditFarmsFragment.newInstance())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        searchTerm = query ?: ""
        filter.filter("")
        return false
    }

    override fun onPause() {
        super.onPause()
        // Force refresh
        hasAdapterBeenSetup = false
        adapterPosition = linearLayoutManager?.findFirstVisibleItemPosition() ?: 0
    }

    override fun setup() {
        header_farm_left_arrow_image_view?.setOnClickListener(this)
        header_farm_right_arrow_image_view?.setOnClickListener(this)
        header_farm_easy_flip_view?.setOnClickListener {
            (activity as MainActivity).pushFragment(EditFarmsFragment.newInstance())
        }

        val seasonTabIndex = sharedPreferences.getInt(SEASON_INDEX, 0)
        filter_fishing_season_tab_layout?.getTabAt(seasonTabIndex)?.select()
        seasonFilterBy = filter_fishing_season_tab_layout?.getTabAt(seasonTabIndex)?.text.toString()
        filter_fishing_season_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                seasonFilterBy = tab?.text.toString()
                filter.filter("")
                sharedPreferences.edit().putInt(SEASON_INDEX, tab?.position ?: 0).apply()
            }
        })

        val locationTabIndex = sharedPreferences.getInt(LOCATION_INDEX, 0)
        filter_fishing_location_tab_layout?.getTabAt(locationTabIndex)?.select()
        locationFilterBy = filter_fishing_location_tab_layout?.getTabAt(locationTabIndex)?.text.toString()
        filter_fishing_location_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                locationFilterBy = tab?.text.toString()
                filter.filter("")
                sharedPreferences.edit().putInt(LOCATION_INDEX, tab?.position ?: 0).apply()
            }
        })

        val weatherIndex = sharedPreferences.getInt(WEATHER_INDEX, 0)
        filter_fishing_weather_tab_layout?.getTabAt(weatherIndex)?.select()
        weatherFilterBy = filter_fishing_weather_tab_layout?.getTabAt(weatherIndex)?.text.toString()
        filter_fishing_weather_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                weatherFilterBy = tab?.text.toString()
                filter.filter("")
                sharedPreferences.edit().putInt(WEATHER_INDEX, tab?.position ?: 0).apply()
            }
        })

        startTime = sharedPreferences.getInt(START_TIME, MIN_START_TIME)
        endTime = sharedPreferences.getInt(END_TIME, MAX_END_TIME)
        time_range_seek_bar?.setRange(MIN_START_TIME.toFloat(), MAX_END_TIME.toFloat(), 1f)
        time_range_seek_bar?.setValue(startTime.toFloat(), endTime.toFloat())
        FragmentUtil.setTimeRangeText(startTime, endTime, time_range_text_view, resources)

        time_range_seek_bar?.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}

            override fun onRangeChanged(rangeSeekBar: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                startTime = leftValue.toInt()
                endTime = rightValue.toInt()
                FragmentUtil.setTimeRangeText(startTime, endTime, time_range_text_view, resources)
                filter.filter("")
                sharedPreferences.edit().putInt(START_TIME, startTime).apply()
                sharedPreferences.edit().putInt(END_TIME, endTime).apply()
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
        })

        showCompleted = sharedPreferences.getBoolean(SHOW_COMPLETED, false)
        show_completed_check_box?.isChecked = showCompleted
        show_completed_check_box?.setOnCheckedChangeListener { _, b ->
            showCompleted = b
            adapter?.updateShowCompleted(showCompleted)
            sharedPreferences.edit().putBoolean(SHOW_COMPLETED, showCompleted).apply()
        }

        FragmentUtil.setupToggleFilterSettings(toggle_filter_settings_text_view, resources, filter_fishing_group, sharedPreferences, SHOW_FILTER_SETTINGS)

        data class Results(
                val farm: Farm?,
                val fishes: List<Fish>
        )

        loading_container?.visibility = View.VISIBLE
        fishing_header_group?.visibility = View.INVISIBLE
        fishing_recycler_view?.visibility = View.INVISIBLE

        val disposable = Single.zip(
                farmRepository.getSelectedFarm(),
                fishRepository.getFishes(),
                BiFunction { farm: Farm?, fishes: List<Fish> -> Results(farm, fishes) }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { results ->
                    loading_container?.visibility = View.INVISIBLE
                    fishing_header_group?.visibility = View.VISIBLE
                    fishing_recycler_view?.visibility = View.VISIBLE
                    farm = results.farm
                    farm?.let {
                        header_farm_name_front_text_view?.text = String.format(getString(R.string.farm_name_template, it.name))
                        header_farm_name_back_text_view?.text = String.format(getString(R.string.farm_name_template, it.name))
                    }
                    fishes.clear()
                    fishes.addAll(results.fishes.sortedBy { it.name })
                    filter.filter("")
                }

        compositeDisposable.add(disposable)

        val selectedFarmChangesDisposable = farmRepository.getSelectedFarmChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { f ->
                    FragmentUtil.flipSelectedFarmText(header_farm_easy_flip_view, header_farm_name_front_text_view, header_farm_name_back_text_view, resources, f)
                    farm = f
                    farm?.let {
                        adapter?.updateFarm(it)
                    }
                }

        compositeDisposable.add(selectedFarmChangesDisposable)
    }

    private fun setupAdapter(fishes: List<Fish>) {
        farm?.let {
            hasAdapterBeenSetup = true
            adapter = FishesAdapter(
                    fishes,
                    it,
                    showCompleted,
                    activity as MainActivity,
                    this
            )

            fishing_recycler_view?.adapter = adapter
            linearLayoutManager = LinearLayoutManager(activity)
            fishing_recycler_view?.layoutManager = linearLayoutManager
            fishing_recycler_view?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            linearLayoutManager?.scrollToPosition(adapterPosition)
        }
    }

    override fun onItemChecked(fish: Fish, isChecked: Boolean) {
        if (isChecked) farm?.fishes?.add(fish.name)
        else farm?.fishes?.remove(fish.name)
        adapter?.notifyDataSetChanged()
        farm?.let {
            farmRepository.updateSelectedFarm(it)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = fishes
                        .filter { fish ->
                            seasonFilterBy == "All" || (fish.availability.seasons.contains(Season.fromString(seasonFilterBy)) && fish.availability.seasons.size != Season.values().size)
                        }
                        .filter { fish ->
                            locationFilterBy == "All" || fish.availability.locations.containsValue(FishingLocationType.fromString(locationFilterBy))
                        }
                        .filter { fish ->
                            weatherFilterBy == "Any" || fish.availability.weather == "Any" || fish.availability.weather.equals(weatherFilterBy, true)
                        }
                        .filter { fish ->
                            val currentRange = startTime until endTime
                            val availableRange = fish.availability.timeRange
                            currentRange.intersect(availableRange).isNotEmpty()
                        }
                        .filter { fish -> fish.name.contains(searchTerm, true) }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                filterResults.count = filteredList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                val filteredList = filterResults?.values as List<Fish>

                if (hasAdapterBeenSetup) {
                    adapter?.updateFishes(filteredList)
                } else {
                    setupAdapter(filteredList)
                }
            }
        }
    }

    companion object {
        private val SEASON_INDEX = "${FishingFragment::class.java.simpleName}_SEASON_INDEX"
        private val LOCATION_INDEX = "${FishingFragment::class.java.simpleName}_LOCATION_INDEX"
        private val WEATHER_INDEX = "${FishingFragment::class.java.simpleName}_WEATHER_INDEX"
        private val START_TIME = "${FishingFragment::class.java.simpleName}_START_TIME"
        private val END_TIME = "${FishingFragment::class.java.simpleName}_END_TIME"
        private val SHOW_COMPLETED = "${FishingFragment::class.java.simpleName}_SHOW_COMPLETED"
        private val SHOW_FILTER_SETTINGS = "${FishingFragment::class.java.simpleName}_SHOW_FILTER_SETTINGS"
        private const val MIN_START_TIME = 6
        private const val MAX_END_TIME = 26

        fun newInstance(): FishingFragment {
            return FishingFragment()
        }
    }
}
