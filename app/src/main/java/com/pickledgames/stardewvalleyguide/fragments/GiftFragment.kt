package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.VillagerReactionsAdapter
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.models.Gift
import com.pickledgames.stardewvalleyguide.models.GiftReaction
import com.pickledgames.stardewvalleyguide.repositories.GiftReactionRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_gift.*
import kotlinx.android.synthetic.main.header_item.*
import kotlinx.android.synthetic.main.loading.*
import javax.inject.Inject

class GiftFragment : InnerBaseFragment(), SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var giftReactionRepository: GiftReactionRepository
    private lateinit var gift: Gift
    private var list: MutableList<Any> = mutableListOf()
    private lateinit var adapter: VillagerReactionsAdapter
    private lateinit var layoutManager: GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_gift, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            val selectedGift: Gift? = arguments?.getParcelable(GIFT)
            if (selectedGift != null) {
                gift = selectedGift
                setup()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.gift, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.gift_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setQuery("", false)
        searchView.clearFocus()
        searchView.onActionViewCollapsed()
        searchView.setOnQueryTextListener(this)
        searchView.setOnQueryTextFocusChangeListener { _, b ->
            header_item_layout?.visibility = if (b) View.GONE else View.VISIBLE
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        filter.filter(query)
        return false
    }

    private fun setup() {
        setTitle(gift.name)
        header_item_image_view.setImageResource(gift.getImageId(activity as MainActivity))
        header_item_image_view.contentDescription = gift.name
        header_item_name_text_view.text = gift.name

        loading_container.visibility = View.VISIBLE
        gift_reactions_recycler_view.visibility = View.GONE
        val disposable = giftReactionRepository.getGiftReactionsByItemName(gift.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { giftReactions ->
                    loading_container.visibility = View.GONE
                    gift_reactions_recycler_view.visibility = View.VISIBLE
                    setupAdapter(giftReactions)
                }

        compositeDisposable.addAll(disposable)
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
        gift_reactions_recycler_view.adapter = adapter

        layoutManager = GridLayoutManager(activity, SPAN_COUNT)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // getSpanSize should return number of spans item should take up
                return if (list[position] is Reaction) SPAN_COUNT else 1
            }
        }

        gift_reactions_recycler_view.layoutManager = layoutManager
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
