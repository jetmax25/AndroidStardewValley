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
import com.pickledgames.stardewvalleyguide.adapters.MuseumItemsAdapter
import com.pickledgames.stardewvalleyguide.databinding.FragmentMuseumBinding
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
    private lateinit var binding: FragmentMuseumBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_museum
        menuId = R.menu.museum
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMuseumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onClick(view: View?) {
        val direction = if (view?.id == R.id.header_farm_left_arrow_image_view) FarmRepository.LEFT else FarmRepository.RIGHT
        val disposable = farmRepository.toggleSelectedFarm(direction).subscribe()
        compositeDisposable.add(disposable)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.museum_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, View.OnFocusChangeListener { _, b ->
            binding.museumHeaderGroup.visibility = if (b) View.GONE else View.VISIBLE
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        binding.headerFarmLayout.headerFarmLeftArrowImageView.setOnClickListener(this)
        binding.headerFarmLayout.headerFarmRightArrowImageView.setOnClickListener(this)
        binding.headerFarmLayout.headerFarmEasyFlipView.setOnClickListener {
            (activity as MainActivity).pushFragment(EditFarmsFragment.newInstance())
        }

        val collectionTabIndex = sharedPreferences.getInt(COLLECTION_INDEX, 0)
        binding.filterMuseumLayout.filterMuseumTabLayout.getTabAt(collectionTabIndex)?.select()
        collectionFilterBy = binding.filterMuseumLayout.filterMuseumTabLayout.getTabAt(collectionTabIndex)?.text.toString()
        binding.filterMuseumLayout.filterMuseumTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
        binding.filterMuseumLayout.showCompletedCheckBox.isChecked = showCompleted
        binding.filterMuseumLayout.showCompletedCheckBox.setOnCheckedChangeListener { _, b ->
            showCompleted = b
            adapter?.updateShowCompleted(showCompleted)
            sharedPreferences.edit().putBoolean(SHOW_COMPLETED, showCompleted).apply()
        }

        FragmentUtil.setupToggleFilterSettings(
            binding.filterMuseumLayout.toggleFilterSettingsTextView,
            resources,
            binding.filterMuseumLayout.filterMuseumGroup,
            sharedPreferences,
            SHOW_FILTER_SETTINGS
        )

        data class Results(
                val farm: Farm?,
                val museumItemsWrapper: MuseumItemRepository.MuseumItemsWrapper
        )

        binding.loadingLayout.loadingContainer.visibility = View.VISIBLE
        binding.museumHeaderGroup.visibility = View.INVISIBLE
        binding.museumRecyclerView.visibility = View.INVISIBLE

        val disposable = Single.zip(
                farmRepository.getSelectedFarm(),
                museumItemRepository.getMuseumItemsWrapper(),
                BiFunction { f: Farm?, miw: MuseumItemRepository.MuseumItemsWrapper -> Results(f, miw) }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { results ->
                    binding.loadingLayout.loadingContainer.visibility = View.INVISIBLE
                    binding.museumHeaderGroup.visibility = View.VISIBLE
                    binding.museumRecyclerView.visibility = View.VISIBLE
                    farm = results.farm
                    farm?.let {
                        binding.headerFarmLayout.headerFarmNameFrontTextView.text = String.format(getString(R.string.farm_name_template, it.name))
                        binding.headerFarmLayout.headerFarmNameBackTextView.text = String.format(getString(R.string.farm_name_template, it.name))
                    }
                    museumItemsWrapper = results.museumItemsWrapper
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
            adapter = MuseumItemsAdapter(
                    list,
                    it,
                    showCompleted,
                    activity as MainActivity,
                    this
            )

            binding.museumRecyclerView.adapter = adapter
            linearLayoutManager = LinearLayoutManager(activity)
            binding.museumRecyclerView.layoutManager = linearLayoutManager
            binding.museumRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
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
                    setupAdapter(filteredList)
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
