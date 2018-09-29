package com.pickledgames.stardewvalleyguide.misc

import android.app.Activity
import android.widget.Toast
import com.android.billingclient.api.*
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.StardewApp
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class PurchaseManager(private val stardewApp: StardewApp) :
        PurchasesUpdatedListener, BillingClientStateListener, PurchaseHistoryResponseListener {

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
    private val flowParams: BillingFlowParams = BillingFlowParams.newBuilder()
            .setSku(PurchaseManager.PRO_SKU)
            .setType(BillingClient.SkuType.INAPP)
            .build()

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
            billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, this)
        } else {
            billingConnectionAttempts = 0
            billingClient.startConnection(this)
            Toast.makeText(stardewApp, R.string.billing_not_enabled, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPurchasesUpdated(@BillingClient.BillingResponse responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK) {
            isPro = purchases?.isNotEmpty() ?: false
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
            billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, this)
        } else {
            initializedSubject.onComplete()
        }
    }

    override fun onPurchaseHistoryResponse(@BillingClient.BillingResponse responseCode: Int, purchases: MutableList<Purchase>) {
        if (responseCode == BillingClient.BillingResponse.OK) {
            for (purchase: Purchase in purchases) {
                if (purchase.sku == PurchaseManager.PRO_SKU) {
                    isPro = true
                    break
                }
            }
        }
        // Call onComplete regardless of whether isPro
        initializedSubject.onComplete()
    }

    companion object {
        // TODO: Update with real SKU
        const val PRO_SKU = "PRO_SKU"
    }
}
