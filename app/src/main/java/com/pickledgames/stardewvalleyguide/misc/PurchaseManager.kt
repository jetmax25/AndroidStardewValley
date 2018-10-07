package com.pickledgames.stardewvalleyguide.misc

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.pickledgames.stardewvalleyguide.BuildConfig
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.StardewApp
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class PurchaseManager(private val stardewApp: StardewApp) :
        BillingClientStateListener, PurchasesUpdatedListener, ConsumeResponseListener {

    private var billingClient: BillingClient = BillingClient.newBuilder(stardewApp)
            .setListener(this)
            .build()
    private var billingConnectionAttempts: Int = 0
    private var isPro: Boolean = false
        set(value) {
            field = value
            isProSubject.onNext(value)
        }
    // Used to tell SplashActivity when to transition
    var initializedSubject: PublishSubject<Any> = PublishSubject.create()
    var isProSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private var isBillingEnabled: Boolean = false
    private val flowParams: BillingFlowParams = BillingFlowParams.newBuilder()
            .setSku(PurchaseManager.PRO_SKU)
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
        } else {
            billingConnectionAttempts = 0
            billingClient.startConnection(this)
            Toast.makeText(stardewApp, R.string.billing_not_enabled, Toast.LENGTH_LONG).show()
        }
    }

    fun restorePurchases() {
        if (isBillingEnabled) {
            val disposable = queryPurchasesObservable.subscribe { pr -> onQueryPurchases(pr) }
            compositeDisposable.add(disposable)
        } else {
            billingConnectionAttempts = 0
            billingClient.startConnection(this)
            Toast.makeText(stardewApp, R.string.billing_not_enabled, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPurchasesUpdated(@BillingClient.BillingResponse responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK) {
            for (purchase: Purchase in purchases.orEmpty()) {
                if (purchase.sku == PurchaseManager.PRO_SKU) {
                    isPro = true
                    break
                }
            }
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
        for (purchase: Purchase in purchasesResult.purchasesList) {
            if (purchase.sku == PurchaseManager.PRO_SKU) {
                isPro = true
                break
            }
        }

        compositeDisposable.clear()
    }

    override fun onConsumeResponse(@BillingClient.BillingResponse responseCode: Int, purchaseToken: String?) {
        Log.i(TAG, "onConsumeResponse called with responseCode: $responseCode for purchaseToken: $purchaseToken.")
    }

    companion object {
        const val TAG = "PurchaseManager"
        val PRO_SKU = if (BuildConfig.DEBUG) "android.test.purchased" else "pro"
    }
}
