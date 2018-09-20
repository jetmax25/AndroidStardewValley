package com.pickledgames.stardewvalleyguide.fragments

import android.content.Context
import android.support.v4.app.Fragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable

open class BaseFragment : Fragment() {

    protected var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
