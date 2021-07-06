package com.mediscreen.risk.model;

public enum RiskEnum {
    NONE("No Risk"),
    BORDERLINE("Limited Risk"),
    DANGER("In Danger"),
    EARLY_ONSET("Early Onset");

    private String label;

    RiskEnum(String text) {
        this.label = text;
    }

    public String getLabel() {
        return this.label;
    }

}
