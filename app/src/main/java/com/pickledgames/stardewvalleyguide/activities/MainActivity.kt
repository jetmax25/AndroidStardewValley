package com.pickledgames.stardewvalleyguide.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.ncapdevi.fragnav.FragNavController
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.fragments.ChecklistsFragment
import com.pickledgames.stardewvalleyguide.fragments.CommunityCenterFragment
import com.pickledgames.stardewvalleyguide.fragments.EditFarmsFragment
import com.pickledgames.stardewvalleyguide.fragments.FriendshipsFragment
import com.pickledgames.stardewvalleyguide.interfaces.OnFarmUpdatedListener
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.CommunityCenterItem
import com.pickledgames.stardewvalleyguide.models.Farm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnFarmUpdatedListener, OnItemCheckedListener {

    private var fragments: List<Fragment> = listOf(
            FriendshipsFragment.newInstance(),
            ChecklistsFragment.newInstance()
    )
    private lateinit var fragNavController: FragNavController
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_friendships -> {
                fragNavController.switchTab(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_checklists -> {
                fragNavController.switchTab(1)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup(savedInstanceState)
    }

    private fun setup(savedInstanceState: Bundle?) {
        val builder = FragNavController.newBuilder(savedInstanceState, supportFragmentManager, R.id.container)
        builder.rootFragments(fragments)
        fragNavController = builder.build()
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    fun pushFragment(fragment: Fragment) {
        fragNavController.pushFragment(fragment)
    }

    private fun popFragment(): Boolean {
        return if (fragNavController.isRootFragment) false else fragNavController.popFragment()
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

    override fun onItemChecked(communityCenterItem: CommunityCenterItem, isChecked: Boolean) {
        val fragment = fragNavController.currentFrag
        if (fragment != null && fragment is CommunityCenterFragment) {
            fragment.updateCheckedItem(communityCenterItem, isChecked)
        }
    }
}
