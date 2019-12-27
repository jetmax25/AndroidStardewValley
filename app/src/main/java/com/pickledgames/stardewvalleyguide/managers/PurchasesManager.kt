package com.pickledgames.stardewvalleyguide.managers

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.pickledgames.stardewvalleyguide.BuildConfig
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.StardewApp
import dagger.Lazy
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class PurchasesManager(
        private val stardewApp: StardewApp,
        private val analyticsManager: Lazy<AnalyticsManager>
) : BillingClientStateListener, PurchasesUpdatedListener, ConsumeResponseListener {

    private var billingClient: BillingClient = BillingClient.newBuilder(stardewApp)
            .setListener(this)
            .build()
    private var billingConnectionAttempts: Int = 0
    var isPro: Boolean = false
        set(value) {
            field = value
            isProSubject.onNext(value)
        }
    // Used to tell SplashActivity when to transition
    var initializedSubject: PublishSubject<Any> = PublishSubject.create()
    var isProSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private var isBillingEnabled: Boolean = false
    @Suppress("DEPRECATION")
    private val flowParams: BillingFlowParams = BillingFlowParams.newBuilder()
            .setSku(PRO_SKU)
            .setType(BillingClient.SkuType.INAPP)
            .build()
    private val queryPurchasesObservable: Single<Purchase.PurchasesResult> = Single.create<Purchase.PurchasesResult> {
        val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        it.onSuccess(purchasesResult)
    }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        billingClient.startConnection(this)
    }

    fun purchaseProVersion(activity: Activity) {
        if (isBillingEnabled) {
            billingClient.launchBillingFlow(activity, flowParams)
            analyticsManager.get().logEvent("Purchase Clicked")
        } else {
            billingConnectionAttempts = 0
            billingClient.startConnection(this)
            Toast.makeText(stardewApp, R.string.billing_not_enabled, Toast.LENGTH_LONG).show()
        }
    }

    fun restorePurchases() {
        if (isBillingEnabled) {
            val disposable = queryPurchasesObservable.subscribe { pr -> onQueryRestoredPurchases(pr) }
            compositeDisposable.add(disposable)
            analyticsManager.get().logEvent("Restore Clicked")
        } else {
            billingConnectionAttempts = 0
            billingClient.startConnection(this)
            Toast.makeText(stardewApp, R.string.billing_not_enabled, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPurchasesUpdated(@BillingClient.BillingResponse responseCode: Int, purchases: MutableList<Purchase>?) {
        var purchased = false
        if (responseCode == BillingClient.BillingResponse.OK) {
            for (purchase: Purchase in purchases.orEmpty()) {
                if (purchase.sku == PRO_SKU) {
                    isPro = true
                    purchased = true
                    break
                }
            }
        }

        if (purchased) {
            analyticsManager.get().logEvent("Purchase Succeeded")
        } else {
            analyticsManager.get().logEvent("Purchase Canceled")
        }
    }

    override fun onBillingServiceDisconnected() {
        if (billingConnectionAttempts < 3) {
            billingClient.startConnection(this)
            billingConnectionAttempts++
        } else {
            isBillingEnabled = false
            initializedSubject.onComplete()
        }
    }

    override fun onBillingSetupFinished(@BillingClient.BillingResponse responseCode: Int) {
        isBillingEnabled = responseCode == BillingClient.BillingResponse.OK
        if (responseCode == BillingClient.BillingResponse.OK) {
            val disposable = queryPurchasesObservable.subscribe({ pr ->
                onQueryPurchases(pr)
                initializedSubject.onComplete()
            }, { error ->
                Log.e(TAG, error.message, error)
                initializedSubject.onComplete()
            })
            compositeDisposable.add(disposable)
        } else {
            initializedSubject.onComplete()
        }
    }

    private fun onQueryPurchases(purchasesResult: Purchase.PurchasesResult) {
        val purchases = purchasesResult.purchasesList ?: emptyList()
        for (purchase: Purchase in purchases) {
            if (purchase.sku == PRO_SKU) {
                isPro = true
                break
            }
        }

        compositeDisposable.clear()
    }

    private fun onQueryRestoredPurchases(purchasesResult: Purchase.PurchasesResult) {
        var restored = false
        val purchases = purchasesResult.purchasesList ?: emptyList()
        for (purchase: Purchase in purchases) {
            if (purchase.sku == PRO_SKU) {
                isPro = true
                restored = true
                break
            }
        }

        compositeDisposable.clear()

        if (restored) {
            analyticsManager.get().logEvent("Restore Succeeded")
        } else {
            analyticsManager.get().logEvent("Restore Failed")
        }
    }

    override fun onConsumeResponse(@BillingClient.BillingResponse responseCode: Int, purchaseToken: String?) {
        Log.i(TAG, "onConsumeResponse called with responseCode: $responseCode for purchaseToken: $purchaseToken.")
    }

    companion object {
        const val TAG = "PurchasesManager"
        val PRO_SKU = if (BuildConfig.DEBUG) "android.test.purchased" else "pro"
    }
}
