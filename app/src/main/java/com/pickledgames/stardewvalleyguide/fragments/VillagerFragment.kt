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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.GiftReactionsAdapter
import com.pickledgames.stardewvalleyguide.databinding.FragmentVillagerBinding
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.GiftReaction
import com.pickledgames.stardewvalleyguide.models.Villager
import com.pickledgames.stardewvalleyguide.repositories.GiftReactionRepository
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VillagerFragment : InnerBaseFragment(), SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var giftReactionRepository: GiftReactionRepository
    @Inject lateinit var adsManager: AdsManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var sharedPreferences: SharedPreferences
    private lateinit var villager: Villager
    private var list: MutableList<Any> = mutableListOf()
    private lateinit var adapter: GiftReactionsAdapter
    private lateinit var layoutManager: GridLayoutManager
    private var filterBy: String = "All"
    private var searchTerm: String = ""
    private lateinit var binding: FragmentVillagerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layoutId = R.layout.fragment_villager
        menuId = R.menu.villager
        adsManager.showAdFor(AdsManager.VILLAGER_FRAGMENT, requireActivity())
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentVillagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (arguments != null) {
            val selectedVillager: Villager? = arguments?.getParcelable(VILLAGER)
            if (selectedVillager != null) {
                villager = selectedVillager
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.villager_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, View.OnFocusChangeListener { _, b ->
            binding.headerVillagerLayout.root.visibility = if (b) View.GONE else View.VISIBLE
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

    override fun setup() {
        setTitle(villager.name)
        with (binding) {
            with (binding.headerVillagerLayout) {
                headerVillagerImageView.setImageResource(villager.getImageId(activity as MainActivity))
                headerVillagerImageView.contentDescription = villager.name
                headerVillagerNameTextView.text = villager.name
                headerVillagerBirthdayTextView.text = villager.birthday.toString()
                headerVillagerBirthdayTextView.setCompoundDrawablesWithIntrinsicBounds(
                    villager.birthday.season.getImageId(activity as MainActivity),
                    0, 0, 0
                )
            }

            binding.loadingVillagerLayout.loadingContainer.visibility = View.VISIBLE
            villagerRecyclerView.visibility = View.GONE
            val disposable = giftReactionRepository.getGiftReactionsByVillagerName(villager.name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { giftReactions ->
                        binding.loadingVillagerLayout.loadingContainer.visibility = View.GONE
                        villagerRecyclerView.visibility = View.VISIBLE
                        setupAdapter(giftReactions)
                    }

            compositeDisposable.add(disposable)

            val filterByTabIndex = sharedPreferences.getInt(FILTER_BY_TAB_INDEX, 0)
            binding.filterVillagerLayout.filterVillagerTabLayout.getTabAt(filterByTabIndex)?.select()
            filterBy = binding.filterVillagerLayout.filterVillagerTabLayout.getTabAt(filterByTabIndex)?.text.toString()
            binding.filterVillagerLayout.filterVillagerTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    filterBy = tab?.text.toString()
                    filter.filter("")
                    sharedPreferences.edit().putInt(FILTER_BY_TAB_INDEX, tab?.position ?: 0).apply()
                }
            })

            analyticsManager.logEvent("Villager Detail", mapOf("Villager" to villager.name))
        }
    }

    private fun setupAdapter(giftReactions: List<GiftReaction>) {
        val reactionCountMap: MutableMap<Reaction, Int> = HashMap()
        for (reaction: Reaction in Reaction.values().asList()) {
            list.add(reaction)
            val filteredGiftReactions = giftReactions.filter { it.reaction == reaction }.sortedBy { it.itemName }.toList()
            reactionCountMap[reaction] = filteredGiftReactions.size
            list.addAll(filteredGiftReactions)
        }

        list = list.filter {
            if (it is Reaction && reactionCountMap[it] == 0) return@filter false
            return@filter true
        }.toMutableList()

        adapter = GiftReactionsAdapter(list)
        binding.villagerRecyclerView.adapter = adapter

        layoutManager = GridLayoutManager(activity, SPAN_COUNT)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // getSpanSize should return number of spans item should take up
                return if (list[position] is Reaction) SPAN_COUNT else 1
            }
        }

        binding.villagerRecyclerView.layoutManager = layoutManager
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val reactionCountMap: MutableMap<Reaction, Int> = HashMap()
                val filteredList: List<Any> = list.filter {
                    if (it is Reaction && (it.type == filterBy || filterBy == "All")) {
                        return@filter true
                    } else if (it is GiftReaction && (it.reaction.type == filterBy || filterBy == "All") && it.itemName.contains(searchTerm, true)) {
                        reactionCountMap[it.reaction] = reactionCountMap[it.reaction]?.inc() ?: 1
                        return@filter true
                    }
                    return@filter false
                }.filter {
                    if (it is Reaction && reactionCountMap[it] == null) return@filter false
                    return@filter true
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                filterResults.count = filteredList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                val filteredList = results?.values as List<Any>
                if (::adapter.isInitialized) {
                    adapter.updateList(filteredList)
                    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            // getSpanSize should return number of spans item should take up
                            return if (filteredList[position] is Reaction) SPAN_COUNT else 1
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val VILLAGER = "VILLAGER"
        private const val SPAN_COUNT = 8
        private val FILTER_BY_TAB_INDEX = "${VillagerFragment::class.java.simpleName}_FILTER_BY_TAB_INDEX"

        fun newInstance(villager: Villager): VillagerFragment {
            val villagerFragment = VillagerFragment()
            val arguments = Bundle()
            arguments.putParcelable(VILLAGER, villager)
            villagerFragment.arguments = arguments
            return villagerFragment
        }
    }
}
