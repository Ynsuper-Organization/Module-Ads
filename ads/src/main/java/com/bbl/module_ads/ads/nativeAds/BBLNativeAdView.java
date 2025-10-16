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
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bbl.module_ads.R;
import com.bbl.module_ads.ads.BBLAd;
import com.bbl.module_ads.ads.BBLAdCallback;
import com.bbl.module_ads.ads.wrapper.ApNativeAd;
import com.facebook.shimmer.ShimmerFrameLayout;

public class BBLNativeAdView extends RelativeLayout implements DefaultLifecycleObserver {

    private int layoutCustomNativeAd = 0;
    private ShimmerFrameLayout layoutLoading;
    private FrameLayout layoutPlaceHolder;
    private String TAG = "BBLNativeAdView";
    
    // Reload lifecycle option
    private boolean isReloadLifeCycle = false;
    private String reloadAdId = null;
    private Activity currentActivity;
    private String currentAdId;
    private BBLAdCallback currentCallback;

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
        
        // Get isReloadLifeCycle from XML attributes
        isReloadLifeCycle = typedArray.getBoolean(R.styleable.BBLNativeAdView_isReloadLifeCycle, false);
        
        // Get reloadAdId from XML attributes
        reloadAdId = typedArray.getString(R.styleable.BBLNativeAdView_reloadAdId);
        
        typedArray.recycle();
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

    /**
     * Set whether to reload ads when activity resumes
     * @param isReloadLifeCycle true to enable reload on lifecycle, false otherwise
     */
    public void setReloadLifeCycle(boolean isReloadLifeCycle) {
        this.isReloadLifeCycle = isReloadLifeCycle;
    }

    /**
     * Get current reload lifecycle setting
     * @return true if reload on lifecycle is enabled
     */
    public boolean isReloadLifeCycle() {
        return isReloadLifeCycle;
    }

    /**
     * Set reload ad ID for lifecycle reload
     * @param reloadAdId The ad ID to use when reloading on lifecycle
     */
    public void setReloadAdId(String reloadAdId) {
        this.reloadAdId = reloadAdId;
    }

    /**
     * Get current reload ad ID
     * @return The reload ad ID, null if not set
     */
    public String getReloadAdId() {
        return reloadAdId;
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
        
        // Store current activity and ad info for lifecycle reload
        this.currentActivity = activity;
        this.currentAdId = idAd;
        this.currentCallback = bblAdCallback;
        
        // Register lifecycle observer if isReloadLifeCycle is enabled
        if (isReloadLifeCycle && activity instanceof androidx.lifecycle.LifecycleOwner) {
            ((androidx.lifecycle.LifecycleOwner) activity).getLifecycle().addObserver(this);
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

    /**
     * Lifecycle observer method - called when activity resumes
     */
    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        if (isReloadLifeCycle && currentActivity != null) {
            if (reloadAdId != null && !reloadAdId.isEmpty()) {
                Log.d(TAG, "Activity resumed, reloading native ad with reloadAdId: " + reloadAdId);
                reloadNativeAdWithNewId(reloadAdId);
            } else if (currentAdId != null) {
                Log.d(TAG, "Activity resumed, reloading native ad with current ID: " + currentAdId);
                reloadNativeAd();
            }
        }
    }

    /**
     * Reload the current native ad
     */
    public void reloadNativeAd() {
        if (currentActivity != null && currentAdId != null) {
            Log.d(TAG, "Reloading native ad with ID: " + currentAdId);
            loadNativeAd(currentActivity, currentAdId, currentCallback);
        } else {
            Log.w(TAG, "Cannot reload native ad: missing activity or ad ID");
        }
    }

    /**
     * Reload native ad with a new ad ID
     * @param newAdId The new ad ID to load
     */
    public void reloadNativeAdWithNewId(String newAdId) {
        if (currentActivity != null) {
            Log.d(TAG, "Reloading native ad with new ID: " + newAdId);
            loadNativeAd(currentActivity, newAdId, currentCallback);
        } else {
            Log.w(TAG, "Cannot reload native ad: missing activity");
        }
    }

    /**
     * Reload native ad with a new ad ID and callback
     * @param newAdId The new ad ID to load
     * @param newCallback The new callback to use
     */
    public void reloadNativeAdWithNewId(String newAdId, BBLAdCallback newCallback) {
        if (currentActivity != null) {
            Log.d(TAG, "Reloading native ad with new ID: " + newAdId + " and new callback");
            loadNativeAd(currentActivity, newAdId, newCallback);
        } else {
            Log.w(TAG, "Cannot reload native ad: missing activity");
        }
    }

    /**
     * Reload native ad with new ad ID, layout and callback
     * @param newAdId The new ad ID to load
     * @param newLayoutCustomNativeAd The new custom native ad layout
     * @param newIdLayoutLoading The new loading layout
     * @param newCallback The new callback to use
     */
    public void reloadNativeAdWithNewId(String newAdId, int newLayoutCustomNativeAd, int newIdLayoutLoading, BBLAdCallback newCallback) {
        if (currentActivity != null) {
            Log.d(TAG, "Reloading native ad with new ID: " + newAdId + ", new layout and new callback");
            loadNativeAd(currentActivity, newAdId, newLayoutCustomNativeAd, newIdLayoutLoading, newCallback);
        } else {
            Log.w(TAG, "Cannot reload native ad: missing activity");
        }
    }

    /**
     * Clear stored activity and ad info
     */
    public void clearStoredInfo() {
        // Unregister lifecycle observer if registered
        if (currentActivity instanceof androidx.lifecycle.LifecycleOwner) {
            ((androidx.lifecycle.LifecycleOwner) currentActivity).getLifecycle().removeObserver(this);
        }
        
        this.currentActivity = null;
        this.currentAdId = null;
        this.currentCallback = null;
    }
}