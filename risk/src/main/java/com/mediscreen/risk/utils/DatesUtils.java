package com.mediscreen.risk.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Map;

public class DatesUtils {

    //Static Zone List
    // https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html
    private static Map<String,String> zone = ZoneId.SHORT_IDS;

    /**
     * <b>Give human age from a birthdate LocalDate</b>
     *
     * @param birthDate birthDate
     * @return age computed fom a birthday
     */
    public static int getAge(LocalDate birthDate) {
        if (birthDate != null) {
            return Period.between(birthDate,
                    getNowLocalDate(zone.get("ECT"))).getYears();//"Europe/Paris"
        } else {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * <b>LocalDate Now by using ZoneId</b>
     *
     * @return now with LocalDate
     */
    public static LocalDate getNowLocalDate(String zoneId) {
        LocalDate result = null;
        if (zoneId != null){
            result = LocalDate.now(ZoneId.of(zoneId));
        } else {
            result = LocalDate.now(ZoneId.systemDefault());
        }
        return result;
    }

}
