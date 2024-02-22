package com.pickledgames.stardewvalleyguide.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.CropStagesAdapter
import com.pickledgames.stardewvalleyguide.databinding.FragmentCropBinding
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.Crop
import com.pickledgames.stardewvalleyguide.views.GridDividerDecoration
import javax.inject.Inject

class CropFragment : InnerBaseFragment() {

    @Inject lateinit var adsManager: AdsManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var sharedPreferences: SharedPreferences
    private lateinit var crop: Crop
    private lateinit var binding: FragmentCropBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_crop
        adsManager.showAdFor(AdsManager.CROP_FRAGMENT, requireActivity() as MainActivity)
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCropBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (arguments != null) {
            val selectedCrop: Crop? = arguments?.getParcelable(CROP)
            if (selectedCrop != null) {
                crop = selectedCrop
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun setup() {
        with(binding) {
            headerItemLayout.headerItemLeftImageView.setImageResource(crop.getImageId(activity as MainActivity))
            headerItemLayout.headerItemLeftImageView.contentDescription = crop.name
            headerItemLayout.headerItemNameTextView.text = crop.name
            headerItemLayout.headerItemRightImageView.setImageResource(crop.getImageId(activity as MainActivity))
            headerItemLayout.headerItemRightImageView.contentDescription = crop.name

            with (informationCropLayout) {
                commonRowStatsLayout.statsTypeTextView.text = getString(R.string.common)
                commonRowStatsLayout.statsTypeImageView.setImageResource(crop.getImageId(activity as MainActivity))
                commonRowStatsLayout.statsTypeImageView.contentDescription = crop.name
                commonRowStatsLayout.statsPriceTextView.text = crop.commonStats.price.toString()
                commonRowStatsLayout.statsHealthTextView.text = crop.commonStats.health.toString()
                commonRowStatsLayout.statsEnergyTextView.text = crop.commonStats.energy.toString()

                silverRowStatsLayout.statsTypeTextView.text = getString(R.string.silver)
                silverRowStatsLayout.statsTypeImageView.setImageResource(R.drawable.misc_silver_icon)
                silverRowStatsLayout.statsTypeImageView.contentDescription = getString(R.string.silver)
                silverRowStatsLayout.statsPriceTextView.text = crop.silverStats.price.toString()
                silverRowStatsLayout.statsHealthTextView.text = crop.silverStats.health.toString()
                silverRowStatsLayout.statsEnergyTextView.text = crop.silverStats.energy.toString()

                goldRowStatsLayout.statsTypeTextView.text = getString(R.string.gold)
                goldRowStatsLayout.statsTypeImageView.setImageResource(R.drawable.misc_gold_icon)
                goldRowStatsLayout.statsTypeImageView.contentDescription = getString(R.string.gold)
                goldRowStatsLayout.statsPriceTextView.text = crop.goldStats.price.toString()
                goldRowStatsLayout.statsHealthTextView.text = crop.goldStats.health.toString()
                goldRowStatsLayout.statsEnergyTextView.text = crop.goldStats.energy.toString()

                if (crop.seasons.contains(Season.Fall)) rowSeasonsLayout.fallAvailabilityTextView.alpha = 1.0f
                if (crop.seasons.contains(Season.Winter)) rowSeasonsLayout.winterAvailabilityTextView.alpha = 1.0f
                if (crop.seasons.contains(Season.Spring)) rowSeasonsLayout.springAvailabilityTextView.alpha = 1.0f
                if (crop.seasons.contains(Season.Summer)) rowSeasonsLayout.summerAvailabilityTextView.alpha = 1.0f
            }

            informationCropLayout.informationCropGoldPerDayTextView.text = String.format(getString(R.string.gold_per_day_template), crop.goldPerDay)
            with (cropStagesRecyclerView) {
                adapter = CropStagesAdapter(crop)
                layoutManager = GridLayoutManager(activity, SPAN_COUNT)
                addItemDecoration(GridDividerDecoration(5, SPAN_COUNT))
            }
        }

        analyticsManager.logEvent("Crop Detail", mapOf("Item Name" to crop.name))
    }

    companion object {
        private const val CROP = "CROP"
        private const val SPAN_COUNT = 4

        fun newInstance(crop: Crop): CropFragment {
            val cropFragment = CropFragment()
            val arguments = Bundle()
            arguments.putParcelable(CROP, crop)
            cropFragment.arguments = arguments
            return cropFragment
        }
    }
}
