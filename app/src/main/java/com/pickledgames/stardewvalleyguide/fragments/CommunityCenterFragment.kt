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
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.CommunityCenterItemsAdapter
import com.pickledgames.stardewvalleyguide.databinding.FragmentCommunityCenterBinding
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.CommunityCenterBundle
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.repositories.CommunityCenterRepository
import com.pickledgames.stardewvalleyguide.repositories.FarmRepository
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CommunityCenterFragment : BaseFragment(), View.OnClickListener, OnItemCheckedListener, SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var farmRepository: FarmRepository
    @Inject lateinit var communityCenterRepository: CommunityCenterRepository
    @Inject lateinit var sharedPreferences: SharedPreferences
    private var farm: Farm? = null
    private var bundles: MutableList<CommunityCenterBundle> = mutableListOf()
    private var adapter: CommunityCenterItemsAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var seasonFilterBy: String = ""
    private var searchTerm: String = ""
    private var showCompleted: Boolean = false
    private var hasAdapterBeenSetup: Boolean = false
    private var adapterPosition: Int = 0
    private lateinit var binding: FragmentCommunityCenterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layoutId = R.layout.fragment_community_center
        menuId = R.menu.community_center
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCommunityCenterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onClick(view: View?) {
        val direction = if (view?.id == R.id.header_farm_left_arrow_image_view) FarmRepository.LEFT else FarmRepository.RIGHT
        val disposable = farmRepository.toggleSelectedFarm(direction).subscribe()
        compositeDisposable.add(disposable)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.community_center_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, View.OnFocusChangeListener { _, b ->
            binding.communityCenterHeaderGroup.visibility = if (b) View.GONE else View.VISIBLE
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        adapterPosition = linearLayoutManager?.findFirstVisibleItemPosition() ?: 0
    }

    override fun setup() {
        binding.headerFarmLayout.headerFarmLeftArrowImageView.setOnClickListener(this)
        binding.headerFarmLayout.headerFarmRightArrowImageView.setOnClickListener(this)
        binding.headerFarmLayout.headerFarmEasyFlipView.setOnClickListener {
            (activity as MainActivity).pushFragment(EditFarmsFragment.newInstance())
        }

        val seasonTabIndex = sharedPreferences.getInt(SEASON_INDEX, 0)
        binding.filterCommunityCenterLayout.filterCommunityCenterSeasonTabLayout.getTabAt(seasonTabIndex)?.select()
        seasonFilterBy = binding.filterCommunityCenterLayout.filterCommunityCenterSeasonTabLayout.getTabAt(seasonTabIndex)?.text.toString()
        binding.filterCommunityCenterLayout.filterCommunityCenterSeasonTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                seasonFilterBy = tab?.text.toString()
                filter.filter("")
                sharedPreferences.edit().putInt(SEASON_INDEX, tab?.position ?: 0).apply()
            }
        })

        showCompleted = sharedPreferences.getBoolean(SHOW_COMPLETED, false)
        binding.filterCommunityCenterLayout.showCompletedCheckBox.isChecked = showCompleted
        binding.filterCommunityCenterLayout.showCompletedCheckBox.setOnCheckedChangeListener { _, b ->
            showCompleted = b
            adapter?.updateShowCompleted(showCompleted)
            sharedPreferences.edit().putBoolean(SHOW_COMPLETED, showCompleted).apply()
        }

        FragmentUtil.setupToggleFilterSettings(
            binding.filterCommunityCenterLayout.toggleFilterSettingsTextView,
            resources,
            binding.filterCommunityCenterLayout.filterCommunityCenterGroup,
            sharedPreferences,
            SHOW_FILTER_SETTINGS
        )

        data class Results(
                val farm: Farm?,
                val bundles: List<CommunityCenterBundle>
        )

        binding.loadingLayout.loadingContainer.visibility = View.VISIBLE
        binding.communityCenterHeaderGroup.visibility = View.INVISIBLE
        binding.communityCenterItemsRecyclerView.visibility = View.INVISIBLE

        val disposable = Single.zip(
                farmRepository.getSelectedFarm(),
                communityCenterRepository.getBundles(),
                BiFunction { f: Farm?, b: List<CommunityCenterBundle> -> Results(f, b) }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { results ->
                    binding.loadingLayout.loadingContainer.visibility = View.INVISIBLE
                    binding.communityCenterHeaderGroup.visibility = View.VISIBLE
                    binding.communityCenterItemsRecyclerView.visibility = View.VISIBLE
                    farm = results.farm
                    farm?.let {
                        binding.headerFarmLayout.headerFarmNameFrontTextView.text = String.format(getString(R.string.farm_name_template, it.name))
                        binding.headerFarmLayout.headerFarmNameBackTextView.text = String.format(getString(R.string.farm_name_template, it.name))
                    }
                    bundles.clear()
                    bundles.addAll(results.bundles)
                    filter.filter("")
                }

        compositeDisposable.add(disposable)

        val selectedFarmChangesDisposable = farmRepository.getSelectedFarmChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { f ->
                    FragmentUtil.flipSelectedFarmText(
                        binding.headerFarmLayout.headerFarmEasyFlipView,
                        binding.headerFarmLayout.headerFarmNameFrontTextView,
                        binding.headerFarmLayout.headerFarmNameBackTextView,
                        resources,
                        f
                    )
                    farm = f
                    farm?.let {
                        adapter?.updateFarm(it)
                    }
                }

        compositeDisposable.add(selectedFarmChangesDisposable)
    }

    private fun setupAdapter(list: List<Any>) {
        farm?.let {
            hasAdapterBeenSetup = true
            adapter = CommunityCenterItemsAdapter(
                    list,
                    it,
                    showCompleted,
                    activity as MainActivity,
                    this
            )

            binding.communityCenterItemsRecyclerView.adapter = adapter
            linearLayoutManager = LinearLayoutManager(activity)
            binding.communityCenterItemsRecyclerView.layoutManager = linearLayoutManager
            binding.communityCenterItemsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            linearLayoutManager?.scrollToPosition(adapterPosition)
        }
    }

    override fun onItemChecked(communityCenterItem: CommunityCenterItem, isChecked: Boolean) {
        if (isChecked) farm?.communityCenterItems?.add(communityCenterItem.uniqueId)
        else farm?.communityCenterItems?.remove(communityCenterItem.uniqueId)
        adapter?.notifyDataSetChanged()
        farm?.let {
            farmRepository.updateSelectedFarm(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Any>()
                bundles.forEach { bundle ->
                    val filteredBundleItems = bundle.items
                            .filter { item ->
                                seasonFilterBy == "All" || (item.seasons.contains(Season.fromString(seasonFilterBy)) && item.seasons.size != Season.values().size)
                            }
                            .filter { item ->
                                if (showCompleted) return@filter true
                                return@filter !(farm?.communityCenterItems?.contains(item.name)
                                        ?: false)
                            }
                            .filter { item -> item.name.contains(searchTerm, true) || bundle.name.contains(searchTerm, true) }
                            .toMutableList()

                    val isComplete = farm?.getCompletedItemsCount(bundle) == bundle.needed

                    if ((showCompleted || !isComplete) && filteredBundleItems.isNotEmpty()) {
                        filteredList.add(bundle)
                        filteredList.addAll(filteredBundleItems)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                filterResults.count = filteredList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                val filteredList = filterResults?.values as List<Any>

                if (hasAdapterBeenSetup) {
                    adapter?.updateList(filteredList)
                } else {
                    setupAdapter(filteredList)
                }
            }
        }
    }

    companion object {
        private val SEASON_INDEX = "${CommunityCenterFragment::class.java.simpleName}_SEASON_INDEX"
        private val SHOW_COMPLETED = "${CommunityCenterFragment::class.java.simpleName}_SHOW_COMPLETED"
        private val SHOW_FILTER_SETTINGS = "${CommunityCenterFragment::class.java.simpleName}_SHOW_FILTER_SETTINGS"

        fun newInstance(): CommunityCenterFragment {
            return CommunityCenterFragment()
        }
    }
}
