package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.CommunityCenterItemsAdapter
import com.pickledgames.stardewvalleyguide.models.CommunityCenterBundle
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import com.pickledgames.stardewvalleyguide.repositories.CommunityCenterRepository
import com.pickledgames.stardewvalleyguide.repositories.FarmRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_community_center.*
import kotlinx.android.synthetic.main.header_farm.*
import javax.inject.Inject

class CommunityCenterFragment : BaseFragment(), View.OnClickListener {

    @Inject lateinit var farmRepository: FarmRepository
    @Inject lateinit var communityCenterRepository: CommunityCenterRepository
    private var bundles: MutableList<CommunityCenterBundle> = mutableListOf()
    private var list: MutableList<Any> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_community_center, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onClick(view: View?) {

    }

    private fun setup() {
        header_farm_name_text_view.setOnClickListener {
            (activity as MainActivity).pushFragment(EditFarmsFragment.newInstance())
        }

        farmRepository.getSelectedFarm()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { f ->
                    header_farm_name_text_view.text = String.format(getString(R.string.farm_name_template, f.name))
                }

        communityCenterRepository.getBundles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { b ->
                    bundles.addAll(b)
                    setupCommunityCenterItemsAdapter()
                }
    }

    private fun setupCommunityCenterItemsAdapter() {
        bundles.forEach {
            list.add(it)
            list.addAll(it.items)
        }

        community_center_recycler_view.adapter = CommunityCenterItemsAdapter(list, activity as MainActivity)
        community_center_recycler_view.layoutManager = LinearLayoutManager(activity)
    }

    fun updateCheckedItem(communityCenterItem: CommunityCenterItem, isChecked: Boolean) {

    }

    companion object {
        fun newInstance(): CommunityCenterFragment {
            return CommunityCenterFragment()
        }
    }
}
