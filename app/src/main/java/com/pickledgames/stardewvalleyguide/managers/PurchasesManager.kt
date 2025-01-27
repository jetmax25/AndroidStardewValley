package com.pickledgames.stardewvalleyguide.managers

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.Purchase.PurchaseState
import com.pickledgames.stardewvalleyguide.BuildConfig
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.StardewApp
import dagger.Lazy
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlin.math.pow

class PurchasesManager(
        private val stardewApp: StardewApp,
        private val analyticsManager: Lazy<AnalyticsManager>
) : BillingClientStateListener, PurchasesUpdatedListener, ConsumeResponseListener {

    private var billingClient: BillingClient = BillingClient.newBuilder(stardewApp)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
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
                        .setProductId(NEW_PRO_SKU)
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
            processPurchases(purchases) {
                isPro = true
                purchased = true
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
        processPurchases(purchases) {
            isPro = true
        }

        compositeDisposable.clear()
    }

    private fun onQueryRestoredPurchases(purchasesResult: PurchaseResult) {
        var restored = false
        val purchases = purchasesResult.purchases
        processPurchases(purchases) {
            isPro = true
            restored = true
        }

        compositeDisposable.clear()

        if (restored) {
            analyticsManager.get().logEvent("Restore Succeeded")
        } else {
            analyticsManager.get().logEvent("Restore Failed")
        }
    }

    private fun processPurchases(purchases: List<Purchase>?, onPurchased: () -> Unit) {
        Log.d(TAG, "processPurchases: ${purchases?.size} purchase(s)")

        if (purchases.isNullOrEmpty()) {
            return
        }

        val oneTimeProductPurchases = purchases.filter { purchase ->
            purchase.products.any { sku ->
                sku in setOf(PRO_SKU, NEW_PRO_SKU)
            }
        }

        oneTimeProductPurchases.forEach { purchase ->
            if (purchase.purchaseState == PurchaseState.PURCHASED ) {
                if (purchase.isAcknowledged) {
                    onPurchased()
                } else {
                    acknowledgePurchase(purchase.purchaseToken) {
                        onPurchased()
                    }
                }
            }
        }
    }

    private fun acknowledgePurchase(purchaseToken: String, onSuccess: () -> Unit) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        // Using a background thread to handle retries asynchronously
        Thread {
            var retryCount = 1
            var response = BillingResponse(500)
            var bResult: BillingResult? = null

            while (retryCount <= MAX_RETRY_ATTEMPT) {
                // Call to acknowledge purchase
                billingClient.acknowledgePurchase(params) { billingResult ->
                    Log.i(TAG, "Acknowledge response code: ${billingResult.responseCode}")
                    response = BillingResponse(billingResult.responseCode)
                    bResult = billingResult
                    bResult = billingResult
                }

                // Waiting for acknowledgePurchase to finish
                Thread.sleep(500)

                // Handling the response
                when {
                    response.isOk -> {
                        Log.i(TAG, "Acknowledge success - token: $purchaseToken")
                        onSuccess()
                        return@Thread
                    }

                    response.canFailGracefully -> {
                        Log.i(TAG, "Token $purchaseToken is already owned.")
                        onSuccess()
                        return@Thread
                    }

                    response.isRecoverableError -> {
                        // Retry on recoverable errors
                        val duration = 500L * 2.0.pow(retryCount).toLong()
                        Thread.sleep(duration) // Delay between retries with exponential backoff
                        if (retryCount < MAX_RETRY_ATTEMPT) {
                            Log.i(TAG, "Retrying($retryCount) to acknowledge for token $purchaseToken - code: ${bResult!!.responseCode}, message: ${bResult!!.debugMessage}")
                        }
                    }

                    response.isNonrecoverableError || response.isTerribleFailure -> {
                        Log.e(TAG, "Failed to acknowledge for token $purchaseToken - code: ${bResult!!.responseCode}, message: ${bResult!!.debugMessage}")
                        break
                    }
                }
                retryCount++
            }

            Log.i(TAG, "Failed to acknowledge the purchase!")
        }.start()  // Start the background thread
    }


    override fun onConsumeResponse(billingResult: BillingResult, purchaseToken: String) {
        Log.i(TAG, "onConsumeResponse called with responseCode: ${billingResult.responseCode} for purchaseToken: $purchaseToken.")
    }

    companion object {
        const val TAG = "PurchasesManager"
        val PRO_SKU = if (BuildConfig.DEBUG) "android.test.purchased" else "pro"
        val NEW_PRO_SKU = if (BuildConfig.DEBUG) "android.test.purchased" else "pro_update"
        private const val MAX_RETRY_ATTEMPT = 3
    }
}
