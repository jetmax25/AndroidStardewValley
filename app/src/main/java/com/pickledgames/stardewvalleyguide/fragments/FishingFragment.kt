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
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_fishing, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    private fun setup() {
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
