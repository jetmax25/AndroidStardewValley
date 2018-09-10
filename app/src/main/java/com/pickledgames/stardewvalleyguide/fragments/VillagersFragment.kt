package com.pickledgames.stardewvalleyguide.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.adapters.VillagersAdapter
import com.pickledgames.stardewvalleyguide.models.Villager
import com.pickledgames.stardewvalleyguide.repositories.VillagerRepository
import com.pickledgames.stardewvalleyguide.views.GridDividerDecoration
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.filter_villagers.*
import kotlinx.android.synthetic.main.fragment_villagers.*
import javax.inject.Inject

class VillagersFragment : Fragment(), SearchView.OnQueryTextListener {

    @Inject lateinit var villagerRepository: VillagerRepository
    var villagers: MutableList<Villager> = mutableListOf()
    lateinit var villagersAdapter: VillagersAdapter

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_villagers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.villagers, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchMenuItem = menu.findItem(R.id.villagers_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        (villagers_recycler_view.adapter as VillagersAdapter).filter.filter(query)
        return false
    }

    private fun setup() {
        villagerRepository.getVillagers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { v ->
                    villagers.addAll(v)
                    villagersAdapter = VillagersAdapter(villagers)
                    villagers_recycler_view.adapter = villagersAdapter
                    villagers_recycler_view.layoutManager = GridLayoutManager(activity, 3)
                    val offset = activity?.resources?.getDimensionPixelOffset(R.dimen.villagers_grid_layout_offset)
                    if (offset != null) villagers_recycler_view.addItemDecoration(GridDividerDecoration(offset, 3))
                    villagersAdapter.sort()
                }

        filter_villagers_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                villagersAdapter.sortBy = tab?.position
                villagersAdapter.sort()
            }
        })
    }

    companion object {
        fun newInstance(): VillagersFragment {
            return VillagersFragment()
        }
    }
}
