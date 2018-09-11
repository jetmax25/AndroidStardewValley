package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.*
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.models.Villager
import kotlinx.android.synthetic.main.profile_villager.*

class VillagerFragment : InnerFragment(), SearchView.OnQueryTextListener {

    lateinit var villager: Villager

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
