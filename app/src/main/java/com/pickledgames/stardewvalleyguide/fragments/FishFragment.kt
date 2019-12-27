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
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.Fish
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import io.github.douglasjunior.androidSimpleTooltip.OverlayView
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.synthetic.main.fragment_fish.*
import kotlinx.android.synthetic.main.header_item.*
import kotlinx.android.synthetic.main.information_fish.*
import kotlinx.android.synthetic.main.row_seasons.*
import kotlinx.android.synthetic.main.row_stats.view.*
import javax.inject.Inject

class FishFragment : InnerBaseFragment() {

    @Inject lateinit var adsManager: AdsManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var sharedPreferences: SharedPreferences
    private lateinit var fish: Fish

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_fish
        adsManager.showAdFor(AdsManager.FISH_FRAGMENT)
        return super.onCreateView(inflater, container, savedInstanceState)
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
        header_item_left_image_view?.setImageResource(fish.getImageId(activity as MainActivity))
        header_item_left_image_view?.contentDescription = fish.name
        header_item_name_text_view?.text = fish.name
        header_item_right_image_view?.setImageResource(fish.getImageId(activity as MainActivity))
        header_item_right_image_view?.contentDescription = fish.name

        description_text_view?.text = fish.description

        common_row_stats_layout?.stats_type_text_view?.text = getString(R.string.common)
        common_row_stats_layout?.stats_type_image_view?.setImageResource(fish.getImageId(activity as MainActivity))
        common_row_stats_layout?.stats_type_image_view?.contentDescription = fish.name
        common_row_stats_layout?.stats_price_text_view?.text = fish.commonStats.price.toString()
        common_row_stats_layout?.stats_health_text_view?.text = fish.commonStats.health.toString()
        common_row_stats_layout?.stats_energy_text_view?.text = fish.commonStats.energy.toString()

        silver_row_stats_layout?.stats_type_text_view?.text = getString(R.string.silver)
        silver_row_stats_layout?.stats_type_image_view?.setImageResource(R.drawable.misc_silver_icon)
        silver_row_stats_layout?.stats_type_image_view?.contentDescription = getString(R.string.silver)
        silver_row_stats_layout?.stats_price_text_view?.text = fish.silverStats.price.toString()
        silver_row_stats_layout?.stats_health_text_view?.text = fish.silverStats.health.toString()
        silver_row_stats_layout?.stats_energy_text_view?.text = fish.silverStats.energy.toString()

        gold_row_stats_layout?.stats_type_text_view?.text = getString(R.string.gold)
        gold_row_stats_layout?.stats_type_image_view?.setImageResource(R.drawable.misc_gold_icon)
        gold_row_stats_layout?.stats_type_image_view?.contentDescription = getString(R.string.gold)
        gold_row_stats_layout?.stats_price_text_view?.text = fish.goldStats.price.toString()
        gold_row_stats_layout?.stats_health_text_view?.text = fish.goldStats.health.toString()
        gold_row_stats_layout?.stats_energy_text_view?.text = fish.goldStats.energy.toString()

        if (fish.availability.seasons.contains(Season.Fall)) fall_availability_text_view?.alpha = 1.0f
        if (fish.availability.seasons.contains(Season.Winter)) winter_availability_text_view?.alpha = 1.0f
        if (fish.availability.seasons.contains(Season.Spring)) spring_availability_text_view?.alpha = 1.0f
        if (fish.availability.seasons.contains(Season.Summer)) summer_availability_text_view?.alpha = 1.0f
        when {
            fish.availability.weather.equals("Any", true) -> {
                sunny_text_view?.alpha = 1.0f
                rainy_text_view?.alpha = 1.0f
            }
            fish.availability.weather.equals("Sunny", true) -> {
                sunny_text_view?.alpha = 1.0f
            }
            fish.availability.weather.equals("Rainy", true) -> {
                rainy_text_view?.alpha = 1.0f
            }
        }

        FragmentUtil.setTimeRangeText(fish.availability.startTime, fish.availability.endTime, time_range_text_view, resources)
        fishing_level_text_view?.text = String.format(getString(R.string.fishing_level_template, fish.fishingLevel))
        behavior_text_view?.text = String.format(getString(R.string.behavior_template, fish.behavior.capitalize()))
        difficulty_text_view?.text = String.format(getString(R.string.difficulty_template, fish.difficulty))
        xp_text_view?.text = String.format(getString(R.string.xp_template, fish.xp))

        fish_locations_recycler_view?.adapter = FishLocationsAdapter(fish)
        fish_locations_recycler_view?.layoutManager = LinearLayoutManager(activity)
        fish_locations_recycler_view?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        val shownTooltip = sharedPreferences.getBoolean(FULL_SCREEN_IMAGE_TOOLTIP, false)
        if (fish.isLegendary && !shownTooltip) {
            sharedPreferences.edit().putBoolean(FULL_SCREEN_IMAGE_TOOLTIP, true).apply()
            SimpleTooltip.Builder(context)
                    .anchorView(fish_locations_recycler_view)
                    .text("Click on image to view full screen")
                    .animated(true)
                    .transparentOverlay(false)
                    .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                    .build()
                    .show()
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
