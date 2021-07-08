package com.mediscreen.risk.model;

/**
 * <b>Used with Patient Model</b>
 * @see com.mediscreen.risk.model.Patient
 */
public enum HumanGender {
    WOMEN("F"),
    MEN("M"),
    TRANSGENRE("T"),
    UNDEFINED("");

    private String label;

    HumanGender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static HumanGender getHumanGender(String ref){
        for (HumanGender each: values()){
            if (each.label.equalsIgnoreCase(ref)){
                return each;
            }
        }
        return UNDEFINED;
    }
}
