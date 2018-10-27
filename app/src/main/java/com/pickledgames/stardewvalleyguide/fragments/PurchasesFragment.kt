package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.managers.PurchasesManager
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_purchases.*
import javax.inject.Inject

class PurchasesFragment : BaseFragment() {

    @Inject lateinit var purchasesManager: PurchasesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutId = R.layout.fragment_purchases
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setup() {
        val disposable = purchasesManager.isProSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    thanks_text_view?.visibility = if (it) View.VISIBLE else View.GONE
                    purchases_group?.visibility = if (it) View.GONE else View.VISIBLE
                }

        compositeDisposable.add(disposable)
        purchase_button?.setOnClickListener {
            purchasesManager.purchaseProVersion(activity as MainActivity)
        }
        restore_purchases_button?.setOnClickListener {
            purchasesManager.restorePurchases()
        }
    }

    companion object {
        fun newInstance(): PurchasesFragment {
            return PurchasesFragment()
        }
    }
}
