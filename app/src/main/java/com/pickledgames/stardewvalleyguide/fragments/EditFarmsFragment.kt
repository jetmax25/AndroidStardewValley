package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.adapters.FarmsAdapter
import com.pickledgames.stardewvalleyguide.enums.FarmType
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.managers.PurchasesManager
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.repositories.FarmRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_farms.*
import kotlinx.android.synthetic.main.loading.*
import javax.inject.Inject

class EditFarmsFragment : InnerBaseFragment() {

    @Inject lateinit var farmRepository: FarmRepository
    @Inject lateinit var purchasesManager: PurchasesManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    private var farms: MutableList<Farm> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_edit_farms
        menuId = R.menu.edit_farms
        setTitle(R.string.edit_farms)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val addFarmMenuItem = menu.findItem(R.id.add_farm)
        addFarmMenuItem?.isVisible = purchasesManager.isPro
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_farm -> {
                val farm = Farm("Unnamed", FarmType.Standard)
                val disposable = farmRepository.addFarm(farm)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            farms.add(farm)
                            edit_farms_recycler_view?.adapter?.notifyItemInserted(farms.size - 1)
                            analyticsManager.logEvent("Farm Added", mapOf("Name" to farm.name))
                        }, { it.printStackTrace() })

                compositeDisposable.add(disposable)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setup() {
        if (farms.isNotEmpty()) {
            setupAdapter()
        } else {
            val farmDisposable = farmRepository.getFarms()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { f ->
                        farms.addAll(f)
                        setupAdapter()
                    }

            compositeDisposable.add(farmDisposable)
        }

        go_pro_text_view?.setOnClickListener {
            (activity as MainActivity).changeTab(MainActivity.PURCHASES)
        }

        // Always subscribe to isProSubject
        val isProDisposable = purchasesManager.isProSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    go_pro_text_view?.visibility = if (it) View.GONE else View.VISIBLE
                    (activity as MainActivity).invalidateOptionsMenu()
                }

        if (purchasesManager.isPro) {
            go_pro_text_view?.visibility = View.GONE
            (activity as MainActivity).invalidateOptionsMenu()
        }

        compositeDisposable.add(isProDisposable)
    }

    private fun setupAdapter() {
        edit_farms_recycler_view?.visibility = View.VISIBLE
        loading_container?.visibility = View.GONE
        edit_farms_recycler_view?.adapter = FarmsAdapter(farms, activity as MainActivity)
        edit_farms_recycler_view?.layoutManager = LinearLayoutManager(activity)
        edit_farms_recycler_view?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    fun updateFarm(farm: Farm?, position: Int) {
        if (position < 0 || position >= farms.size) return

        if (farm == null && farms.size == 1) {
            Toast.makeText(activity, R.string.delete_error_message, Toast.LENGTH_LONG).show()
        } else if (farm == null) {
            val deletedFarm = farms.removeAt(position)
            edit_farms_recycler_view?.adapter?.notifyItemRemoved(position)
            farmRepository.deleteFarm(deletedFarm, position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()

            analyticsManager.logEvent("Farm Deleted", mapOf("Name" to deletedFarm.name))
        } else {
            farms[position] = farm
            edit_farms_recycler_view?.adapter?.notifyItemChanged(position)
            farmRepository.updateFarm(farm, position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()

            analyticsManager.logEvent("Farm Edited", mapOf("Name" to farm.name))
        }
    }

    companion object {
        fun newInstance(): EditFarmsFragment {
            return EditFarmsFragment()
        }
    }
}
