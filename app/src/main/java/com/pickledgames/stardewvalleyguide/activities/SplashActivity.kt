package com.pickledgames.stardewvalleyguide.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.ads.AdsService
import com.pickledgames.stardewvalleyguide.managers.PurchasesManager
import com.pickledgames.stardewvalleyguide.utils.runWithDelay
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject lateinit var purchasesManager: PurchasesManager
    @Inject lateinit var adsService: AdsService
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun setup() {
        runWithDelay(1000) {
            goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToAgeVerificationActivity() {
        val intent = Intent(this, AgeVerificationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
