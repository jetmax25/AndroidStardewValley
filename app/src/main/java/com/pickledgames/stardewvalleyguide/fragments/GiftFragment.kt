package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.VillagerReactionsAdapter
import com.pickledgames.stardewvalleyguide.ads.AdsService
import com.pickledgames.stardewvalleyguide.databinding.FragmentGiftBinding
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.Gift
import com.pickledgames.stardewvalleyguide.models.GiftReaction
import com.pickledgames.stardewvalleyguide.repositories.GiftReactionRepository
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GiftFragment : InnerBaseFragment(), SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var giftReactionRepository: GiftReactionRepository
    @Inject lateinit var adsManager: AdsManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var adsService: AdsService
    private lateinit var gift: Gift
    private var list: MutableList<Any> = mutableListOf()
    private lateinit var adapter: VillagerReactionsAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var binding: FragmentGiftBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        menuId = R.menu.gift
        if (adsService.areAdsEnabled()) {
            adsManager.showAdFor(AdsManager.GIFT_FRAGMENT, requireActivity())
        }
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentGiftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (arguments != null) {
            val selectedGift: Gift? = arguments?.getParcelable(GIFT)
            if (selectedGift != null) {
                gift = selectedGift
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.gift_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, View.OnFocusChangeListener { _, b ->
            binding.headerItemLayout.root.visibility = if (b) View.GONE else View.VISIBLE
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        filter.filter(query)
        return false
    }

    override fun setup() {
        setTitle(gift.name)
        binding.headerItemLayout.headerItemLeftImageView.setImageResource(
            gift.getImageId(activity as MainActivity)
        )
        binding.headerItemLayout.headerItemLeftImageView.contentDescription = gift.name
        binding.headerItemLayout.headerItemNameTextView.text = gift.name
        binding.headerItemLayout.headerItemRightImageView.setImageResource(
            gift.getImageId(activity as MainActivity)
        )
        binding.headerItemLayout.headerItemRightImageView.contentDescription = gift.name
        binding.loadingLayout.loadingContainer.visibility = View.VISIBLE
        binding.giftReactionsRecyclerView.visibility = View.GONE
        val disposable = giftReactionRepository.getGiftReactionsByItemName(gift.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { giftReactions ->
                    binding.loadingLayout.loadingContainer.visibility = View.GONE
                    binding.giftReactionsRecyclerView.visibility = View.VISIBLE
                    setupAdapter(giftReactions)
                }

        compositeDisposable.add(disposable)

        analyticsManager.logEvent("Gift Detail", mapOf("Gift" to gift.name))
    }

    private fun setupAdapter(giftReactions: List<GiftReaction>) {
        val reactionCountMap: MutableMap<Reaction, Int> = HashMap()
        for (reaction: Reaction in Reaction.values().asList()) {
            list.add(reaction)
            val filteredGiftReactions = giftReactions.filter { it.reaction == reaction }.sortedBy { it.villagerName }.toList()
            reactionCountMap[reaction] = filteredGiftReactions.size
            list.addAll(filteredGiftReactions)
        }

        list = list.filter {
            if (it is Reaction && reactionCountMap[it] == 0) return@filter false
            return@filter true
        }.toMutableList()

        adapter = VillagerReactionsAdapter(list)
        binding.giftReactionsRecyclerView.adapter = adapter

        layoutManager = GridLayoutManager(activity, SPAN_COUNT)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // getSpanSize should return number of spans item should take up
                return if (list[position] is Reaction) SPAN_COUNT else 1
            }
        }

        binding.giftReactionsRecyclerView.layoutManager = layoutManager
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val reactionCountMap: MutableMap<Reaction, Int> = HashMap()
                val filteredList: List<Any> = list.filter {
                    if (it is Reaction) {
                        return@filter true
                    } else if (it is GiftReaction && it.villagerName.contains(constraint.toString(), true)) {
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

    companion object {
        private const val GIFT = "GIFT"
        private const val SPAN_COUNT = 7

        fun newInstance(gift: Gift): GiftFragment {
            val giftFragment = GiftFragment()
            val arguments = Bundle()
            arguments.putParcelable(GIFT, gift)
            giftFragment.arguments = arguments
            return giftFragment
        }
    }
}
