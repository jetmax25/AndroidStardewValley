package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Filter
import android.widget.Filterable
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
import kotlinx.android.synthetic.main.fragment_community_center.*
import kotlinx.android.synthetic.main.fragment_fishing.*
import kotlinx.android.synthetic.main.header_farm.*
import kotlinx.android.synthetic.main.loading.*
import javax.inject.Inject

class FishingFragment : BaseFragment(), View.OnClickListener, OnItemCheckedListener, SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var farmRepository: FarmRepository
    @Inject lateinit var fishRepository: FishRepository
    private lateinit var farm: Farm
    private var fishes: MutableList<Fish> = mutableListOf()
    private lateinit var adapter: FishesAdapter
    private var searchTerm: String = ""
    private var seasonFilterBy: String = "All"
    private var locationFilterBy: String = "All"
    private var weatherFilterBy: String = "Any"
    private var startTime: Int = 6
    private var endTime: Int = 26
    private var showCompleted: Boolean = false
    private var hasAdapterBeenSetup: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_fishing
        menuId = R.menu.fishing
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onClick(view: View?) {
        val direction = if (view?.id == R.id.header_farm_left_arrow_image_view) FarmRepository.LEFT else FarmRepository.RIGHT
        val disposable = farmRepository.toggleSelectedFarm(direction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { f ->
                    farm = f
                    header_farm_easy_flip_view?.isFrontSide?.let {
                        if (it) {
                            header_farm_name_back_text_view?.text = String.format(getString(R.string.farm_name_template, farm.name))
                        } else {
                            header_farm_name_front_text_view?.text = String.format(getString(R.string.farm_name_template, farm.name))
                        }
                        // Flip after setting text
                        header_farm_easy_flip_view?.flipTheView()
                    }
                    adapter.updateFarm(farm)
                }

        compositeDisposable.add(disposable)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu?.findItem(R.id.fishing_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, View.OnFocusChangeListener { _, b ->
            fishing_header_group?.visibility = if (b) View.GONE else View.VISIBLE
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.community_center_edit_farms -> {
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
    }

    override fun setup() {
        header_farm_left_arrow_image_view?.setOnClickListener(this)
        header_farm_right_arrow_image_view?.setOnClickListener(this)
        header_farm_easy_flip_view?.setOnClickListener {
            (activity as MainActivity).pushFragment(EditFarmsFragment.newInstance())
        }

        filter_fishing_season_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                seasonFilterBy = tab?.text.toString()
                filter.filter("")
            }
        })

        filter_fishing_location_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                locationFilterBy = tab?.text.toString()
                filter.filter("")
            }
        })

        filter_fishing_weather_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                weatherFilterBy = tab?.text.toString()
                filter.filter("")
            }
        })

        time_range_seek_bar.setRange(startTime.toFloat(), endTime.toFloat(), 1f)
        time_range_seek_bar.setValue(startTime.toFloat(), endTime.toFloat())
        setTimeRangeText()

        time_range_seek_bar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}

            override fun onRangeChanged(rangeSeekBar: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                startTime = leftValue.toInt()
                endTime = rightValue.toInt()
                setTimeRangeText()
                filter.filter("")
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
        })

        show_completed_check_box?.setOnCheckedChangeListener { _, b ->
            showCompleted = b
            adapter.updateShowCompleted(showCompleted)
        }

        data class Results(
                val farm: Farm,
                val fishes: List<Fish>
        )

        val disposable = Single.zip(
                farmRepository.getSelectedFarm(),
                fishRepository.getFishes(),
                BiFunction { farm: Farm, fishes: List<Fish> -> Results(farm, fishes) }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { results ->
                    loading_container?.visibility = View.INVISIBLE
                    community_center_header_group?.visibility = View.VISIBLE
                    community_center_items_recycler_view?.visibility = View.VISIBLE
                    farm = results.farm
                    header_farm_name_front_text_view?.text = String.format(getString(R.string.farm_name_template, farm.name))
                    header_farm_name_back_text_view?.text = String.format(getString(R.string.farm_name_template, farm.name))
                    fishes.clear()
                    fishes.addAll(results.fishes.sortedBy { it.name })
                    filter.filter("")
                }

        compositeDisposable.add(disposable)

        FragmentUtil.setupToggleFilterSettings(toggle_filter_settings_text_view, resources, filter_fishing_group)
    }

    private fun getFormattedTimeString(time: Int): String {
        return when (time) {
            in 6 until 12 -> "$time AM"
            12 -> "12 PM"
            in 13 until 24 -> "${time % 12} PM"
            24 -> "12 AM"
            else -> "${time % 24} AM"
        }
    }

    private fun setTimeRangeText() {
        val formattedStartTime = getFormattedTimeString(startTime)
        val formattedEndTime = getFormattedTimeString(endTime)
        time_range_text_view.text = String.format(getString(R.string.time_range_template), formattedStartTime, formattedEndTime)
    }

    private fun setupFishesAdapter(fishes: List<Fish>) {
        hasAdapterBeenSetup = true
        adapter = FishesAdapter(
                fishes,
                farm,
                showCompleted,
                activity as MainActivity,
                this
        )

        fishing_recycler_view?.adapter = adapter
        fishing_recycler_view?.layoutManager = LinearLayoutManager(activity)
    }

    override fun onItemChecked(fish: Fish, isChecked: Boolean) {
        if (isChecked) farm.fishes.add(fish.name)
        else farm.fishes.remove(fish.name)
        adapter.notifyDataSetChanged()
        farmRepository.updateSelectedFarm(farm)
                .subscribeOn(Schedulers.io())
                .subscribe()
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
                    adapter.updateFishes(filteredList)
                } else {
                    setupFishesAdapter(filteredList)
                }
            }
        }
    }

    companion object {
        fun newInstance(): FishingFragment {
            return FishingFragment()
        }
    }
}
