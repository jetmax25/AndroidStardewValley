package com.pickledgames.stardewvalleyguide.fragments

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.view.*
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.misc.PurchaseManager
import kotlinx.android.synthetic.main.fragment_purchases.*
import javax.inject.Inject

class PurchasesFragment : BaseFragment() {

    @Inject lateinit var purchaseManager: PurchaseManager
    private val builder = CustomTabsIntent.Builder()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_purchases, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    private fun setup() {
        val disposable = purchaseManager.isProSubject
                .subscribe {
                    thanks_text_view.visibility = if (it) View.VISIBLE else View.GONE
                    purchases_group.visibility = if (it) View.GONE else View.VISIBLE
                }

        compositeDisposable.add(disposable)
        purchase_button.setOnClickListener {
            purchaseManager.purchaseProVersion(activity as MainActivity)
        }
        restore_purchases_button.setOnClickListener {
            purchaseManager.restorePurchases()
        }

        builder.setToolbarColor(ContextCompat.getColor(activity as MainActivity, R.color.colorPrimary))
        about_heifer_button.setOnClickListener {
            val url = "https://www.heifer.org/"
            builder.build().launchUrl(activity, Uri.parse(url))
        }
        direct_donation_button.setOnClickListener {
            val url = "https://www.heifer.org/campaign/end-hunger-poverty-donation.html"
            builder.build().launchUrl(activity, Uri.parse(url))
        }
    }

    companion object {
        fun newInstance(): PurchasesFragment {
            return PurchasesFragment()
        }
    }
}
