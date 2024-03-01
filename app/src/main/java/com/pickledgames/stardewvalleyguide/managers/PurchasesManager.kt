package com.pickledgames.stardewvalleyguide.managers

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
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
        .enablePendingPurchases()
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
    private val queryProductDetailsParams =
        QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRO_SKU)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()
    private val queryPurchasesObservable: Single<PurchaseResult> = Single.create<PurchaseResult> {
        val queryPurchaseParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(queryPurchaseParams) { billingResult, purchases ->
            it.onSuccess(
                PurchaseResult(
                    billingResult.responseCode,
                    purchases
                )
            )
        }
    }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    data class PurchaseResult(val responseCode: Int, val purchases: List<Purchase>)

    init {
        billingClient.startConnection(this)
    }

    fun purchaseProVersion(activity: Activity) {
        if (isBillingEnabled) {
            billingClient.queryProductDetailsAsync(queryProductDetailsParams) { _, products ->
                val productDetailsParamsList = products.map { product ->
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .build()
                }

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

                billingClient.launchBillingFlow(activity, billingFlowParams)
            }
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

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        var purchased = false
        if (result.responseCode == BillingResponseCode.OK) {
            for (sku in purchases.orEmpty().flatMap { it.products }) {
                if (sku == PRO_SKU) {
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

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        isBillingEnabled = responseCode == BillingResponseCode.OK
        if (responseCode == BillingResponseCode.OK) {
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

    private fun onQueryPurchases(purchasesResult: PurchaseResult) {
        val purchases = purchasesResult.purchases
        for (product in purchases.flatMap { it.products }) {
            if (product == PRO_SKU) {
                isPro = true
                break
            }
        }

        compositeDisposable.clear()
    }

    private fun onQueryRestoredPurchases(purchasesResult: PurchaseResult) {
        var restored = false
        val purchases = purchasesResult.purchases
        for (product in purchases.flatMap { it.products }) {
            if (product == PRO_SKU) {
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

    override fun onConsumeResponse(billingResult: BillingResult, purchaseToken: String) {
        Log.i(TAG, "onConsumeResponse called with responseCode: ${billingResult.responseCode} for purchaseToken: $purchaseToken.")
    }

    companion object {
        const val TAG = "PurchasesManager"
        val PRO_SKU = if (BuildConfig.DEBUG) "android.test.purchased" else "pro"
    }
}
