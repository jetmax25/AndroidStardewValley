package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.repositories.FishRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FishingFragment : BaseFragment() {

    @Inject lateinit var fishRepository: FishRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_fishing
        menuId = R.menu.fishing
        return super.onCreateView(inflater, container, savedInstanceState)
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
