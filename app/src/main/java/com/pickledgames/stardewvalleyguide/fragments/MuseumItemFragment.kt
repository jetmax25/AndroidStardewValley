package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.MuseumItemLocationsAdapter
import com.pickledgames.stardewvalleyguide.databinding.FragmentMuseumItemBinding
import com.pickledgames.stardewvalleyguide.managers.AdsManager
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.models.Artifact
import com.pickledgames.stardewvalleyguide.models.LostBook
import com.pickledgames.stardewvalleyguide.models.Mineral
import com.pickledgames.stardewvalleyguide.models.MuseumItem
import javax.inject.Inject

class MuseumItemFragment : InnerBaseFragment() {

    @Inject lateinit var adsManager: AdsManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    private lateinit var museumItem: MuseumItem
    private lateinit var binding: FragmentMuseumItemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_museum_item
        adsManager.showAdFor(AdsManager.MUSEUM_ITEM_FRAGMENT, requireActivity())
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMuseumItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("RemoveExplicitTypeArguments")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (arguments != null) {
            when (arguments?.getString(MUSEUM_ITEM_TYPE)) {
                ARTIFACT -> museumItem = arguments?.getParcelable<Artifact>(ARTIFACT) as Artifact
                LOST_BOOK -> museumItem = arguments?.getParcelable<LostBook>(LOST_BOOK) as LostBook
                MINERAL -> museumItem = arguments?.getParcelable<Mineral>(MINERAL) as Mineral
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun setup() {
        with(binding) {
            headerItemLayout.headerItemLeftImageView.setImageResource(museumItem.getImageId(activity as MainActivity))
            headerItemLayout.headerItemLeftImageView.contentDescription = museumItem.name
            headerItemLayout.headerItemNameTextView.text = museumItem.name
            headerItemLayout.headerItemRightImageView.setImageResource(museumItem.getImageId(activity as MainActivity))
            headerItemLayout.headerItemRightImageView.contentDescription = museumItem.name
            informationMuseumItemLayout.descriptionTextView.text = museumItem.description


            when (museumItem) {
                is Artifact -> {
                    val artifact = museumItem as Artifact
                    informationMuseumItemLayout.priceTextView.text = String.format(getString(R.string.price_template), artifact.price)
                    museumItemLocationsRecyclerView.adapter = MuseumItemLocationsAdapter(artifact.locations)
                    museumItemLocationsRecyclerView.layoutManager = LinearLayoutManager(activity)
                    museumItemLocationsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                }
                is LostBook -> {
                    informationMuseumItemLayout.priceTextView.visibility = View.GONE
                    informationMuseumItemLayout.informationHeaderTextView.visibility = View.GONE
                    museumItemLocationsRecyclerView.visibility = View.GONE
                }
                is Mineral -> {
                    val mineral = (museumItem as Mineral)
                    informationMuseumItemLayout.priceTextView.text = String.format(getString(R.string.price_template), mineral.price)
                    if (mineral.minMineLevel != -1 && mineral.maxMineLevel != -1) {
                        informationMuseumItemLayout.mineLevelsTextView.visibility = View.VISIBLE
                        informationMuseumItemLayout.mineLevelsTextView.text = String.format(getString(R.string.mine_levels_template), mineral.minMineLevel, mineral.maxMineLevel)
                    }
                    museumItemLocationsRecyclerView.adapter = MuseumItemLocationsAdapter(mineral.locations)
                    museumItemLocationsRecyclerView.layoutManager = LinearLayoutManager(activity)
                    museumItemLocationsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                }
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
