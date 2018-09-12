package com.pickledgames.stardewvalleyguide.fragments

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.GiftReactionsAdapter
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.models.GiftReaction
import com.pickledgames.stardewvalleyguide.models.Villager
import com.pickledgames.stardewvalleyguide.repositories.GiftReactionRepository
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_villager.*
import kotlinx.android.synthetic.main.profile_villager.*
import javax.inject.Inject

class VillagerFragment : InnerFragment(), SearchView.OnQueryTextListener {

    @Inject lateinit var giftReactionRepository: GiftReactionRepository
    lateinit var villager: Villager
    var giftReactions: MutableList<GiftReaction> = mutableListOf()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_villager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            val selectedVillager: Villager? = arguments?.getParcelable(VILLAGER)
            if (selectedVillager != null) {
                villager = selectedVillager
                setup()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.villager, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchMenuItem = menu.findItem(R.id.villager_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return false
    }

    private fun setup() {
        setTitle(villager.name)
        profile_villager_image_view.setImageResource(villager.getImageId(activity as MainActivity))
        profile_villager_name_text_view.text = villager.name
        profile_villager_birthday_text_view.text = villager.birthday.toString()
        profile_villager_birthday_text_view.setCompoundDrawablesWithIntrinsicBounds(
                villager.birthday.season.getImageId(activity as MainActivity),
                0, 0, 0
        )

        giftReactionRepository.getGiftReactionsByVillagerName(villager.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { g ->
                    giftReactions.clear()
                    giftReactions.addAll(g)
                    setupAdapter()
                }
    }

    private fun setupAdapter() {
        val list: MutableList<Any> = mutableListOf()
        for (reaction: Reaction in Reaction.values().asList()) {
            list.add(reaction)
            val filteredGiftReactions = giftReactions.filter { it.reaction == reaction }.sortedBy { it.itemName }
            list.addAll(filteredGiftReactions)
        }

        villager_recycler_view.adapter = GiftReactionsAdapter(list)
        val layoutManager = GridLayoutManager(activity, 8)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // getSpanSize should return number of spans item should take up
                return if (list[position] is Reaction) 8 else 1
            }
        }

        villager_recycler_view.layoutManager = layoutManager
    }

    companion object {
        private const val VILLAGER = "VILLAGER"

        fun newInstance(villager: Villager): VillagerFragment {
            val villagerFragment = VillagerFragment()
            val arguments = Bundle()
            arguments.putParcelable(VILLAGER, villager)
            villagerFragment.arguments = arguments
            return villagerFragment
        }
    }
}
