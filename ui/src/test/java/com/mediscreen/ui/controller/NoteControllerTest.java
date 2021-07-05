package com.mediscreen.ui.controller;

import com.mediscreen.ui.model.Note;
import com.mediscreen.ui.model.Patient;
import com.mediscreen.ui.service.restTemplateService.NoteRestService;
import com.mediscreen.ui.service.restTemplateService.PatientRestService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NoteControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    private PatientController patientController;
    @MockBean
    private NoteRestService noteRestService;
    @MockBean
    private PatientRestService patientRestService;
    @InjectMocks
    private NoteController noteController;

    private final String rootURL = "/note";

    private static Patient patientGiven;
    private static int patientId = 1;
    private static Note noteGiven;

    @BeforeEach
    void beforeEach(){
        noteController.localMode=false;
    }

    static {
        patientGiven = new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test");
        patientGiven.setId(patientId);
        noteGiven = new Note(patientId,"le_texte_de_note",
                LocalDate.now().minus(14, ChronoUnit.DAYS));
    }

    @Order(1)
    @Test
    void list() throws Exception {
        //***********GIVEN*************
        String uri = "/"+ patientId +"/list";
        when(noteRestService.getList(patientId)).thenReturn(List.of(noteGiven));
        when(patientRestService.getById(anyInt())).thenReturn(patientGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(rootURL+ uri)
                .accept(MediaType.TEXT_HTML_VALUE);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(noteRestService, Mockito.never()).getList(patientId);

        //**************WHEN-THEN****************************
        MvcResult mvcResult =  mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note/list"))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_HTML_VALUE+";charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Historical for Patient")))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("patient", patientGiven))
                .andReturn();

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(noteRestService, Mockito.times(1)).getList(patientId);
        verify(patientRestService, Mockito.times(1)).getById(patientId);
    }

    @DisplayName("Show Add Form")
    @Order(2)
    @Test
    void addNoteForm() throws Exception {
        //***********GIVEN*************
        String uri = "/"+ patientId +"/add";
        when(patientRestService.getById(anyInt())).thenReturn(patientGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(rootURL + uri)
                .accept(MediaType.TEXT_HTML_VALUE);

        //**************WHEN-THEN****************************
        MvcResult mvcResult =  mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note/add"))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_HTML_VALUE+";charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Add New Note for Patient")))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("patient", patientGiven))
                .andReturn();
        verify(patientRestService, Mockito.times(1)).getById(patientId);
    }

    @DisplayName("Add - Validate - Ok")
    @Order(3)
    @Test
    void validate_validNote() throws Exception {
        //***********GIVEN*************
        String uri = "/"+ patientId +"/validate";
        noteGiven.setRecordDate(null);
        when(noteRestService.add(noteGiven)).thenReturn(noteGiven);
        when(patientRestService.getById(anyInt())).thenReturn(patientGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(rootURL+ uri)
                .param("patientId", String.valueOf(patientId))
                .param("note", noteGiven.getNote())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .accept(MediaType.TEXT_HTML_VALUE);

        //String urlDestination =  UriUtils.encode("list", "UTF-8");
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(noteRestService, Mockito.never()).add(noteGiven);

        //**************WHEN-THEN****************************
        MvcResult mvcResult =  mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isFound())//302 redirection
                .andExpect(redirectedUrl(rootURL + "/"+ patientId +"/list"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/note/"+ patientId +"/list"))
                //.andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_HTML_VALUE+";charset=UTF-8"))
                //.andExpect(MockMvcResultMatchers.content().string(containsString("Historical for Patient")))
                //.andExpect(model().attributeExists("patient"))
                //.andExpect(model().attribute("patient", patientGiven))
                .andReturn();

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(noteRestService, Mockito.times(1)).add(noteGiven);//ArgumentMatchers.refEq(curvePointCreated)
        verify(patientRestService, Mockito.times(1)).getById(patientId);
    }

    @DisplayName("Show Update Form")
    @Order(4)
    @Test
    void updateNoteForm() throws Exception {
        //***********GIVEN*************
        noteGiven.setId("1");
        String uri = "/"+ patientId +"/update/"+ noteGiven.getId();
        when(patientRestService.getById(anyInt())).thenReturn(patientGiven);
        when(noteRestService.getById(anyString())).thenReturn(noteGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(rootURL + uri)
                .accept(MediaType.TEXT_HTML_VALUE);

        //**************WHEN-THEN****************************
        MvcResult mvcResult =  mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note/update"))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_HTML_VALUE+";charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Update Note for Patient")))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("patient", patientGiven))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", noteGiven))
                .andReturn();
        verify(patientRestService, Mockito.times(1)).getById(patientId);
        verify(noteRestService, Mockito.times(1)).getById(noteGiven.getId());
    }

    @DisplayName("Update Note")
    @Order(5)
    @Test
    void updateNote() throws Exception {
        //***********GIVEN*************
        noteGiven.setId("1");
        noteGiven.setRecordDate(null);
        String uri = "/"+ patientId +"/update";
        when(patientRestService.getById(anyInt())).thenReturn(patientGiven);
        when(noteRestService.getById(anyString())).thenReturn(noteGiven);
        when(noteRestService.update(noteGiven)).thenReturn(noteGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(rootURL + uri)
                .param("id", noteGiven.getId())
                .param("patientId", String.valueOf(patientId))
                .param("note", noteGiven.getNote())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .accept(MediaType.TEXT_HTML_VALUE);

        //**************WHEN-THEN****************************
        MvcResult mvcResult =  mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isFound())//302 redirection
                .andExpect(redirectedUrl(rootURL + "/"+ patientId +"/list"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/note/"+ patientId +"/list"))
                .andReturn();
        verify(patientRestService, Mockito.times(1)).getById(patientId);
        verify(noteRestService, Mockito.times(1)).getById(noteGiven.getId());
    }
}