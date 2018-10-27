package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.*
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.models.Fish
import com.pickledgames.stardewvalleyguide.repositories.FarmRepository
import com.pickledgames.stardewvalleyguide.repositories.FishRepository
import com.pickledgames.stardewvalleyguide.utils.FragmentUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_fishing.*
import javax.inject.Inject

class FishingFragment : BaseFragment(), SearchView.OnQueryTextListener {

    @Inject lateinit var farmRepository: FarmRepository
    @Inject lateinit var fishRepository: FishRepository
    private lateinit var farm: Farm
    private var fishes: MutableList<Fish> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_fishing
        menuId = R.menu.fishing
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val searchMenuItem = menu?.findItem(R.id.fishing_search)
        FragmentUtil.setupSearchView(searchMenuItem, this, View.OnFocusChangeListener { _, b ->
            fishing_header_group?.visibility = if (b) View.GONE else View.VISIBLE
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.community_center_edit_farms -> {
                (activity as MainActivity).pushFragment(EditFarmsFragment.newInstance())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return false
    }

    override fun setup() {
        val disposable = fishRepository.getFishes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { fishes ->
                    println(fishes.size)
                }

        compositeDisposable.add(disposable)
    }

    companion object {
        fun newInstance(): FishingFragment {
            return FishingFragment()
        }
    }
}
