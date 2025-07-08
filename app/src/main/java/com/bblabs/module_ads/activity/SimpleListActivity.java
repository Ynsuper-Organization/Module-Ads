package com.bblabs.module_ads.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.bbl.module_ads.ads.BBLAd;
import com.bbl.module_ads.config.BBLAdConfig;
import com.bbl.module_ads.ads.nativeAds.BBLAdPlacer;
import com.bbl.module_ads.ads.nativeAds.BBLAdAdapter;
import com.bbl.module_ads.ads.wrapper.ApAdValue;
import com.bblabs.module_ads.adapter.ListSimpleAdapter;
import com.bblvn.example_ads.R;

import java.util.ArrayList;
import java.util.List;

public class SimpleListActivity extends AppCompatActivity {
    private static final String TAG = "SimpleListActivity";
    BBLAdAdapter adAdapter;
    int layoutCustomNative = R.layout.custom_native_admob_small;
    String idNative = "";
    SwipeRefreshLayout swRefresh;
    ListSimpleAdapter originalAdapter;
    RecyclerView recyclerView;
    BBLAdPlacer.Listener listener = new BBLAdPlacer.Listener() {
        @Override
        public void onAdLoaded(int i) {
            Log.i(TAG, "onAdLoaded native list: " + i);
        }

        @Override
        public void onAdRemoved(int i) {
            Log.i(TAG, "onAdRemoved: " + i);
        }

        @Override
        public void onAdClicked() {

        }

        @Override
        public void onAdRevenuePaid(ApAdValue adValue) {

        }

        @Override
        public void onAdImpression() {

        }

    };

    private List<String> sampleData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        swRefresh = findViewById(R.id.swRefresh);
        addSampleData();
        // init adapter origin
        originalAdapter = new ListSimpleAdapter(new ListSimpleAdapter.Listener() {
            @Override
            public void onRemoveItem(int pos) {
//                adAdapter.notifyItemRemoved(adAdapter.getOriginalPosition(pos));
                adAdapter.getAdapter().notifyDataSetChanged();
//                setupAdAdapter();
            }
        });
        originalAdapter.setSampleData(sampleData);
        recyclerView = findViewById(R.id.rvListSimple);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (BBLAd.getInstance().getMediationProvider() == BBLAdConfig.PROVIDER_ADMOB) {
            layoutCustomNative = com.bbl.module_ads.R.layout.custom_native_admod_medium;
            idNative = getString(R.string.admod_native_id);
        } else {
            layoutCustomNative =R.layout.custom_native_admob_small;
            idNative = getString(R.string.applovin_test_native);
        }

        setupAdAdapter();
        swRefresh.setOnRefreshListener(() -> {
            sampleData.add("Item add new");
            adAdapter.getAdapter().notifyDataSetChanged();
            swRefresh.setRefreshing(false);
        });
    }

    private void setupAdAdapter() {
        adAdapter = BBLAd.getInstance().getNativeRepeatAdapter(this, idNative, layoutCustomNative, com.bbl.module_ads.R.layout.layout_native_medium,
                originalAdapter, listener, 5);

        recyclerView.setAdapter(adAdapter.getAdapter());
        adAdapter.loadAds();
    }

    private void addSampleData() {
        for (int i = 0; i < 30; i++) {
            sampleData.add("Item " + i);
        }
    }

    @Override
    public void onDestroy() {
        adAdapter.destroy();
        super.onDestroy();
    }
}