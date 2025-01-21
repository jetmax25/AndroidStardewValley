package com.pickledgames.stardewvalleyguide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import com.pickledgames.stardewvalleyguide.BuildConfig
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.FragmentPurchasesBinding
import com.pickledgames.stardewvalleyguide.managers.PurchasesManager
import com.pickledgames.stardewvalleyguide.utils.underLineText
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class PurchasesFragment : BaseFragment() {

    @Inject lateinit var purchasesManager: PurchasesManager
    private lateinit var binding: FragmentPurchasesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPurchasesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setup() {
        val disposable = purchasesManager.isProSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.thanksTextView.visibility = if (it) View.VISIBLE else View.GONE
                    binding.purchasesGroup.visibility = if (it) { binding.purchaseButton.clearAnimation(); View.GONE } else View.VISIBLE
                }

        compositeDisposable.add(disposable)

        binding.restorePurchasesButton.underLineText()
        binding.toggleProButton.underLineText()

        startAnimation(binding.purchaseButton)

        binding.purchaseButton.setOnClickListener {
            purchasesManager.purchaseProVersion(activity as MainActivity)
        }

        binding.restorePurchasesButton.setOnClickListener {
            purchasesManager.restorePurchases()
        }

        if (BuildConfig.DEBUG) {
            binding.toggleProButton.visibility = View.VISIBLE
            binding.toggleProButton.setOnClickListener {
                purchasesManager.isPro = !purchasesManager.isPro
            }
        }
    }

    private fun startAnimation(button: Button) {
        val pulseAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.heartbeat)
        button.startAnimation(pulseAnimation)
    }

    companion object {
        fun newInstance(): PurchasesFragment {
            return PurchasesFragment()
        }
    }
}
