package com.mediscreen.risk.services;

import com.mediscreen.risk.exception.NotFoundException;
import com.mediscreen.risk.model.HumanGender;
import com.mediscreen.risk.model.Note;
import com.mediscreen.risk.model.Patient;
import com.mediscreen.risk.model.Risk;
import com.mediscreen.risk.services.restTemplateService.NoteRestService;
import com.mediscreen.risk.services.restTemplateService.PatientRestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
            return null;// evaluateRisk(patient);
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
            return null;// evaluateRisk(patient);
        } catch (NotFoundException notFoundException) {
            log.debug("Patient Not Found: Risk Not Evaluated");
            throw notFoundException;
        }
    }

    private Risk findPatientRisk(Patient patient){
        Risk riskResult = new Risk(patient);
        HumanGender humanGender = patient.getGender();

        List<Note> patientNotes = noteRestService.getList(patient.getId());

        return riskResult;
    }
}
