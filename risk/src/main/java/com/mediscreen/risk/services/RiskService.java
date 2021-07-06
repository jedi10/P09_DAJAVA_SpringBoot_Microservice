package com.mediscreen.risk.services;

import com.mediscreen.risk.exception.NotFoundException;
import com.mediscreen.risk.model.*;
import com.mediscreen.risk.services.restTemplateService.NoteRestService;
import com.mediscreen.risk.services.restTemplateService.PatientRestService;
import com.mediscreen.risk.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <>Get Risk For a Patient</>
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

        long riskIteration = countRiskIteration(patientNotes);

        riskResult.setRiskEnum(getRiskEnum(riskIteration));

        return riskResult;
    }

    static RiskEnum getRiskEnum(long riskIteration) {
        if (riskIteration >= 8) {
            return RiskEnum.EARLY_ONSET;
        } else if (riskIteration >= 6) {
            return RiskEnum.DANGER;
        } else if (riskIteration >= 2) {
            return RiskEnum.BORDERLINE;
        } else {
            return RiskEnum.NONE;
        }
    }

    static long countRiskIteration(List<Note> noteList) {
        List<String> riskList = ListUtils.getRiskFactors();

        AtomicLong iteration = new AtomicLong();

        riskList.forEach(risk->
            iteration.set(iteration.get() + noteList.stream()
                    .filter((note) ->
                            note.getNote().toUpperCase(Locale.ROOT).contains(risk.toUpperCase(Locale.ROOT)))
                    .count())
        );
        return iteration.longValue();
    }
}

//Locale.Root is the base of all locales, is neutral locale used for sensitive operation