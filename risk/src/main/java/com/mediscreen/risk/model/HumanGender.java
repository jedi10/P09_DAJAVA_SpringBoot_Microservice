package com.mediscreen.risk.model;

/**
 * <b>Used by Risk Model</b>
 * @see com.mediscreen.risk.model.Risk
 */
public enum HumanGender {
    WOMEN("F"),
    MEN("M"),
    TRANSGENRE("T");

    private String label;

    HumanGender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}
