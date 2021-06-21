package com.mediscreen.ui.service.restTemplateService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class PatientRestService {

    @Autowired
    RestTemplate restTemplate;

}
