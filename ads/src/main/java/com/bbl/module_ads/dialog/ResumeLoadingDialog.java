package com.bbl.module_ads.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.bbl.module_ads.R;

public class ResumeLoadingDialog extends Dialog {

    private Handler autoDismissHandler;
    private Runnable autoDismissRunnable;
    private static final long AUTO_DISMISS_DELAY_MS = 3000; // 3 seconds

    public ResumeLoadingDialog(Context context) {
        super(context, R.style.AppTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_resume_loading);
        
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
