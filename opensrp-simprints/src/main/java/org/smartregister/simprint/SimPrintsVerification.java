package org.smartregister.simprint;

import com.simprints.libsimprints.Tier;

import java.io.Serializable;

public class SimPrintsVerification implements Serializable {
    private String guid;
    private Boolean checkStatus;
    private Tier tier;
    private MaskedTier maskedTier;

    public SimPrintsVerification(String guId){
        this.guid = guId;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    public String getGuid() {
        return guid;
    }

    public Boolean getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public void setMaskedTier(MaskedTier maskedTier) {
        this.maskedTier = maskedTier;
    }

    public MaskedTier getMaskedTier() {
        return maskedTier;
    }

    public enum MaskedTier {
        TIER_1,
        TIER_2,
        TIER_3,
        TIER_4,
        TIER_5,
    }
}


