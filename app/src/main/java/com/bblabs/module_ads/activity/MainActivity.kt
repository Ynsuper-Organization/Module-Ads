package com.bblabs.module_ads.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.bbl.module_ads.admob.Admob
import com.bbl.module_ads.admob.AppOpenManager
import com.bbl.module_ads.ads.BBLAd
import com.bbl.module_ads.ads.BBLAdCallback
import com.bbl.module_ads.ads.bannerAds.BBLBannerAdView
import com.bbl.module_ads.ads.nativeAds.BBLNativeAdView
import com.bbl.module_ads.ads.nativeAds.NativeAdConfig
import com.bbl.module_ads.ads.wrapper.ApAdError
import com.bbl.module_ads.ads.wrapper.ApInterstitialAd
import com.bbl.module_ads.ads.wrapper.ApNativeAd
import com.bbl.module_ads.ads.wrapper.ApRewardAd
import com.bbl.module_ads.billing.AppPurchase
import com.bbl.module_ads.config.BBLAdConfig
import com.bbl.module_ads.dialog.DialogExitApp1
import com.bbl.module_ads.dialog.InAppDialog
import com.bbl.module_ads.dialog.InAppDialog.ICallback
import com.bbl.module_ads.event.BBLAdjust
import com.bbl.module_ads.funtion.AdCallback
import com.bbl.module_ads.funtion.DialogExitListener
import com.bbl.module_ads.funtion.PurchaseListener
import com.bblvn.example_ads.BuildConfig
import com.bblvn.example_ads.R
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.nativead.NativeAd
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private val frAds: FrameLayout? = null
    private var unifiedNativeAd: NativeAd? = null
    private var mInterstitialAd: ApInterstitialAd? = null
    private var rewardAd: ApRewardAd? = null


    private var idBanner = ""
    private var idNative = BuildConfig.ad_native
    private var idInter = ""

//        private BBLNativeAdView bblNativeAdView;
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //        bblNativeAdView = findViewById(R.id.bbl_native_ads);
        Thread(
            Runnable { // Initialize the Google Mobile Ads SDK on a background thread.
                MobileAds.initialize(
                    this,
                    OnInitializationCompleteListener { initializationStatus: InitializationStatus? ->
                        val statusMap =
                            initializationStatus!!.getAdapterStatusMap()
                        for (adapterClass in statusMap.keys) {
                            val status = statusMap.get(adapterClass)
                            Log.d(
                                "MyApp",
                                String.format(
                                    "Adapter name: %s, Description: %s, Latency: %d",
                                    adapterClass, status!!.getDescription(), status.getLatency()
                                )
                            )
                        }
                    })
            })
            .start()

        configMediationProvider()
        BBLAd.getInstance().setCountClickToShowAds(3)
        BBLAd.getInstance().setPrepareLoadingAdsDialogLayout(R.layout.custom_loading_ads)
        AppOpenManager.getInstance().setEnableScreenContentCallback(true)
        AppOpenManager.getInstance()
            .setFullScreenContentCallback(object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    Log.e("AppOpenManager", "onAdShowedFullScreenContent: ")
                }
            })

        //        bblNativeAdView.loadNativeAd(this, idNative, new BBLAdCallback() {
//            @Override
//            public void onAdImpression() {
//                super.onAdImpression();
//            }
//        });

        
        // Example: How to use BBLNativeAdView with NativeAdConfig
        // Uncomment the following code to use BBLNativeAdView with custom configuration:
        val bblNativeAdView = findViewById<BBLNativeAdView>(R.id.bbl_native_ads)

        // Method 1: Set config first, then load (backward compatible)
//        bblNativeAdView.setNativeAdConfig(nativeConfig)
//        bblNativeAdView.loadNativeAd(this, idNative, object : BBLAdCallback() {
//            override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
//                super.onNativeAdLoaded(nativeAd)
//                Log.d(TAG, "BBLNativeAdView: Native ad loaded with custom config")
//            }
//
//            override fun onAdFailedToLoad(adError: ApAdError?) {
//                super.onAdFailedToLoad(adError)
//                Log.d(TAG, "BBLNativeAdView: Failed to load native ad")
//            }
//        })

        // Method 2: Use new method with explicit config (recommended)
        bblNativeAdView.loadNativeAdWithConfig(this, idNative, object : BBLAdCallback() {
            override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                super.onNativeAdLoaded(nativeAd)
                Log.d(TAG, "BBLNativeAdView: Native ad loaded with explicit config")
            }
            
            override fun onAdFailedToLoad(adError: ApAdError?) {
                super.onAdFailedToLoad(adError)
                Log.d(TAG, "BBLNativeAdView: Failed to load native ad")
            }
        }, loadNativeConfigFromAssets("native_ad_config_2.json"))

        Admob.getInstance().loadNativeWithConfig(
            this,
            idNative,
            com.bbl.module_ads.R.layout.layout_native_custom,
            loadNativeConfigFromAssets("native_ad_config.json"), null
        )



        AppPurchase.getInstance().setPurchaseListener(object : PurchaseListener {
            override fun onProductPurchased(productId: String?, transactionDetails: String?) {
                startActivity(Intent(this@MainActivity, MainActivity::class.java))
                finish()
            }

            override fun displayErrorMessage(errorMsg: String?) {
            }

            override fun onUserCancelBilling() {
            }
        })

        val bannerAdView = findViewById<BBLBannerAdView>(R.id.bannerView)
        bannerAdView.loadBanner(this, idBanner, object : BBLAdCallback() {
            override fun onAdImpression() {
                super.onAdImpression()
            }
        })
        loadAdInterstitial()

        findViewById<View?>(R.id.btShowAds).setOnClickListener(View.OnClickListener { v: View? ->
            if (mInterstitialAd!!.isReady()) {
                val inter = BBLAd.getInstance().getInterstitialAds(this, idInter)

                BBLAd.getInstance()
                    .showInterstitialAdByTimes(this, mInterstitialAd, object : BBLAdCallback() {
                        override fun onNextAction() {
                            startActivity(Intent(this@MainActivity, ContentActivity::class.java))
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                        }

                        override fun onInterstitialShow() {
                            super.onInterstitialShow()
                        }
                    }, true)
            } else {
                loadAdInterstitial()
            }
        })

        findViewById<View?>(R.id.btForceShowAds).setOnClickListener(View.OnClickListener { v: View? ->
            if (mInterstitialAd!!.isReady()) {
                BBLAd.getInstance()
                    .forceShowInterstitial(this, mInterstitialAd, object : BBLAdCallback() {
                        override fun onNextAction() {
                            startActivity(Intent(this@MainActivity, SimpleListActivity::class.java))
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                        }

                        override fun onInterstitialShow() {
                            super.onInterstitialShow()
                        }
                    }, true)
            } else {
                loadAdInterstitial()
            }
        })

        findViewById<View?>(R.id.btnShowReward).setOnClickListener(View.OnClickListener { v: View? ->
            if (rewardAd != null && rewardAd!!.isReady()) {
                BBLAd.getInstance().forceShowRewardAd(this, rewardAd, BBLAdCallback())
                return@OnClickListener
            }
            rewardAd = BBLAd.getInstance().getRewardAd(this, BuildConfig.ad_reward)
        })

        val btnIAP = findViewById<Button>(R.id.btIap)
        if (AppPurchase.getInstance().isPurchased()) {
            btnIAP.setText("Consume Purchase")
        } else {
            btnIAP.setText("Purchase")
        }
        btnIAP.setOnClickListener(View.OnClickListener { v: View? ->
            if (AppPurchase.getInstance().isPurchased()) {
                AppPurchase.getInstance().consumePurchase(AppPurchase.PRODUCT_ID_TEST)
            } else {
                val dialog = InAppDialog(this)
                dialog.setCallback(ICallback {
                    AppPurchase.getInstance().purchase(this, PRODUCT_ID)
                    dialog.dismiss()
                })
                dialog.show()
            }
        })
        val btnNativeFull = findViewById<Button>(R.id.btnNativeFull)
        btnNativeFull.setOnClickListener(View.OnClickListener { v: View? ->
            if (mInterstitialAd!!.isReady()) {
                val inter = BBLAd.getInstance().getInterstitialAds(this, idInter)

                BBLAd.getInstance()
                    .showInterstitialAdByTimes(this, mInterstitialAd, object : BBLAdCallback() {
                        override fun onNextAction() {
                            //ShowNativeFull

                            startActivity(Intent(this@MainActivity, ContentActivity::class.java))
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                        }

                        override fun onInterstitialShow() {
                            super.onInterstitialShow()
                        }
                    }, true)
            } else {
                loadAdInterstitial()
            }
        })
    }

    private fun configMediationProvider() {
        if (BBLAd.getInstance().getMediationProvider() == BBLAdConfig.PROVIDER_ADMOB) {
            idBanner = BuildConfig.ad_banner
            idNative = BuildConfig.ad_native
            idInter = BuildConfig.ad_interstitial_splash
        } else {
            idBanner = getString(R.string.applovin_test_banner)
            idNative = getString(R.string.applovin_test_native)
            idInter = getString(R.string.applovin_test_inter)
        }
    }

    private fun loadAdInterstitial() {
        mInterstitialAd = BBLAd.getInstance().getInterstitialAds(this, idInter)
    }


    fun onTrackSimpleEventClick(v: View?) {
        BBLAdjust.onTrackEvent(EVENT_TOKEN_SIMPLE)
    }

    fun onTrackRevenueEventClick(v: View?) {
        BBLAdjust.onTrackIAPRevenue(EVENT_TOKEN_REVENUE, 1000000f, "USD")
    }


    override fun onResume() {
        super.onResume()
        loadNativeExit()
    }

    private fun loadNativeExit() {
        if (unifiedNativeAd != null) return
        Admob.getInstance().loadNativeAd(this, BuildConfig.ad_native, object : AdCallback() {
            override fun onUnifiedNativeAdLoaded(unifiedNativeAd: NativeAd) {
                this@MainActivity.unifiedNativeAd = unifiedNativeAd
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }
        })
    }

    private fun loadNativeConfigFromAssets(remoteFile: String): NativeAdConfig {
        try {
            val `is` = getAssets().open(remoteFile)
            val br = BufferedReader(InputStreamReader(`is`))
            val sb = StringBuilder()
            var line: String?
            while ((br.readLine().also { line = it }) != null) {
                sb.append(line)
            }
            br.close()
            `is`.close()
            return NativeAdConfig.fromJson(this, sb.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            return NativeAdConfig()
        }
    }

    override fun onBackPressed() {
        if (unifiedNativeAd == null) return

        val dialogExitApp1 = DialogExitApp1(this, unifiedNativeAd, 3)
        dialogExitApp1.setDialogExitListener(object : DialogExitListener {
            override fun onExit(exit: Boolean) {
                super@MainActivity.onBackPressed()
            }
        })
        dialogExitApp1.setCancelable(false)
        dialogExitApp1.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (AppPurchase.getInstance().isPurchased(this)) {
            findViewById<View?>(R.id.btIap).setVisibility(View.GONE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val PRODUCT_ID: String = "android.test.purchased"
        private const val TAG = "MAIN_TEST"

        private const val EVENT_TOKEN_SIMPLE = ""
        private const val EVENT_TOKEN_REVENUE = "wnyjng"
    }
}