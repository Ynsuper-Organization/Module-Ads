package com.bbl.module_ads.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bbl.module_ads.R;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class PrepareLoadingAdsDialog extends Dialog {

    public PrepareLoadingAdsDialog(Context context) {
        super(context, R.style.AppTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_prepair_loading_ads);

        // Áp dụng hiệu ứng fade-in cho toàn bộ layout của dialog
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        findViewById(R.id.dialog_root_view).startAnimation(fadeIn);
    }

    public void hideLoadingAdsText() {
        findViewById(R.id.loading_dialog_tv).setVisibility(View.INVISIBLE);
    }
}
