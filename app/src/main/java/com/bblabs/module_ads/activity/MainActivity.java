package com.bblabs.module_ads.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bbl.module_ads.admob.Admob;
import com.bbl.module_ads.admob.AppOpenManager;
import com.bbl.module_ads.ads.BBLAd;
import com.bbl.module_ads.ads.BBLAdCallback;
import com.bbl.module_ads.ads.bannerAds.BBLBannerAdView;
import com.bbl.module_ads.ads.nativeAds.NativeAdConfig;
import com.bbl.module_ads.ads.wrapper.ApAdError;
import com.bbl.module_ads.ads.wrapper.ApInterstitialAd;
import com.bbl.module_ads.ads.wrapper.ApRewardAd;
import com.bbl.module_ads.billing.AppPurchase;
import com.bbl.module_ads.config.BBLAdConfig;
import com.bbl.module_ads.dialog.DialogExitApp1;
import com.bbl.module_ads.dialog.InAppDialog;
import com.bbl.module_ads.event.BBLAdjust;
import com.bbl.module_ads.funtion.AdCallback;
import com.bbl.module_ads.funtion.DialogExitListener;
import com.bbl.module_ads.funtion.PurchaseListener;
import com.bblvn.example_ads.BuildConfig;
import com.bblvn.example_ads.R;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.nativead.NativeAd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String PRODUCT_ID = "android.test.purchased";
    private static final String TAG = "MAIN_TEST";

    private static final String EVENT_TOKEN_SIMPLE = "";
    private static final String EVENT_TOKEN_REVENUE = "wnyjng";


    private FrameLayout frAds;
    private NativeAd unifiedNativeAd;
    private ApInterstitialAd mInterstitialAd;
    private ApRewardAd rewardAd;


    private String idBanner = "";
    private String idNative = "";
    private String idInter = "";

//    private BBLNativeAdView bblNativeAdView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        bblNativeAdView = findViewById(R.id.bbl_native_ads);
        new Thread(
                () ->
                        // Initialize the Google Mobile Ads SDK on a background thread.
                        MobileAds.initialize(
                                this,
                                initializationStatus -> {
                                    Map<String, AdapterStatus> statusMap =
                                            initializationStatus.getAdapterStatusMap();
                                    for (String adapterClass : statusMap.keySet()) {
                                        AdapterStatus status = statusMap.get(adapterClass);
                                        Log.d(
                                                "MyApp",
                                                String.format(
                                                        "Adapter name: %s, Description: %s, Latency: %d",
                                                        adapterClass, status.getDescription(), status.getLatency()));
                                    }
                                    // Start loading ads here...
                                }))
                .start();

        configMediationProvider();
        BBLAd.getInstance().setCountClickToShowAds(3);
        BBLAd.getInstance().setPrepareLoadingAdsDialogLayout(R.layout.custom_loading_ads);
        AppOpenManager.getInstance().setEnableScreenContentCallback(true);
        AppOpenManager.getInstance().setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                Log.e("AppOpenManager", "onAdShowedFullScreenContent: ");

            }
        });

//        bblNativeAdView.loadNativeAd(this, idNative, new BBLAdCallback() {
//            @Override
//            public void onAdImpression() {
//                super.onAdImpression();
//            }
//        });

        NativeAdConfig nativeConfig = loadNativeConfigFromAssets();
        Admob.getInstance().loadNativeWithConfig(this, idNative, com.bbl.module_ads.R.layout.layout_native_custom, nativeConfig);


        AppPurchase.getInstance().setPurchaseListener(new PurchaseListener() {
            @Override
            public void onProductPurchased(String productId, String transactionDetails) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void displayErrorMessage(String errorMsg) {
            }

            @Override
            public void onUserCancelBilling() {

            }
        });

        BBLBannerAdView bannerAdView = findViewById(R.id.bannerView);
        bannerAdView.loadBanner(this, idBanner, new BBLAdCallback() {
            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
        loadAdInterstitial();

        findViewById(R.id.btShowAds).setOnClickListener(v -> {
            if (mInterstitialAd.isReady()) {

                ApInterstitialAd inter = BBLAd.getInstance().getInterstitialAds(this, idInter);

                BBLAd.getInstance().showInterstitialAdByTimes(this, mInterstitialAd, new BBLAdCallback() {
                    @Override
                    public void onNextAction() {
                        startActivity(new Intent(MainActivity.this, ContentActivity.class));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable ApAdError adError) {
                        super.onAdFailedToShow(adError);
                    }

                    @Override
                    public void onInterstitialShow() {
                        super.onInterstitialShow();
                    }
                }, true);
            } else {
                loadAdInterstitial();
            }
        });

        findViewById(R.id.btForceShowAds).setOnClickListener(v -> {
            if (mInterstitialAd.isReady()) {
                BBLAd.getInstance().forceShowInterstitial(this, mInterstitialAd, new BBLAdCallback() {
                    @Override
                    public void onNextAction() {
                        startActivity(new Intent(MainActivity.this, SimpleListActivity.class));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable ApAdError adError) {
                        super.onAdFailedToShow(adError);
                    }

                    @Override
                    public void onInterstitialShow() {
                        super.onInterstitialShow();
                    }
                }, true);
            } else {
                loadAdInterstitial();
            }

        });

        findViewById(R.id.btnShowReward).setOnClickListener(v -> {
            if (rewardAd != null && rewardAd.isReady()) {
                BBLAd.getInstance().forceShowRewardAd(this, rewardAd, new BBLAdCallback());
                return;
            }
            rewardAd = BBLAd.getInstance().getRewardAd(this, BuildConfig.ad_reward);
        });

        Button btnIAP = findViewById(R.id.btIap);
        if (AppPurchase.getInstance().isPurchased()) {
            btnIAP.setText("Consume Purchase");
        } else {
            btnIAP.setText("Purchase");
        }
        btnIAP.setOnClickListener(v -> {
            if (AppPurchase.getInstance().isPurchased()) {
                AppPurchase.getInstance().consumePurchase(AppPurchase.PRODUCT_ID_TEST);
            } else {
                InAppDialog dialog = new InAppDialog(this);
                dialog.setCallback(() -> {
                    AppPurchase.getInstance().purchase(this, PRODUCT_ID);
                    dialog.dismiss();
                });
                dialog.show();
            }
        });
        Button btnNativeFull = findViewById(R.id.btnNativeFull);
        btnNativeFull.setOnClickListener(v -> {
            if (mInterstitialAd.isReady()) {

                ApInterstitialAd inter = BBLAd.getInstance().getInterstitialAds(this, idInter);

                BBLAd.getInstance().showInterstitialAdByTimes(this, mInterstitialAd, new BBLAdCallback() {
                    @Override
                    public void onNextAction() {
                        //ShowNativeFull

                        startActivity(new Intent(MainActivity.this, ContentActivity.class));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable ApAdError adError) {
                        super.onAdFailedToShow(adError);
                    }

                    @Override
                    public void onInterstitialShow() {
                        super.onInterstitialShow();
                    }
                }, true);
            } else {
                loadAdInterstitial();
            }
        });

    }

    private void configMediationProvider() {
        if (BBLAd.getInstance().getMediationProvider() == BBLAdConfig.PROVIDER_ADMOB) {
            idBanner = BuildConfig.ad_banner;
            idNative = BuildConfig.ad_native;
            idInter = BuildConfig.ad_interstitial_splash;
        } else {
            idBanner = getString(R.string.applovin_test_banner);
            idNative = getString(R.string.applovin_test_native);
            idInter = getString(R.string.applovin_test_inter);
        }
    }

    private void loadAdInterstitial() {

        mInterstitialAd = BBLAd.getInstance().getInterstitialAds(this, idInter);
    }


    public void onTrackSimpleEventClick(View v) {
        BBLAdjust.onTrackEvent(EVENT_TOKEN_SIMPLE);
    }

    public void onTrackRevenueEventClick(View v) {
        BBLAdjust.onTrackIAPRevenue(EVENT_TOKEN_REVENUE, 1000000f, "USD");
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadNativeExit();
    }

    private void loadNativeExit() {

        if (unifiedNativeAd != null)
            return;
        Admob.getInstance().loadNativeAd(this, BuildConfig.ad_native, new AdCallback() {
            @Override
            public void onUnifiedNativeAdLoaded(NativeAd unifiedNativeAd) {
                MainActivity.this.unifiedNativeAd = unifiedNativeAd;
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
    }

    private NativeAdConfig loadNativeConfigFromAssets() {
        try {
            InputStream is = getAssets().open("native_ad_config.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            is.close();
            return NativeAdConfig.fromJson(this, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new NativeAdConfig();
        }
    }

    @Override
    public void onBackPressed() {
        if (unifiedNativeAd == null)
            return;

        DialogExitApp1 dialogExitApp1 = new DialogExitApp1(this, unifiedNativeAd, 3);
        dialogExitApp1.setDialogExitListener(new DialogExitListener() {
            @Override
            public void onExit(boolean exit) {
                MainActivity.super.onBackPressed();
            }
        });
        dialogExitApp1.setCancelable(false);
        dialogExitApp1.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AppPurchase.getInstance().isPurchased(this)) {
            findViewById(R.id.btIap).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}