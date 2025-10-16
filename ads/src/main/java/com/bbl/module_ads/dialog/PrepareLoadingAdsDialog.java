package com.bbl.module_ads.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bbl.module_ads.R;
import com.bbl.module_ads.ads.BBLAd;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class PrepareLoadingAdsDialog extends Dialog {

    private int customLayoutId = R.layout.dialog_prepair_loading_ads; // Default layout

    public PrepareLoadingAdsDialog(Context context) {
        super(context, R.style.AppTheme);
        // Get custom layout from BBLAd config if available
        int globalLayout = BBLAd.getInstance().getPrepareLoadingAdsDialogLayout();
        if (globalLayout != -1) {
            this.customLayoutId = globalLayout;
        }
    }

    /**
     * Constructor with custom layout
     * @param context The context
     * @param customLayoutId The custom layout resource ID
     */
    public PrepareLoadingAdsDialog(Context context, int customLayoutId) {
        super(context, R.style.AppTheme);
        this.customLayoutId = customLayoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(customLayoutId);

        // Áp dụng hiệu ứng fade-in cho toàn bộ layout của dialog
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        View rootView = findViewById(R.id.dialog_root_view);
        if (rootView != null) {
            rootView.startAnimation(fadeIn);
        }
    }

    /**
     * Set custom layout for the dialog
     * @param customLayoutId The custom layout resource ID
     */
    public void setCustomLayout(int customLayoutId) {
        this.customLayoutId = customLayoutId;
        // Recreate dialog with new layout if already showing
        if (isShowing()) {
            dismiss();
            show();
        }
    }

    /**
     * Get current custom layout ID
     * @return The current layout resource ID
     */
    public int getCustomLayoutId() {
        return customLayoutId;
    }

    public void hideLoadingAdsText() {
        View loadingText = findViewById(R.id.loading_dialog_tv);
        if (loadingText != null) {
            loadingText.setVisibility(View.INVISIBLE);
        }
    }
}
