package com.bblabs.module_ads;

import com.bbl.module_ads.ads.BBLAd;
import com.bbl.module_ads.config.AdjustConfig;
import com.bbl.module_ads.config.AppsflyerConfig;
import com.bbl.module_ads.config.BBLAdConfig;
import com.bbl.module_ads.application.AdsMultiDexApplication;
import com.bbl.module_ads.applovin.AppLovin;
import com.bbl.module_ads.applovin.AppOpenMax;
import com.bbl.module_ads.billing.AppPurchase;
import com.bbl.module_ads.admob.Admob;
import com.bbl.module_ads.admob.AppOpenManager;
import com.bblabs.module_ads.activity.MainActivity;
import com.bblabs.module_ads.activity.SplashActivity;
import com.bblvn.example_ads.BuildConfig;


import java.util.ArrayList;
import java.util.List;

public class MyApplication extends AdsMultiDexApplication {
    private final String APPSFLYER_TOKEN = "";
    private final String ADJUST_TOKEN = "";
    private final String EVENT_PURCHASE_ADJUST = "";
    private final String EVENT_AD_IMPRESSION_ADJUST = "";
    protected StorageCommon storageCommon;
    private static MyApplication context;
    public static MyApplication getApplication() {
        return context;
    }
    public StorageCommon getStorageCommon() {
        return storageCommon;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Admob.getInstance().setNumToShowAds(0);

        storageCommon = new StorageCommon();
        initBilling();
        initAds();

    }

    private void initAds() {
        String environment = BuildConfig.env_dev ? BBLAdConfig.ENVIRONMENT_DEVELOP : BBLAdConfig.ENVIRONMENT_PRODUCTION;
        bblAdConfig = new BBLAdConfig(this, BBLAdConfig.PROVIDER_ADMOB, environment);

        AdjustConfig adjustConfig = new AdjustConfig(true,ADJUST_TOKEN);
        adjustConfig.setEventAdImpression(EVENT_AD_IMPRESSION_ADJUST);

        adjustConfig.setEventNamePurchase(EVENT_PURCHASE_ADJUST);
        bblAdConfig.setAdjustConfig(adjustConfig);

        AppsflyerConfig appsflyerConfig = new AppsflyerConfig(true,APPSFLYER_TOKEN);
        listTestDevice.add("4271C93CF09454C3100C720F43AADEF4");
        bblAdConfig.setListDeviceTest(listTestDevice);
        bblAdConfig.setIntervalInterstitialAd(15);
        bblAdConfig.setTiktokAppID("");

        BBLAd.getInstance().init(this, bblAdConfig, false);

        Admob.getInstance().setDisableAdResumeWhenClickAds(true);
        AppLovin.getInstance().setDisableAdResumeWhenClickAds(true);
        Admob.getInstance().setOpenActivityAfterShowInterAds(true);

        if (BBLAd.getInstance().getMediationProvider() == BBLAdConfig.PROVIDER_ADMOB) {
            AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        } else {
            AppOpenMax.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        }
    }

    private void initBilling() {
        List<String> listINAPId = new ArrayList<>();
        listINAPId.add(MainActivity.PRODUCT_ID);
        List<String> listSubsId = new ArrayList<>();

        AppPurchase.getInstance().initBilling(getApplication(), listINAPId, listSubsId);
    }

}
