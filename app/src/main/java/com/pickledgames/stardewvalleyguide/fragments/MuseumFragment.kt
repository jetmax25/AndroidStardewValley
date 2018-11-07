package com.pickledgames.stardewvalleyguide.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.MuseumItemsAdapter
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.models.MuseumItem
import com.pickledgames.stardewvalleyguide.models.MuseumItemCollection
import com.pickledgames.stardewvalleyguide.repositories.FarmRepository
import com.pickledgames.stardewvalleyguide.repositories.MuseumItemRepository
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.filter_museum.*
import kotlinx.android.synthetic.main.fragment_museum.*
import kotlinx.android.synthetic.main.header_farm.*
import kotlinx.android.synthetic.main.loading.*
import javax.inject.Inject

class MuseumFragment : BaseFragment(), View.OnClickListener, OnItemCheckedListener, SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var farmRepository: FarmRepository
    @Inject lateinit var museumItemRepository: MuseumItemRepository
    @Inject lateinit var sharedPreferences: SharedPreferences
    private var farm: Farm? = null
    private var museumItemsWrapper: MuseumItemRepository.MuseumItemsWrapper? = null
    private var adapter: MuseumItemsAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var searchTerm: String = ""
    private var collectionFilterBy: String = ""
    private var showCompleted: Boolean = false
    private var hasAdapterBeenSetup: Boolean = false
    private var adapterPosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_museum
        menuId = R.menu.museum
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onClick(view: View?) {
        val direction = if (view?.id == R.id.header_farm_left_arrow_image_view) FarmRepository.LEFT else FarmRepository.RIGHT
        val disposable = farmRepository.toggleSelectedFarm(direction).subscribe()
        compositeDisposable.add(disposable)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu?.findItem(R.id.museum_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, View.OnFocusChangeListener { _, b ->
            museum_header_group?.visibility = if (b) View.GONE else View.VISIBLE
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.museum_edit_farms -> {
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

        val collectionTabIndex = sharedPreferences.getInt(COLLECTION_INDEX, 0)
        filter_museum_tab_layout?.getTabAt(collectionTabIndex)?.select()
        collectionFilterBy = filter_museum_tab_layout?.getTabAt(collectionTabIndex)?.text.toString()
        filter_museum_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                collectionFilterBy = tab?.text.toString()
                filter.filter("")
                sharedPreferences.edit().putInt(COLLECTION_INDEX, tab?.position
                        ?: 0).apply()
            }
        })

        showCompleted = sharedPreferences.getBoolean(SHOW_COMPLETED, false)
        show_completed_check_box.isChecked = showCompleted
        show_completed_check_box?.setOnCheckedChangeListener { _, b ->
            showCompleted = b
            adapter?.updateShowCompleted(showCompleted)
            sharedPreferences.edit().putBoolean(SHOW_COMPLETED, showCompleted).apply()
        }

        FragmentUtil.setupToggleFilterSettings(toggle_filter_settings_text_view, resources, filter_museum_group, sharedPreferences, SHOW_FILTER_SETTINGS)

        data class Results(
                val farm: Farm,
                val museumItemsWrapper: MuseumItemRepository.MuseumItemsWrapper
        )

        loading_container?.visibility = View.VISIBLE
        museum_header_group?.visibility = View.INVISIBLE
        museum_recycler_view?.visibility = View.INVISIBLE

        val disposable = Single.zip(
                farmRepository.getSelectedFarm(),
                museumItemRepository.getMuseumItemsWrapper(),
                BiFunction { f: Farm, miw: MuseumItemRepository.MuseumItemsWrapper -> Results(f, miw) }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { results ->
                    loading_container?.visibility = View.INVISIBLE
                    museum_header_group?.visibility = View.VISIBLE
                    museum_recycler_view?.visibility = View.VISIBLE
                    farm = results.farm
                    farm?.let {
                        header_farm_name_front_text_view?.text = String.format(getString(R.string.farm_name_template, it.name))
                        header_farm_name_back_text_view?.text = String.format(getString(R.string.farm_name_template, it.name))
                    }
                    museumItemsWrapper = results.museumItemsWrapper
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

    private fun setupFishesAdapter(list: List<Any>) {
        farm?.let {
            hasAdapterBeenSetup = true
            adapter = MuseumItemsAdapter(
                    list,
                    it,
                    showCompleted,
                    activity as MainActivity,
                    this
            )

            museum_recycler_view?.adapter = adapter
            linearLayoutManager = LinearLayoutManager(activity)
            museum_recycler_view?.layoutManager = linearLayoutManager
            museum_recycler_view?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            linearLayoutManager?.scrollToPosition(adapterPosition)
        }
    }

    override fun onItemChecked(museumItem: MuseumItem, isChecked: Boolean) {
        if (isChecked) farm?.museumItems?.add(museumItem.uniqueId)
        else farm?.museumItems?.remove(museumItem.uniqueId)
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

                if (museumItemsWrapper?.artifacts != null && (collectionFilterBy == "All" || collectionFilterBy == "Artifacts")) {
                    val filteredArtifacts = museumItemsWrapper?.artifacts!!
                            .filter { artifact -> artifact.name.contains(searchTerm, true) }
                    if (filteredArtifacts.isNotEmpty()) {
                        filteredList.add(MuseumItemCollection("Artifacts", museumItemsWrapper?.artifacts!!.size))
                        filteredList.addAll(filteredArtifacts)
                    }
                }

                if (museumItemsWrapper?.lostBooks != null && (collectionFilterBy == "All" || collectionFilterBy == "Lost Books")) {
                    val filteredLostBooks = museumItemsWrapper?.lostBooks!!
                            .filter { lostBook -> lostBook.name.contains(searchTerm, true) }
                    if (filteredLostBooks.isNotEmpty()) {
                        filteredList.add(MuseumItemCollection("Lost Books", museumItemsWrapper?.lostBooks!!.size))
                        filteredList.addAll(filteredLostBooks)
                    }
                }

                if (museumItemsWrapper?.minerals != null && (collectionFilterBy == "All" || collectionFilterBy == "Minerals")) {
                    val filteredMinerals = museumItemsWrapper?.minerals!!
                            .filter { mineral -> mineral.name.contains(searchTerm, true) }
                    if (filteredMinerals.isNotEmpty()) {
                        filteredList.add(MuseumItemCollection("Minerals", museumItemsWrapper?.minerals!!.size))
                        filteredList.addAll(filteredMinerals)
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
                    setupFishesAdapter(filteredList)
                }
            }
        }
    }

    companion object {
        private val COLLECTION_INDEX = "${MuseumFragment::class.java.simpleName}_COLLECTION_INDEX"
        private val SHOW_COMPLETED = "${MuseumFragment::class.java.simpleName}_SHOW_COMPLETED"
        private val SHOW_FILTER_SETTINGS = "${MuseumFragment::class.java.simpleName}_SHOW_FILTER_SETTINGS"

        fun newInstance(): MuseumFragment {
            return MuseumFragment()
        }
    }
}
