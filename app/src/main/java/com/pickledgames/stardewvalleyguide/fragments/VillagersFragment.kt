package com.pickledgames.stardewvalleyguide.fragments

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
import com.pickledgames.stardewvalleyguide.views.GridDividerDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.filter_villagers.*
import kotlinx.android.synthetic.main.fragment_villagers.*
import javax.inject.Inject

class VillagersFragment : BaseFragment(), SearchView.OnQueryTextListener {

    @Inject lateinit var villagerRepository: VillagerRepository
    private var villagers: MutableList<Villager> = mutableListOf()
    private lateinit var villagersAdapter: VillagersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_villagers
        menuId = R.menu.villagers
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.villagers_search)
        searchMenuItem?.actionView?.let {
            val searchView = it as SearchView
            searchView.setOnQueryTextListener(this)
        }
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

            compositeDisposable.addAll(disposable)
        }

        filter_villagers_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                villagersAdapter.setSortBy(tab?.position)
                villagersAdapter.sort()
            }
        })
    }

    private fun setupAdapter() {
        villagersAdapter = VillagersAdapter(villagers, activity as MainActivity)
        villagers_recycler_view?.adapter = villagersAdapter
        villagers_recycler_view?.layoutManager = GridLayoutManager(activity, 3)
        val offset = activity?.resources?.getDimensionPixelOffset(R.dimen.villagers_grid_layout_offset)
        if (offset != null) villagers_recycler_view?.addItemDecoration(GridDividerDecoration(offset, 3))
        villagersAdapter.sort()
    }

    companion object {
        fun newInstance(): VillagersFragment {
            return VillagersFragment()
        }
    }
}
