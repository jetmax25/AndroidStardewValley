package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.GiftsAdapter
import com.pickledgames.stardewvalleyguide.models.Gift
import com.pickledgames.stardewvalleyguide.repositories.GiftReactionRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_gifts.*
import javax.inject.Inject

class GiftsFragment : BaseFragment(), SearchView.OnQueryTextListener, Filterable {

    @Inject lateinit var giftReactionRepository: GiftReactionRepository
    private var gifts: MutableList<Gift> = mutableListOf()
    private var list: MutableList<Any> = mutableListOf()
    private lateinit var adapter: GiftsAdapter
    private lateinit var layoutManager: GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_gifts, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.gifts, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu.findItem(R.id.gifts_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        filter.filter(query)
        return false
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredGifts = gifts.filter { g -> g.name.contains(constraint.toString(), true) }
                val filteredList = filterAndSortGifts(filteredGifts)

                val filterResults = FilterResults()
                filterResults.values = filteredList
                filterResults.count = filteredList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                val filteredList = results?.values as MutableList<Any>
                adapter.updateList(filteredList)
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        // getSpanSize should return number of spans item should take up
                        return if (filteredList[position] is String) SPAN_COUNT else 1
                    }
                }
            }
        }
    }

    private fun setup() {
        if (list.isNotEmpty()) {
            setupAdapter()
        } else {
            val disposable = giftReactionRepository.getGifts()
                    .doOnSuccess { g -> gifts.addAll(g) }
                    .map(this::filterAndSortGifts)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { l ->
                        list.addAll(l)
                        setupAdapter()
                    }

            compositeDisposable.addAll(disposable)
        }
    }

    private fun filterAndSortGifts(gifts: List<Gift>): List<Any> {
        val categories: MutableSet<String> = mutableSetOf()
        for (gift: Gift in gifts) {
            categories.add(gift.category)
        }

        val list: MutableList<Any> = mutableListOf()
        for (category: String in categories.sorted()) {
            list.add(category)
            val filteredGifts = gifts.filter { it.category == category }.sortedBy { it.name }.toList()
            list.addAll(filteredGifts)
        }

        return list
    }

    private fun setupAdapter() {
        adapter = GiftsAdapter(list, activity as MainActivity)
        gifts_recycler_view.adapter = adapter

        layoutManager = GridLayoutManager(activity, SPAN_COUNT)
        gifts_recycler_view.layoutManager = layoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // getSpanSize should return number of spans item should take up
                return if (list[position] is String) SPAN_COUNT else 1
            }
        }
    }

    companion object {
        private const val SPAN_COUNT = 6

        fun newInstance(): GiftsFragment {
            return GiftsFragment()
        }
    }
}
