package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.FarmsAdapter
import com.pickledgames.stardewvalleyguide.enums.FarmType
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.repositories.FarmRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_farms.*
import kotlinx.android.synthetic.main.loading.*
import javax.inject.Inject

class EditFarmsFragment : InnerBaseFragment() {

    @Inject lateinit var farmRepository: FarmRepository
    private var farms: MutableList<Farm> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        setTitle(R.string.edit_farms)
        return inflater.inflate(R.layout.fragment_edit_farms, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.edit_farms, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_farm -> {
                val farm = Farm("Unnamed", FarmType.Standard)
                val disposable = farmRepository.addFarm(farm)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { _ ->
                            farms.add(farm)
                            edit_farms_recycler_view.adapter?.notifyItemInserted(farms.size - 1)
                        }

                compositeDisposable.add(disposable)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setup() {
        val disposable = farmRepository.getFarms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { f ->
                    farms.addAll(f)
                    edit_farms_recycler_view.visibility = View.VISIBLE
                    loading_container.visibility = View.GONE
                    edit_farms_recycler_view.adapter = FarmsAdapter(farms, activity as MainActivity)
                    edit_farms_recycler_view.layoutManager = LinearLayoutManager(activity)
                    edit_farms_recycler_view.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                }

        compositeDisposable.add(disposable)
    }

    fun updateFarm(farm: Farm?, position: Int) {
        if (farm == null && farms.size == 1) {
            Toast.makeText(activity, R.string.delete_error_message, Toast.LENGTH_SHORT).show()
        } else if (farm == null) {
            val deletedFarm = farms.removeAt(position)
            edit_farms_recycler_view.adapter?.notifyItemRemoved(position)
            farmRepository.deleteFarm(deletedFarm, position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        } else {
            farms[position] = farm
            edit_farms_recycler_view.adapter?.notifyItemChanged(position)
            farmRepository.updateFarm(farm, position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
    }

    companion object {
        fun newInstance(): EditFarmsFragment {
            return EditFarmsFragment()
        }
    }
}
