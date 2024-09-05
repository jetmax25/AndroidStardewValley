package com.pickledgames.stardewvalleyguide.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.CropsAdapter
import com.pickledgames.stardewvalleyguide.databinding.FragmentCropsBinding
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.enums.SortOrder
import com.pickledgames.stardewvalleyguide.models.Crop
import com.pickledgames.stardewvalleyguide.repositories.CropRepository
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CropsFragment : BaseFragment(), SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var cropRepository: CropRepository
    @Inject lateinit var sharedPreferences: SharedPreferences
    private var crops: MutableList<Crop> = mutableListOf()
    private var adapter: CropsAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var hasAdapterBeenSetup: Boolean = false
    private var adapterPosition: Int = 0
    private var seasonFilterBy: String = ""
    private var sortBy: String = ""
    private var sortOrder: String = ""
    private var searchTerm: String = ""
    private lateinit var binding: FragmentCropsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_crops
        menuId = R.menu.crops
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCropsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.crops_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, View.OnFocusChangeListener { _, b ->
            binding.filterCropsLayout.root.visibility = if (b) View.GONE else View.VISIBLE
        })
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
        val seasonTabIndex = sharedPreferences.getInt(SEASON_INDEX, 0)

        with (binding.filterCropsLayout) {
            filterCropsSeasonTabLayout.getTabAt(seasonTabIndex)?.select()
            seasonFilterBy = filterCropsSeasonTabLayout.getTabAt(seasonTabIndex)?.text.toString()
            filterCropsSeasonTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    seasonFilterBy = tab?.text.toString()
                    filter.filter("")
                    sharedPreferences.edit().putInt(SEASON_INDEX, tab?.position ?: 0).apply()
                }
            })
            val sortByTabIndex = sharedPreferences.getInt(SORT_BY_INDEX, 0)
            filterCropsSortByTabLayout.getTabAt(sortByTabIndex)?.select()
            sortBy = filterCropsSortByTabLayout.getTabAt(sortByTabIndex)?.text.toString()
            filterCropsSortByTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    sortBy = tab?.text.toString()
                    filter.filter("")
                    sharedPreferences.edit().putInt(SORT_BY_INDEX, tab?.position ?: 0).apply()
                }
            })
        }

        sortOrder = sharedPreferences.getString(SORT_ORDER, SortOrder.Descending.toString())
                ?: SortOrder.Descending.toString()
        toggleSortOrderDrawable()
        binding.filterCropsLayout.filterCropsSortOrderImageView.setOnClickListener {
            sortOrder = if (sortOrder == SortOrder.Ascending.toString()) SortOrder.Descending.toString() else SortOrder.Ascending.toString()
            toggleSortOrderDrawable()
            filter.filter("")
            sharedPreferences.edit().putString(SORT_ORDER, sortOrder).apply()
        }

        FragmentUtil.setupToggleFilterSettings(
            binding.filterCropsLayout.toggleFilterSettingsTextView,
            resources,
            binding.filterCropsLayout.filterCropsGroup,
            sharedPreferences,
            SHOW_FILTER_SETTINGS
        )

        binding.loadingLayout.loadingContainer.visibility = View.VISIBLE
        binding.filterCropsLayout.root.visibility = View.INVISIBLE
        binding.cropsRecyclerView.visibility = View.INVISIBLE

        val disposable = cropRepository.getCrops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { c ->
                    binding.loadingLayout.loadingContainer.visibility = View.INVISIBLE
                    binding.filterCropsLayout.root.visibility = View.VISIBLE
                    binding.cropsRecyclerView.visibility = View.VISIBLE
                    crops.clear()
                    crops.addAll(c)
                    filter.filter("")
                }

        compositeDisposable.add(disposable)
    }

    private fun setupAdapter(crops: List<Crop>) {
        hasAdapterBeenSetup = true
        adapter = CropsAdapter(crops, activity as MainActivity)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.cropsRecyclerView.adapter = adapter
        binding.cropsRecyclerView.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
        linearLayoutManager?.scrollToPosition(adapterPosition)
    }

    private fun toggleSortOrderDrawable() {
        // Images are flipped
        with (binding.filterCropsLayout.filterCropsSortOrderImageView) {
            if (sortOrder == SortOrder.Ascending.toString()) {
                setImageResource(R.drawable.ic_sort_descending)
                contentDescription = getString(R.string.ascending)
            } else {
                setImageResource(R.drawable.ic_sort_ascending)
                contentDescription = getString(R.string.descending)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = crops
                        .filter { crop ->
                            seasonFilterBy == "All" || (crop.seasons.contains(Season.fromString(seasonFilterBy)) && crop.seasons.size != Season.values().size)
                        }
                        .filter { crop -> crop.name.contains(searchTerm, true) }
                        .toMutableList()

                val comparator = when (sortBy) {
                    getString(R.string.a_z) -> Comparator { c1, c2 -> c1.name.compareTo(c2.name) }
                    getString(R.string.price) -> Comparator { c1, c2 -> c1.seedPrice.compareTo(c2.seedPrice) }
                    getString(R.string.time) -> Comparator { c1, c2 -> c1.harvestTime.compareTo(c2.harvestTime) }
                    getString(R.string.gold_per_day) -> Comparator { c1, c2 -> c1.goldPerDay.compareTo(c2.goldPerDay) }
                    else -> Comparator<Crop> { c1, c2 -> c1.name.compareTo(c2.name) }
                }

                filteredList.sortWith(comparator)

                if (sortOrder == SortOrder.Ascending.toString()) {
                    filteredList.reverse()
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                filterResults.count = filteredList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                val filteredList = filterResults?.values as List<Crop>

                if (hasAdapterBeenSetup) {
                    adapter?.updateCrops(filteredList)
                } else {
                    setupAdapter(filteredList)
                }
            }
        }
    }

    companion object {
        private val SEASON_INDEX = "${CropsFragment::class.java.simpleName}_SEASON_INDEX"
        private val SORT_BY_INDEX = "${CropsFragment::class.java.simpleName}_SORT_BY_INDEX"
        private val SORT_ORDER = "${CropsFragment::class.java.simpleName}_SORT_ORDER"
        private val SHOW_FILTER_SETTINGS = "${CropsFragment::class.java.simpleName}_SHOW_FILTER_SETTINGS"

        fun newInstance(): CropsFragment {
            return CropsFragment()
        }
    }
}
