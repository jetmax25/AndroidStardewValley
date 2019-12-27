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
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.Crop
import com.pickledgames.stardewvalleyguide.views.GridDividerDecoration
import kotlinx.android.synthetic.main.fragment_crop.*
import kotlinx.android.synthetic.main.header_item.*
import kotlinx.android.synthetic.main.information_crop.*
import kotlinx.android.synthetic.main.row_seasons.*
import kotlinx.android.synthetic.main.row_stats.view.*
import javax.inject.Inject

class CropFragment : InnerBaseFragment() {

    @Inject lateinit var adsManager: AdsManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var sharedPreferences: SharedPreferences
    private lateinit var crop: Crop

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_crop
        adsManager.showAdFor(AdsManager.CROP_FRAGMENT)
        return super.onCreateView(inflater, container, savedInstanceState)
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
        header_item_left_image_view?.setImageResource(crop.getImageId(activity as MainActivity))
        header_item_left_image_view?.contentDescription = crop.name
        header_item_name_text_view?.text = crop.name
        header_item_right_image_view?.setImageResource(crop.getImageId(activity as MainActivity))
        header_item_right_image_view?.contentDescription = crop.name

        common_row_stats_layout?.stats_type_text_view?.text = getString(R.string.common)
        common_row_stats_layout?.stats_type_image_view?.setImageResource(crop.getImageId(activity as MainActivity))
        common_row_stats_layout?.stats_type_image_view?.contentDescription = crop.name
        common_row_stats_layout?.stats_price_text_view?.text = crop.commonStats.price.toString()
        common_row_stats_layout?.stats_health_text_view?.text = crop.commonStats.health.toString()
        common_row_stats_layout?.stats_energy_text_view?.text = crop.commonStats.energy.toString()

        silver_row_stats_layout?.stats_type_text_view?.text = getString(R.string.silver)
        silver_row_stats_layout?.stats_type_image_view?.setImageResource(R.drawable.misc_silver_icon)
        silver_row_stats_layout?.stats_type_image_view?.contentDescription = getString(R.string.silver)
        silver_row_stats_layout?.stats_price_text_view?.text = crop.silverStats.price.toString()
        silver_row_stats_layout?.stats_health_text_view?.text = crop.silverStats.health.toString()
        silver_row_stats_layout?.stats_energy_text_view?.text = crop.silverStats.energy.toString()

        gold_row_stats_layout?.stats_type_text_view?.text = getString(R.string.gold)
        gold_row_stats_layout?.stats_type_image_view?.setImageResource(R.drawable.misc_gold_icon)
        gold_row_stats_layout?.stats_type_image_view?.contentDescription = getString(R.string.gold)
        gold_row_stats_layout?.stats_price_text_view?.text = crop.goldStats.price.toString()
        gold_row_stats_layout?.stats_health_text_view?.text = crop.goldStats.health.toString()
        gold_row_stats_layout?.stats_energy_text_view?.text = crop.goldStats.energy.toString()

        if (crop.seasons.contains(Season.Fall)) fall_availability_text_view?.alpha = 1.0f
        if (crop.seasons.contains(Season.Winter)) winter_availability_text_view?.alpha = 1.0f
        if (crop.seasons.contains(Season.Spring)) spring_availability_text_view?.alpha = 1.0f
        if (crop.seasons.contains(Season.Summer)) summer_availability_text_view?.alpha = 1.0f

        information_crop_gold_per_day_text_view.text = String.format(getString(R.string.gold_per_day_template), crop.goldPerDay)

        crop_stages_recycler_view?.adapter = CropStagesAdapter(crop)
        crop_stages_recycler_view?.layoutManager = GridLayoutManager(activity, SPAN_COUNT)
        crop_stages_recycler_view?.addItemDecoration(GridDividerDecoration(5, SPAN_COUNT))

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
