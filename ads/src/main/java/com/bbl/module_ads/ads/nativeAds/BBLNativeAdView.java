package com.bbl.module_ads.ads.nativeAds;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bbl.module_ads.R;
import com.bbl.module_ads.ads.BBLAd;
import com.bbl.module_ads.ads.BBLAdCallback;
import com.bbl.module_ads.ads.wrapper.ApNativeAd;
import com.facebook.shimmer.ShimmerFrameLayout;

public class BBLNativeAdView extends RelativeLayout {

    private int layoutCustomNativeAd = 0;
    private ShimmerFrameLayout layoutLoading;
    private FrameLayout layoutPlaceHolder;
    private String TAG = "BBLNativeAdView";

    public BBLNativeAdView(@NonNull Context context) {
        super(context);
        init();
    }

    public BBLNativeAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BBLNativeAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    public BBLNativeAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BBLNativeAdView, 0, 0);
        // Get layout native view custom and  layout loading
        layoutCustomNativeAd = typedArray.getResourceId(R.styleable.BBLNativeAdView_layoutCustomNativeAd, 0);
        int idLayoutLoading = typedArray.getResourceId(R.styleable.BBLNativeAdView_layoutLoading, 0);
        if (idLayoutLoading != 0)
            layoutLoading = (ShimmerFrameLayout) LayoutInflater.from(getContext()).inflate(idLayoutLoading, null);

        init();
    }

    private void init() {
        layoutPlaceHolder = new FrameLayout(getContext());
        addView(layoutPlaceHolder);
        if (layoutLoading != null)
            addView(layoutLoading);

    }

    public void setLayoutCustomNativeAd(int layoutCustomNativeAd) {
        this.layoutCustomNativeAd = layoutCustomNativeAd;
    }

    public void setLayoutLoading(int idLayoutLoading) {
        this.layoutLoading = (ShimmerFrameLayout) LayoutInflater.from(getContext()).inflate(idLayoutLoading, null);
        addView(layoutLoading);
    }

    public void populateNativeAdView(Activity activity, ApNativeAd nativeAd){
        if(layoutLoading == null){
            Log.e(TAG, "populateNativeAdView error : layoutLoading not set"  );
            return;
        }
        BBLAd.getInstance().populateNativeAdView(activity, nativeAd, layoutPlaceHolder, layoutLoading);
    }

    public void loadNativeAd(Activity activity, String idAd ) {
        loadNativeAd(activity, idAd, new BBLAdCallback(){});
    }
    public void loadNativeAd(Activity activity, String idAd, BBLAdCallback bblAdCallback) {
        if(layoutLoading == null){
            setLayoutLoading(R.layout.loading_native_medium);
        }
        if (layoutCustomNativeAd == 0){
            layoutCustomNativeAd = R.layout.custom_native_admod_medium_rate;
            setLayoutCustomNativeAd(layoutCustomNativeAd);
        }
        BBLAd.getInstance().loadNativeAd(activity, idAd, layoutCustomNativeAd, layoutPlaceHolder, layoutLoading, bblAdCallback);
    }

    public void loadNativeAd(Activity activity, String idAd, int layoutCustomNativeAd, int idLayoutLoading) {
        setLayoutLoading(idLayoutLoading);
        setLayoutCustomNativeAd(layoutCustomNativeAd);
        loadNativeAd(activity,idAd);
    }

    public void loadNativeAd(Activity activity, String idAd, int layoutCustomNativeAd, int idLayoutLoading, BBLAdCallback miaAdCallback) {
        setLayoutLoading(idLayoutLoading);
        setLayoutCustomNativeAd(layoutCustomNativeAd);
        loadNativeAd(activity,idAd, miaAdCallback);
    }
}