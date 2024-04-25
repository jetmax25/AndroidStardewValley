package com.pickledgames.stardewvalleyguide.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.pickledgames.stardewvalleyguide.ads.AdsService
import com.pickledgames.stardewvalleyguide.databinding.ActivityAgeVerificationBinding
import dagger.android.AndroidInjection
import javax.inject.Inject

class AgeVerificationActivity : AppCompatActivity() {

    @Inject lateinit var adsService: AdsService
    private lateinit var binding: ActivityAgeVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAgeVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.ageVerificationButton.setOnClickListener {
            binding.ageVerificationEditText.text?.toString()?.toIntOrNull()?.let { age ->
                val isForChildren = age < 18
                onVerifyAgeClicked(isForChildren)
            }
        }
        binding.ageVerificationEditText.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.toIntOrNull()?.let { age ->
                binding.ageVerificationButton.isEnabled = age in 1..100
            } ?: run {
                binding.ageVerificationButton.isEnabled = false
            }
        }

        binding.ageVerificationEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun onVerifyAgeClicked(isForChildren: Boolean) {
        adsService.verifyAge()
        initializeAds(isForChildren)
        goToMainActivity()
    }

    private fun initializeAds(isForChildren: Boolean) {
        MobileAds.initialize(this) {}
        val requestConfiguration = if (isForChildren) {
            RequestConfiguration.Builder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
                .build()
        } else {
            RequestConfiguration.Builder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE)
                .build()
        }
        MobileAds.setRequestConfiguration(requestConfiguration)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}