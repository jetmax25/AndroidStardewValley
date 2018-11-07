package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.MuseumItemLocationsAdapter
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.Artifact
import com.pickledgames.stardewvalleyguide.models.LostBook
import com.pickledgames.stardewvalleyguide.models.Mineral
import com.pickledgames.stardewvalleyguide.models.MuseumItem
import kotlinx.android.synthetic.main.fragment_museum_item.*
import kotlinx.android.synthetic.main.header_item.*
import kotlinx.android.synthetic.main.information_museum_item.*
import javax.inject.Inject

class MuseumItemFragment : InnerBaseFragment() {

    @Inject lateinit var analyticsManager: AnalyticsManager
    private lateinit var museumItem: MuseumItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_museum_item
        // TODO: Ads?
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (arguments != null) {
            val museumItemType = arguments?.getString(MUSEUM_ITEM_TYPE)
            when (museumItemType) {
                ARTIFACT -> museumItem = arguments?.getParcelable(ARTIFACT) as Artifact
                LOST_BOOK -> museumItem = arguments?.getParcelable(LOST_BOOK) as LostBook
                MINERAL -> museumItem = arguments?.getParcelable(MINERAL) as Mineral
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun setup() {
        header_item_left_image_view?.setImageResource(museumItem.getImageId(activity as MainActivity))
        header_item_left_image_view?.contentDescription = museumItem.name
        header_item_name_text_view?.text = museumItem.name
        header_item_right_image_view?.setImageResource(museumItem.getImageId(activity as MainActivity))
        header_item_right_image_view?.contentDescription = museumItem.name
        description_text_view?.text = museumItem.description

        when (museumItem) {
            is Artifact -> {
                val artifact = museumItem as Artifact
                price_text_view?.text = String.format(getString(R.string.price_template), artifact.price)
                museum_item_locations_recycler_view?.adapter = MuseumItemLocationsAdapter(artifact.locations)
                museum_item_locations_recycler_view?.layoutManager = LinearLayoutManager(activity)
                museum_item_locations_recycler_view?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            }
            is LostBook -> {
                price_text_view?.visibility = View.GONE
                museum_item_locations_header_text_view?.visibility = View.GONE
                museum_item_locations_recycler_view?.visibility = View.GONE
            }
            is Mineral -> {
                val mineral = (museumItem as Mineral)
                price_text_view?.text = String.format(getString(R.string.price_template), mineral.price)
                if (mineral.minMineLevel != -1 && mineral.maxMineLevel != -1) {
                    mine_levels_text_view?.visibility = View.VISIBLE
                    mine_levels_text_view?.text = String.format(getString(R.string.mine_levels_template), mineral.minMineLevel, mineral.maxMineLevel)
                }
                museum_item_locations_recycler_view?.adapter = MuseumItemLocationsAdapter(mineral.locations)
                museum_item_locations_recycler_view?.layoutManager = LinearLayoutManager(activity)
                museum_item_locations_recycler_view?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            }
        }

        analyticsManager.logEvent("Museum Detail", mapOf("Item Name" to museumItem.name))
    }

    companion object {
        private const val MUSEUM_ITEM_TYPE = "MUSEUM_ITEM_TYPE"
        private const val ARTIFACT = "ARTIFACT"
        private const val LOST_BOOK = "LOST_BOOK"
        private const val MINERAL = "MINERAL"

        fun newInstance(artifact: Artifact): MuseumItemFragment {
            val arguments = Bundle()
            arguments.putString(MUSEUM_ITEM_TYPE, ARTIFACT)
            arguments.putParcelable(ARTIFACT, artifact)
            return newInstance(arguments)
        }

        fun newInstance(lostBook: LostBook): MuseumItemFragment {
            val arguments = Bundle()
            arguments.putString(MUSEUM_ITEM_TYPE, LOST_BOOK)
            arguments.putParcelable(LOST_BOOK, lostBook)
            return newInstance(arguments)
        }

        fun newInstance(mineral: Mineral): MuseumItemFragment {
            val arguments = Bundle()
            arguments.putString(MUSEUM_ITEM_TYPE, MINERAL)
            arguments.putParcelable(MINERAL, mineral)
            return newInstance(arguments)
        }

        private fun newInstance(arguments: Bundle): MuseumItemFragment {
            val museumItemFragment = MuseumItemFragment()
            museumItemFragment.arguments = arguments
            return museumItemFragment
        }
    }
}
