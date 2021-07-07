package com.mediscreen.risk.model;

/**
 * <b>Used by Risk Model</b>
 * @see com.mediscreen.risk.model.Risk
 */
public enum RiskLevelEnum {
    NONE("No Risk"),
    BORDERLINE("Limited Risk"),
    DANGER("In Danger"),
    EARLY_ONSET("Early Onset");

    private String label;

    RiskLevelEnum(String text) {
        this.label = text;
    }

    public String getLabel() {
        return this.label;
    }

}
