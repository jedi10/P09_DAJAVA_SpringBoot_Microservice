package com.mediscreen.risk.utils;

import com.mediscreen.risk.model.data.RiskFactor;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public static List<String> getRiskFactors(){
        List<String> result = new ArrayList<>();
        result.add(RiskFactor.HEMOGLOBINE);
        result.add(RiskFactor.MICROALBUMINE);
        result.add(RiskFactor.TAILLE);
        result.add(RiskFactor.POIDS);
        result.add(RiskFactor.FUMEUR);
        result.add(RiskFactor.ANORMAL);
        result.add(RiskFactor.CHOLESTEROL);
        result.add(RiskFactor.VERTIGE);
        result.add(RiskFactor.RECHUTE);
        result.add(RiskFactor.REACTION);
        result.add(RiskFactor.ANTICORPS);
        return result;
    }
}
