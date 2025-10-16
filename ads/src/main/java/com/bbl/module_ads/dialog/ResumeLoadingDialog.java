package com.bbl.module_ads.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.bbl.module_ads.R;
import com.bbl.module_ads.ads.BBLAd;

public class ResumeLoadingDialog extends Dialog {

    private Handler autoDismissHandler;
    private Runnable autoDismissRunnable;
    private static final long AUTO_DISMISS_DELAY_MS = 3000; // 3 seconds
    private int customLayoutId = R.layout.dialog_resume_loading; // Default layout

    public ResumeLoadingDialog(Context context) {
        super(context, R.style.AppTheme);
        // Get custom layout from BBLAd config if available
        int globalLayout = BBLAd.getInstance().getResumeLoadingDialogLayout();
        if (globalLayout != -1) {
            this.customLayoutId = globalLayout;
        }
    }

    /**
     * Constructor with custom layout
     * @param context The context
     * @param customLayoutId The custom layout resource ID
     */
    public ResumeLoadingDialog(Context context, int customLayoutId) {
        super(context, R.style.AppTheme);
        this.customLayoutId = customLayoutId;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(customLayoutId);
        
        // Khởi tạo auto dismiss logic
        setupAutoDismiss();
    }
    
    private void setupAutoDismiss() {
        autoDismissHandler = new Handler(Looper.getMainLooper());
        autoDismissRunnable = new Runnable() {
            @Override
            public void run() {
                if (isShowing()) {
                    try {
                        dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        // Đặt timer để tự động đóng dialog sau 3 giây
        autoDismissHandler.postDelayed(autoDismissRunnable, AUTO_DISMISS_DELAY_MS);
    }
    
    @Override
    public void dismiss() {
        // Hủy timer khi dialog được đóng thủ công
        if (autoDismissHandler != null && autoDismissRunnable != null) {
            autoDismissHandler.removeCallbacks(autoDismissRunnable);
        }
        super.dismiss();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // Đảm bảo hủy timer khi dialog không còn hiển thị
        if (autoDismissHandler != null && autoDismissRunnable != null) {
            autoDismissHandler.removeCallbacks(autoDismissRunnable);
        }
    }
}
