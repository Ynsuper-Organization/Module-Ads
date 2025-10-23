package com.bbl.module_ads.ads.wrapper;

import android.view.View;

import com.google.android.gms.ads.nativead.NativeAd;
import com.bbl.module_ads.ads.nativeAds.NativeAdConfig;

public class ApNativeAd extends ApAdBase {
    private int layoutCustomNative;
    private View nativeView;
    private NativeAd admobNativeAd;
    private NativeAdConfig nativeAdConfig;

    public ApNativeAd(StatusAd status) {
        super(status);
    }

    public ApNativeAd(int layoutCustomNative, View nativeView) {
        this.layoutCustomNative = layoutCustomNative;
        this.nativeView = nativeView;
        status = StatusAd.AD_LOADED;
    }

    public ApNativeAd(int layoutCustomNative, NativeAd admobNativeAd) {
        this.layoutCustomNative = layoutCustomNative;
        this.admobNativeAd = admobNativeAd;
        status = StatusAd.AD_LOADED;
    }

    public ApNativeAd(int layoutCustomNative, NativeAd admobNativeAd, NativeAdConfig nativeAdConfig) {
        this.layoutCustomNative = layoutCustomNative;
        this.admobNativeAd = admobNativeAd;
        this.nativeAdConfig = nativeAdConfig;
        status = StatusAd.AD_LOADED;
    }

    public ApNativeAd(int layoutCustomNative, View nativeView, NativeAdConfig nativeAdConfig) {
        this.layoutCustomNative = layoutCustomNative;
        this.nativeView = nativeView;
        this.nativeAdConfig = nativeAdConfig;
        status = StatusAd.AD_LOADED;
    }

    public NativeAd getAdmobNativeAd() {
        return admobNativeAd;
    }

    public void setAdmobNativeAd(NativeAd admobNativeAd) {
        this.admobNativeAd = admobNativeAd;
        if (admobNativeAd != null)
            status = StatusAd.AD_LOADED;
    }

    public ApNativeAd() {
    }


    @Override
    boolean isReady() {
        return nativeView != null || admobNativeAd != null;
    }


    public int getLayoutCustomNative() {
        return layoutCustomNative;
    }

    public void setLayoutCustomNative(int layoutCustomNative) {
        this.layoutCustomNative = layoutCustomNative;
    }

    public View getNativeView() {
        return nativeView;
    }

    public void setNativeView(View nativeView) {
        this.nativeView = nativeView;
    }

    public NativeAdConfig getNativeAdConfig() {
        return nativeAdConfig;
    }

    public void setNativeAdConfig(NativeAdConfig nativeAdConfig) {
        this.nativeAdConfig = nativeAdConfig;
    }

    /**
     * Mark this ad as impressed
     */
    public void markAsImpressed() {
        this.status = StatusAd.AD_IMPRESSED;
    }

    /**
     * Check if this ad has been impressed
     * @return true if ad has been impressed, false otherwise
     */
    public boolean isImpressed() {
        return this.status == StatusAd.AD_IMPRESSED;
    }

    /**
     * Check if this ad is ready to be impressed (loaded but not yet impressed)
     * @return true if ad is loaded and not yet impressed
     */
    public boolean isReadyToImpress() {
        return isReady() && !isImpressed();
    }

    public String toString() {
        return "Status:" + status + " == nativeView:" + nativeView + " == admobNativeAd:" + admobNativeAd;
    }

}
