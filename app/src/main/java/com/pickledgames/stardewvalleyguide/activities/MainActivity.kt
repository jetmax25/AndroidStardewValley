package com.pickledgames.stardewvalleyguide.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ncapdevi.fragnav.FragNavController
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.ads.AdsService
import com.pickledgames.stardewvalleyguide.databinding.ActivityMainBinding
import com.pickledgames.stardewvalleyguide.fragments.*
import com.pickledgames.stardewvalleyguide.interfaces.OnFarmUpdatedListener
import com.pickledgames.stardewvalleyguide.managers.AnalyticsManager
import com.pickledgames.stardewvalleyguide.managers.LoginManager
import com.pickledgames.stardewvalleyguide.managers.PurchasesManager
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.utils.runWithDelay
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.Instant
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnFarmUpdatedListener {

    @Inject lateinit var purchasesManager: PurchasesManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var loginManager: LoginManager
    @Inject lateinit var adsService: AdsService
    private var fragments: List<Fragment> = listOf(
            FriendshipsFragment.newInstance(),
            ChecklistsFragment.newInstance(),
            CropsFragment.newInstance(),
            PurchasesFragment.newInstance()
    )
    private lateinit var fragNavController: FragNavController
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_friendships -> {
                analyticsManager.logEvent("Tab Opened: Friendships")
                fragNavController.switchTab(FRIENDSHIPS)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_checklists -> {
                analyticsManager.logEvent("Tab Opened: Checklists")
                fragNavController.switchTab(CHECKLISTS)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_crops -> {
                analyticsManager.logEvent("Tab Opened: Crops")
                fragNavController.switchTab(CROPS)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_purchases -> {
                analyticsManager.logEvent("Tab Opened: Purchases")
                fragNavController.switchTab(PURCHASES)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup(savedInstanceState)
        if (adsService.areAdsEnabled()) {
            binding.bannerAdView.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragNavController.onSaveInstanceState(outState)
    }

    private fun setup(savedInstanceState: Bundle?) {
        fragNavController = FragNavController(supportFragmentManager, R.id.container)
        fragNavController.rootFragments = fragments
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)

        binding.navigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        if (purchasesManager.isPro) {
            binding.navigation?.menu?.removeItem(R.id.navigation_purchases)
            binding.bannerAdView?.visibility = View.GONE
        } else {
            val adRequest = AdRequest.Builder().build()
            binding.bannerAdView?.loadAd(adRequest)

            val disposable = purchasesManager.isProSubject
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        binding.bannerAdView?.visibility = if (it) View.GONE else View.VISIBLE
                    }

            compositeDisposable.add(disposable)
        }

        if (loginManager.numberOfLogins == 0) {
            loginManager.firstLogin = Instant.now()
        }

        if (loginManager.shouldShowReview) {
            analyticsManager.logEvent("Requesting Review")
            AlertDialog.Builder(this)
                    .setMessage(R.string.play_store_review_message)
                    .setPositiveButton(R.string.absolutely) { _, _ ->
                        loginManager.reviewed = true
                        openPlayStore()
                        analyticsManager.logEvent("Agreed To Review")
                    }
                    .setNeutralButton(R.string.later) { _, _ ->
                        analyticsManager.logEvent("Agreed To Review Later")
                    }
                    .setNegativeButton(R.string.nope) { _, _ ->
                        loginManager.declinedReview = true
                        analyticsManager.logEvent("Declined Review")
                    }
                    .create()
                    .show()
        }

        loginManager.lastLogin = Instant.now()
        loginManager.numberOfLogins++
        analyticsManager.logEvent("Opened App")

        showPurchaseDialog()
    }

    private fun openPlayStore() {
        val uri = Uri.parse("market://details?id=${this.packageName}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun pushFragment(fragment: Fragment) {
        fragNavController.pushFragment(fragment)
    }

    private fun popFragment(): Boolean {
        return if (fragNavController.isRootFragment) false else fragNavController.popFragment()
    }

    private fun showPurchaseDialog() {
        runWithDelay(3000) {
            PurchaseDialogFragment().show(supportFragmentManager, null)
        }
    }


    fun changeTab(tabIndex: Int) {
        binding.navigation.selectedItemId = when (tabIndex) {
            FRIENDSHIPS -> R.id.navigation_friendships
            CHECKLISTS -> R.id.navigation_checklists
            PURCHASES -> R.id.navigation_purchases
            else -> throw Exception("Invalid Tab Index")
        }
    }

    override fun onBackPressed() {
        if (!popFragment()) super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFarmUpdated(farm: Farm?, position: Int) {
        val fragment = fragNavController.currentFrag
        if (fragment != null && fragment is EditFarmsFragment) {
            fragment.updateFarm(farm, position)
        }
    }

    companion object {
        const val FRIENDSHIPS: Int = FragNavController.TAB1
        const val CHECKLISTS: Int = FragNavController.TAB2
        const val CROPS: Int = FragNavController.TAB3
        const val PURCHASES: Int = FragNavController.TAB4
    }
}
