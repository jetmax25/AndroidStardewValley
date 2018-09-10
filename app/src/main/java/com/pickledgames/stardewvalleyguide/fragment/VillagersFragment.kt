package com.pickledgames.stardewvalleyguide.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.adapter.VillagersAdapter
import com.pickledgames.stardewvalleyguide.model.Villager
import com.pickledgames.stardewvalleyguide.repository.VillagerRepository
import com.pickledgames.stardewvalleyguide.view.GridDividerDecoration
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_villagers.*
import javax.inject.Inject

class VillagersFragment : Fragment() {

    @Inject lateinit var villagerRepository: VillagerRepository
    var villagers: MutableList<Villager> = mutableListOf()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_villagers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    private fun setup() {
        villagerRepository.getVillagers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { v ->
                    villagers.addAll(v)
                    villagers_recycler_view.adapter = VillagersAdapter(villagers)
                    villagers_recycler_view.layoutManager = GridLayoutManager(activity, 3)
                    val offset = activity?.resources?.getDimensionPixelOffset(R.dimen.villagers_grid_layout_offset)
                    if (offset != null) villagers_recycler_view.addItemDecoration(GridDividerDecoration(offset, 3))
                }
    }

    companion object {
        fun newInstance(): VillagersFragment {
            return VillagersFragment()
        }
    }
}
