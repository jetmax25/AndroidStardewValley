package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.GuidesAdapter
import com.pickledgames.stardewvalleyguide.ads.AdsService
import com.pickledgames.stardewvalleyguide.databinding.FragmentCommunityCenterItemBinding
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import javax.inject.Inject

class CommunityCenterItemFragment : InnerBaseFragment() {

    @Inject lateinit var adsManager: AdsManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var adsService: AdsService
    private lateinit var communityCenterItem: CommunityCenterItem
    private lateinit var binding: FragmentCommunityCenterItemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        if (adsService.areAdsEnabled()) {
            adsManager.showAdFor(AdsManager.COMMUNITY_CENTER_ITEM_FRAGMENT, requireActivity())
        }
        binding = FragmentCommunityCenterItemBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (arguments != null) {
            val selectedCommunityCenterItem: CommunityCenterItem? = arguments?.getParcelable(COMMUNITY_CENTER_ITEM)
            if (selectedCommunityCenterItem != null) {
                communityCenterItem = selectedCommunityCenterItem
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun setup() {
        binding.headerItemLayout.headerItemLeftImageView.setImageResource(communityCenterItem.getImageId(activity as MainActivity))
        binding.headerItemLayout.headerItemLeftImageView.contentDescription = communityCenterItem.name
        binding.headerItemLayout.headerItemNameTextView.text = communityCenterItem.name
        binding.headerItemLayout.headerItemRightImageView.setImageResource(communityCenterItem.getImageId(activity as MainActivity))
        binding.headerItemLayout.headerItemRightImageView.contentDescription = communityCenterItem.name

        if (communityCenterItem.seasons.contains(Season.Fall)) binding.rowSeasonsLayout.fallAvailabilityTextView.alpha = 1.0f
        if (communityCenterItem.seasons.contains(Season.Winter)) binding.rowSeasonsLayout.winterAvailabilityTextView.alpha = 1.0f
        if (communityCenterItem.seasons.contains(Season.Spring)) binding.rowSeasonsLayout.springAvailabilityTextView.alpha = 1.0f
        if (communityCenterItem.seasons.contains(Season.Summer)) binding.rowSeasonsLayout.summerAvailabilityTextView.alpha = 1.0f
        if (communityCenterItem.isTravelingMerchant) binding.travelingMerchantImageView.alpha = 1.0f

        binding.guidesRecyclerView.adapter = GuidesAdapter(communityCenterItem.guides)
        binding.guidesRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.guidesRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

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
