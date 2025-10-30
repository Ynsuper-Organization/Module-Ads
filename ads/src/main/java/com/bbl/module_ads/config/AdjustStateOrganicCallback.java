package com.bbl.module_ads.config;

import com.adjust.sdk.AdjustAttribution;

public interface AdjustStateOrganicCallback {
    void isUserOrganic(Boolean isOrganic);

    default void onAttributionChanged(AdjustAttribution attribution) {

    }
}
