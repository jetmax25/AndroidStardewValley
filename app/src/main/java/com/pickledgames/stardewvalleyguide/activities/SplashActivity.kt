package com.pickledgames.stardewvalleyguide.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.managers.PurchasesManager
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject lateinit var purchasesManager: PurchasesManager
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
        val disposable = purchasesManager.initializedSubject
                .delay(1, TimeUnit.SECONDS)
                .doOnComplete { goToMainActivity() }
                .subscribe()

        compositeDisposable.add(disposable)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
