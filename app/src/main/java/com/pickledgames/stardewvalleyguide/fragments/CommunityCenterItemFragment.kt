package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.GuidesAdapter
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import kotlinx.android.synthetic.main.fragment_community_center_item.*
import kotlinx.android.synthetic.main.header_item.*
import javax.inject.Inject

class CommunityCenterItemFragment : InnerBaseFragment() {

    private lateinit var communityCenterItem: CommunityCenterItem
    @Inject lateinit var adsManager: AdsManager
    @Inject lateinit var analyticsManager: AnalyticsManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        adsManager.showAdFor(AdsManager.COMMUNITY_CENTER_ITEM_FRAGMENT)
        return inflater.inflate(R.layout.fragment_community_center_item, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            val selectedCommunityCenterItem: CommunityCenterItem? = arguments?.getParcelable(COMMUNITY_CENTER_ITEM)
            if (selectedCommunityCenterItem != null) {
                communityCenterItem = selectedCommunityCenterItem
                setup()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    private fun setup() {
        header_item_name_text_view?.text = communityCenterItem.name
        header_item_image_view?.setImageResource(communityCenterItem.getImageId(activity as MainActivity))
        header_item_image_view?.contentDescription = communityCenterItem.name

        if (communityCenterItem.seasons.contains(Season.Fall)) fall_availability_text_view?.alpha = 1.0f
        if (communityCenterItem.seasons.contains(Season.Winter)) winter_availability_text_view?.alpha = 1.0f
        if (communityCenterItem.seasons.contains(Season.Spring)) spring_availability_text_view?.alpha = 1.0f
        if (communityCenterItem.seasons.contains(Season.Summer)) summer_availability_text_view?.alpha = 1.0f
        if (communityCenterItem.isTravelingMerchant) traveling_merchant_image_view?.alpha = 1.0f

        guides_recycler_view?.adapter = GuidesAdapter(communityCenterItem.guides)
        guides_recycler_view?.layoutManager = LinearLayoutManager(activity)
        guides_recycler_view?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        analyticsManager.logEvent("Community Center Detail", mapOf("Item Name" to communityCenterItem.name))
    }

    companion object {
        private const val COMMUNITY_CENTER_ITEM = "COMMUNITY_CENTER_ITEM"

        fun newInstance(communityCenterItem: CommunityCenterItem): CommunityCenterItemFragment {
            val communityCenterItemFragment = CommunityCenterItemFragment()
            val arguments = Bundle()
            arguments.putParcelable(COMMUNITY_CENTER_ITEM, communityCenterItem)
            communityCenterItemFragment.arguments = arguments
            return communityCenterItemFragment
        }
    }
}
