package com.pickledgames.stardewvalleyguide.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.ncapdevi.fragnav.FragNavController
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.fragments.ChecklistsFragment
import com.pickledgames.stardewvalleyguide.fragments.EditFarmsFragment
import com.pickledgames.stardewvalleyguide.fragments.FriendshipsFragment
import com.pickledgames.stardewvalleyguide.fragments.PurchasesFragment
import com.pickledgames.stardewvalleyguide.interfaces.OnFarmUpdatedListener
import com.pickledgames.stardewvalleyguide.misc.PurchaseManager
import com.pickledgames.stardewvalleyguide.models.Farm
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnFarmUpdatedListener {

    private var fragments: List<Fragment> = listOf(
            FriendshipsFragment.newInstance(),
            ChecklistsFragment.newInstance(),
            PurchasesFragment.newInstance()
    )
    private lateinit var fragNavController: FragNavController
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_friendships -> {
                fragNavController.switchTab(FRIENDSHIPS)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_checklists -> {
                fragNavController.switchTab(CHECKLISTS)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_purchases -> {
                fragNavController.switchTab(PURCHASES)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    @Inject lateinit var purchaseManager: PurchaseManager
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun setup(savedInstanceState: Bundle?) {
        val builder = FragNavController.newBuilder(savedInstanceState, supportFragmentManager, R.id.container)
        builder.rootFragments(fragments)
        fragNavController = builder.build()
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        val adRequest = AdRequest.Builder().build()
        banner_ad_view.loadAd(adRequest)
        val disposable = purchaseManager.isProSubject.subscribe {
            banner_ad_view.visibility = if (it) View.GONE else View.VISIBLE
        }

        compositeDisposable.add(disposable)
    }

    fun pushFragment(fragment: Fragment) {
        fragNavController.pushFragment(fragment)
    }

    private fun popFragment(): Boolean {
        return if (fragNavController.isRootFragment) false else fragNavController.popFragment()
    }

    fun changeTab(tabIndex: Int) {
        navigation.selectedItemId = when (tabIndex) {
            FRIENDSHIPS -> R.id.navigation_friendships
            CHECKLISTS -> R.id.navigation_checklists
            PURCHASES -> R.id.navigation_purchases
            else -> throw Exception("Invalid Tab Index")
        }
    }

    override fun onBackPressed() {
        if (!popFragment()) super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
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
        const val FRIENDSHIPS = 0
        const val CHECKLISTS = 1
        const val PURCHASES = 2
    }
}
