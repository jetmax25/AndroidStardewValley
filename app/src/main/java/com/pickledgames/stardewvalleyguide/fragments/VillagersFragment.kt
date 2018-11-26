package com.pickledgames.stardewvalleyguide.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.VillagersAdapter
import com.pickledgames.stardewvalleyguide.models.Villager
import com.pickledgames.stardewvalleyguide.repositories.VillagerRepository
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import com.pickledgames.stardewvalleyguide.views.GridDividerDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.filter_villagers.*
import kotlinx.android.synthetic.main.fragment_villagers.*
import javax.inject.Inject

class VillagersFragment : BaseFragment(), SearchView.OnQueryTextListener {

    @Inject lateinit var villagerRepository: VillagerRepository
    @Inject lateinit var sharedPreferences: SharedPreferences
    private var villagers: MutableList<Villager> = mutableListOf()
    private lateinit var villagersAdapter: VillagersAdapter
    private var sortBy: String = "A-Z"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_villagers
        menuId = R.menu.villagers
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.villagers_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, null)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        villagersAdapter.filter.filter(query)
        return false
    }

    override fun setup() {
        if (villagers.isNotEmpty()) {
            setupAdapter()
        } else {
            val disposable = villagerRepository.getVillagers()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { v ->
                        villagers.addAll(v)
                        setupAdapter()
                    }

            compositeDisposable.add(disposable)
        }

        val sortByTabIndex = sharedPreferences.getInt(SORT_BY_TAB_INDEX, 0)
        filter_villagers_tab_layout?.getTabAt(sortByTabIndex)?.select()
        sortBy = filter_villagers_tab_layout?.getTabAt(sortByTabIndex)?.text.toString()
        filter_villagers_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                sortBy = tab?.text.toString()
                villagersAdapter.setSortBy(sortBy)
                sharedPreferences.edit().putInt(SORT_BY_TAB_INDEX, tab?.position ?: 0).apply()
            }
        })
    }

    private fun setupAdapter() {
        villagersAdapter = VillagersAdapter(villagers, activity as MainActivity)
        villagers_recycler_view?.adapter = villagersAdapter
        villagers_recycler_view?.layoutManager = GridLayoutManager(activity, 3)
        val offset = activity?.resources?.getDimensionPixelOffset(R.dimen.villagers_grid_layout_offset)
        if (offset != null) villagers_recycler_view?.addItemDecoration(GridDividerDecoration(offset, 3))
        villagersAdapter.setSortBy(sortBy)
    }

    companion object {
        private val SORT_BY_TAB_INDEX = "${VillagersFragment::class.java.simpleName}_SORT_BY_TAB_INDEX"

        fun newInstance(): VillagersFragment {
            return VillagersFragment()
        }
    }
}
