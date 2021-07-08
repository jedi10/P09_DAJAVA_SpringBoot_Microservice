package com.mediscreen.risk.services;

import com.mediscreen.risk.exception.NotFoundException;
import com.mediscreen.risk.model.*;
import com.mediscreen.risk.services.restTemplateService.NoteRestService;
import com.mediscreen.risk.services.restTemplateService.PatientRestService;
import com.mediscreen.risk.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <b>Get Risk For a Patient</b>
 */
@Slf4j
@Service
public class RiskService {

    @Autowired
    private PatientRestService patientRestService;

    @Autowired
    private NoteRestService noteRestService;

    /**
     * <b>get Risk for patient with patientId</b>
     *
     * @param patientId mandatory
     * @return Risk if defined
     * @throws NotFoundException patient was not found with patientId
     */
    public Risk getRisk(int patientId) {
        try {
            Patient patient = patientRestService.getById(patientId);
            return findPatientRisk(patient);
        } catch (NotFoundException notFoundException) {
            log.debug("Patient Not Found: Risk Not Evaluated");
            throw notFoundException;
        }
    }

    /**
     * <b>get Risk for patient with familyName</b>
     *
     * @param familyName mandatory
     * @return Risk if defined
     * @throws NotFoundException patient was not found with patientId
     */
    public Risk getRisk(String familyName) {
        try {
            Patient patient = patientRestService.getByFamilyName(familyName);
            return findPatientRisk(patient);
        } catch (NotFoundException notFoundException) {
            log.debug("Patient Not Found: Risk Not Evaluated");
            throw notFoundException;
        }
    }

    private Risk findPatientRisk(Patient patient){
        Risk riskResult = new Risk(patient);

        HumanGender humanGender = patient.getGender();

        List<Note> patientNotes = noteRestService.getList(patient.getId());

        long riskIteration = distinctRiskIteration(patientNotes);

        riskResult.setRiskLevelEnum(getRiskEnum(riskIteration, patient.getGender(), riskResult.getAge()));

        return riskResult;
    }

    static RiskLevelEnum getRiskEnum(long patientRiskIteration, HumanGender humanGender, int age) {

        long riskIterationLevel0 = 1;
        long riskIterationLevel1 = 2;
        long riskIterationLevel2 = 6;
        long riskIterationLevel3 = 8;

        if (age < 30) {
            if (HumanGender.MEN.equals(humanGender)) {
                riskIterationLevel2 = 3;
                riskIterationLevel3 = 5;
            }
            if (HumanGender.WOMEN.equals(humanGender)) {
                riskIterationLevel2 = 4;
                riskIterationLevel3 = 7;
            }
        }

        if (patientRiskIteration >= riskIterationLevel3) {
            return RiskLevelEnum.EARLY_ONSET;
        } else if (patientRiskIteration >= riskIterationLevel2) {
            return RiskLevelEnum.DANGER;
        } else if (patientRiskIteration >= riskIterationLevel1 && age > 30) {
            return RiskLevelEnum.BORDERLINE;
        } else if (patientRiskIteration >= riskIterationLevel1) {
            return RiskLevelEnum.NONE;
        } else if (patientRiskIteration <= riskIterationLevel0){
            return RiskLevelEnum.NONE;
        }
        return RiskLevelEnum.UNDEFINED;
    }

    static long distinctRiskIteration(List<Note> noteList) {
        List<String> riskList = ListUtils.getRiskFactors();

        AtomicLong iteration = new AtomicLong();

        riskList.forEach(risk-> {
                    java.util.Optional<Note> noteDetected =  noteList.stream()
                            .filter((note) ->
                                    note.getNote().toUpperCase(Locale.ROOT).contains(risk.toUpperCase(Locale.ROOT)))
                            .findFirst();
                    if(noteDetected.isPresent()){
                        iteration.set(iteration.get()+ 1);
                    }
        });
        return iteration.longValue();
    }
}

//Locale.Root is the base of all locales, is neutral locale used for sensitive operation