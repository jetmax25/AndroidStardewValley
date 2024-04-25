package com.pickledgames.stardewvalleyguide.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.FishLocationsAdapter
import com.pickledgames.stardewvalleyguide.ads.AdsService
import com.pickledgames.stardewvalleyguide.databinding.FragmentFishBinding
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.Fish
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import io.github.douglasjunior.androidSimpleTooltip.OverlayView
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import javax.inject.Inject

class FishFragment : InnerBaseFragment() {

    @Inject lateinit var adsManager: AdsManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var sharedPreferences: SharedPreferences
    @Inject lateinit var adsService: AdsService
    private lateinit var fish: Fish
    private lateinit var binding: FragmentFishBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layoutId = R.layout.fragment_fish
        if (adsService.areAdsEnabled()) {
            adsManager.showAdFor(AdsManager.FISH_FRAGMENT, requireActivity())
        }
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentFishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (arguments != null) {
            val selectedFish: Fish? = arguments?.getParcelable(FISH)
            if (selectedFish != null) {
                fish = selectedFish
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    @SuppressLint("DefaultLocale")
    override fun setup() {
        with (binding) {
            with (headerItemLayout) {
                headerItemLeftImageView.setImageResource(fish.getImageId(activity as MainActivity))
                headerItemLeftImageView.contentDescription = fish.name
                headerItemNameTextView.text = fish.name
                headerItemRightImageView.setImageResource(fish.getImageId(activity as MainActivity))
                headerItemRightImageView.contentDescription = fish.name
            }

            with (informationFishLayout) {
                descriptionTextView.text = fish.description

                commonRowStatsLayout.statsTypeTextView.text = getString(R.string.common)
                commonRowStatsLayout.statsTypeImageView.setImageResource(fish.getImageId(activity as MainActivity))
                commonRowStatsLayout.statsTypeImageView.contentDescription = fish.name
                commonRowStatsLayout.statsPriceTextView.text = fish.commonStats.price.toString()
                commonRowStatsLayout.statsHealthTextView.text = fish.commonStats.health.toString()
                commonRowStatsLayout.statsEnergyTextView.text = fish.commonStats.energy.toString()

                silverRowStatsLayout.statsTypeTextView.text = getString(R.string.silver)
                silverRowStatsLayout.statsTypeImageView.setImageResource(R.drawable.misc_silver_icon)
                silverRowStatsLayout.statsTypeImageView.contentDescription = getString(R.string.silver)
                silverRowStatsLayout.statsPriceTextView.text = fish.silverStats.price.toString()
                silverRowStatsLayout.statsHealthTextView.text = fish.silverStats.health.toString()
                silverRowStatsLayout.statsEnergyTextView.text = fish.silverStats.energy.toString()

                goldRowStatsLayout.statsTypeTextView.text = getString(R.string.gold)
                goldRowStatsLayout.statsTypeImageView.setImageResource(R.drawable.misc_gold_icon)
                goldRowStatsLayout.statsTypeImageView.contentDescription = getString(R.string.gold)
                goldRowStatsLayout.statsPriceTextView.text = fish.goldStats.price.toString()
                goldRowStatsLayout.statsHealthTextView.text = fish.goldStats.health.toString()
                goldRowStatsLayout.statsEnergyTextView.text = fish.goldStats.energy.toString()

                if (fish.availability.seasons.contains(Season.Fall)) rowSeasonsLayout.fallAvailabilityTextView.alpha = 1.0f
                if (fish.availability.seasons.contains(Season.Winter)) rowSeasonsLayout.winterAvailabilityTextView.alpha = 1.0f
                if (fish.availability.seasons.contains(Season.Spring)) rowSeasonsLayout.springAvailabilityTextView.alpha = 1.0f
                if (fish.availability.seasons.contains(Season.Summer)) rowSeasonsLayout.summerAvailabilityTextView.alpha = 1.0f
            }


            when {
                fish.availability.weather.equals("Any", true) -> {
                    informationFishLayout.sunnyTextView.alpha = 1.0f
                    informationFishLayout.rainyTextView.alpha = 1.0f
                }
                fish.availability.weather.equals("Sunny", true) -> {
                    informationFishLayout.sunnyTextView.alpha = 1.0f
                }
                fish.availability.weather.equals("Rainy", true) -> {
                    informationFishLayout.rainyTextView.alpha = 1.0f
                }
            }

            FragmentUtil.setTimeRangeText(fish.availability.startTime, fish.availability.endTime, informationFishLayout.timeRangeTextView, resources)
            informationFishLayout.fishingLevelTextView.text = String.format(getString(R.string.fishing_level_template, fish.fishingLevel))
            informationFishLayout.behaviorTextView.text = String.format(getString(R.string.behavior_template, fish.behavior.capitalize()))
            informationFishLayout.difficultyTextView.text = String.format(getString(R.string.difficulty_template, fish.difficulty))
            informationFishLayout.xpTextView.text = String.format(getString(R.string.xp_template, fish.xp))

            with (fishLocationsRecyclerView) {
                adapter = FishLocationsAdapter(fish)
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            }

            val shownTooltip = sharedPreferences.getBoolean(FULL_SCREEN_IMAGE_TOOLTIP, false)
            if (fish.isLegendary && !shownTooltip) {
                sharedPreferences.edit().putBoolean(FULL_SCREEN_IMAGE_TOOLTIP, true).apply()
                SimpleTooltip.Builder(context)
                    .anchorView(fishLocationsRecyclerView)
                    .text("Click on image to view full screen")
                    .animated(true)
                    .transparentOverlay(false)
                    .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                    .build()
                    .show()
            }
        }

        analyticsManager.logEvent("Fish Detail", mapOf("Item Name" to fish.name))
    }

    companion object {
        private const val FISH = "FISH"
        private const val FULL_SCREEN_IMAGE_TOOLTIP = "FULL_SCREEN_IMAGE_TOOLTIP"

        fun newInstance(fish: Fish): FishFragment {
            val fishFragment = FishFragment()
            val arguments = Bundle()
            arguments.putParcelable(FISH, fish)
            fishFragment.arguments = arguments
            return fishFragment
        }
    }
}
