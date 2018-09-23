package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.CommunityCenterItemsAdapter
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.CommunityCenterBundle
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.repositories.CommunityCenterRepository
import com.pickledgames.stardewvalleyguide.repositories.FarmRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.filter_community_center.*
import kotlinx.android.synthetic.main.fragment_community_center.*
import kotlinx.android.synthetic.main.header_farm.*
import kotlinx.android.synthetic.main.loading.*
import javax.inject.Inject

class CommunityCenterFragment : BaseFragment(), View.OnClickListener, OnItemCheckedListener, SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var farmRepository: FarmRepository
    @Inject lateinit var communityCenterRepository: CommunityCenterRepository
    private lateinit var farm: Farm
    private var bundles: MutableList<CommunityCenterBundle> = mutableListOf()
    private var list: MutableList<Any> = mutableListOf()
    private lateinit var adapter: CommunityCenterItemsAdapter
    private var filterBy: String = "All"
    private var searchTerm: String = ""
    private var showMissing: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_community_center, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onClick(view: View?) {
        val direction = if (view?.id == R.id.header_farm_left_arrow_image_view) FarmRepository.LEFT else FarmRepository.RIGHT
        farmRepository.toggleSelectedFarm(direction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { f ->
                    farm = f
                    header_farm_name_text_view.text = String.format(getString(R.string.farm_name_template, farm.name))
                    adapter.updateFarm(farm)
                }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.community_center, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu?.findItem(R.id.community_center_search)
        val searchView = searchMenuItem?.actionView as SearchView
        searchView.setQuery("", false)
        searchView.clearFocus()
        searchView.onActionViewCollapsed()
        searchView.setOnQueryTextListener(this)
        searchView.setOnQueryTextFocusChangeListener { _, b ->
            community_center_header_group?.visibility = if (b) View.GONE else View.VISIBLE
        }
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

    private fun setup() {
        header_farm_name_text_view.setOnClickListener {
            (activity as MainActivity).pushFragment(EditFarmsFragment.newInstance())
        }

        header_farm_left_arrow_image_view.setOnClickListener(this)
        header_farm_right_arrow_image_view.setOnClickListener(this)
        filter_community_center_season_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterBy = tab?.text.toString()
                filter.filter("")
            }
        })
        show_missing_check_box.setOnCheckedChangeListener { _, b ->
            showMissing = b
            filter.filter("")
        }

        data class Results(
                val farm: Farm,
                val bundles: List<CommunityCenterBundle>
        )

        loading_container.visibility = View.VISIBLE
        community_center_header_group.visibility = View.INVISIBLE
        community_center_items_recycler_view.visibility = View.INVISIBLE

        val disposable = Single.zip(
                farmRepository.getSelectedFarm(),
                communityCenterRepository.getBundles(),
                BiFunction { f: Farm, b: List<CommunityCenterBundle> -> Results(f, b) }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { results ->
                    loading_container.visibility = View.INVISIBLE
                    community_center_header_group.visibility = View.VISIBLE
                    community_center_items_recycler_view.visibility = View.VISIBLE
                    farm = results.farm
                    header_farm_name_text_view.text = String.format(getString(R.string.farm_name_template, farm.name))
                    bundles.addAll(results.bundles)
                    setupCommunityCenterItemsAdapter()
                }

        compositeDisposable.add(disposable)
    }

    private fun setupCommunityCenterItemsAdapter() {
        bundles.forEach {
            list.add(it)
            list.addAll(it.items)
        }

        adapter = CommunityCenterItemsAdapter(
                list,
                farm,
                activity as MainActivity,
                this
        )

        community_center_items_recycler_view.adapter = adapter
        community_center_items_recycler_view.layoutManager = LinearLayoutManager(activity)
    }

    override fun onItemChecked(communityCenterItem: CommunityCenterItem, isChecked: Boolean) {
        if (isChecked) farm.communityCenterItems.add(communityCenterItem.name)
        else farm.communityCenterItems.remove(communityCenterItem.name)
        adapter.notifyDataSetChanged()
        farmRepository.updateSelectedFarm(farm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Any>()
                bundles.forEach { bundle ->
                    val filteredBundleItems = bundle.items
                            .filter { item -> filterBy == "All" || item.seasons.contains(Season.fromString(filterBy)) }
                            .filter { item ->
                                if (showMissing) return@filter !farm.communityCenterItems.contains(item.name)
                                return@filter true
                            }
                            .filter { item -> item.name.contains(searchTerm, true) }

                    if (filteredBundleItems.isNotEmpty()) {
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
                adapter.updateList(filteredList)
            }
        }
    }

    companion object {
        fun newInstance(): CommunityCenterFragment {
            return CommunityCenterFragment()
        }
    }
}