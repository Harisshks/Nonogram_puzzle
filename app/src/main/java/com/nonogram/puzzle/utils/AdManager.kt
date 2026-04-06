package com.nonogram.puzzle.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
    }

    fun loadInterstitial(context: Context, onLoaded: () -> Unit) {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            Constants.INTERSTITIAL_AD_UNIT_ID,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    onLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        interstitialAd?.let {
            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onDismissed()
                }
            }
            it.show(activity)
        } ?: onDismissed()
    }

    fun loadRewarded(
        context: Context,
        onLoaded: () -> Unit = {},
        onFailed: () -> Unit = {}
    ) {
        val request = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            Constants.REWARDED_AD_UNIT_ID,
            request,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    onLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    onFailed()
                }
            }
        )
    }

    /**
     * Shows a rewarded ad if available; otherwise loads one and shows when ready.
     * [onUserEarnedReward] runs when the user completes the ad.
     * [onUnavailable] when ad cannot be shown (load failed or no fill).
     */
    fun showRewarded(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onDismissed: () -> Unit = {},
        onUnavailable: () -> Unit = {}
    ) {
        fun showLoaded(ad: RewardedAd) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    onDismissed()
                }
            }
            ad.show(activity, OnUserEarnedRewardListener {
                onUserEarnedReward()
            })
        }

        val existing = rewardedAd
        if (existing != null) {
            showLoaded(existing)
        } else {
            loadRewarded(
                activity.applicationContext,
                onLoaded = {
                    rewardedAd?.let { showLoaded(it) } ?: onUnavailable()
                },
                onFailed = onUnavailable
            )
        }
    }
}
