package com.rustik.rustik.test.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rustik.rustik.controller.CabinController;
import com.rustik.rustik.dto.CabinDTO;
import com.rustik.rustik.model.Cabin;
import com.rustik.rustik.model.CabinCategory;
import com.rustik.rustik.model.Feature;
import com.rustik.rustik.service.CabinService;
import com.rustik.rustik.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CabinControllerTest {

    @Mock
    private CabinService cabinService;

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private CabinController cabinController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cabinController).build();
    }

    @Test
    void testGetAllCabins() throws Exception {
        //Dto Esperado
        //CabinDTO cabinDTO = new CabinDTO(1L, "Cabaña A", "Ubicación A", 4, "Descripción", 100.0, "MODERNA");
        //List<CabinDTO> cabins = Arrays.asList(cabinDTO);

        //Creacion de Base de datos fake
        List features = new ArrayList();
        List imagenees = new ArrayList();
        Cabin cabin = new Cabin(1L, "Cabaña A", "Ubicación A", 4, "Descripción", 100.0, CabinCategory.MODERNA, features, imagenees  );

        when(cabinService.findAll()).thenReturn(Arrays.asList(cabin)); // Mocking cabinService.findAll()

        mockMvc.perform(get("/api/v1/cabins"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Cabaña A"));
    }


    @Test
    void testGetCabinById() throws Exception {
        Long cabinId = 1L;
        List features = new ArrayList();
        List imagenees = new ArrayList();
        Cabin cabin = new Cabin(1L, "Cabaña A", "Ubicación A", 4, "Descripción", 100.0, CabinCategory.MODERNA, features, imagenees  );
        Map<String, Object> ratingSummary = new HashMap<>();
        ratingSummary.put("averageScore", 4.5);
        ratingSummary.put("totalRatings", 50L);

        when(cabinService.findById(cabinId)).thenReturn(cabin);
        when(ratingService.getRatingSummaryForCabin(cabinId)).thenReturn(ratingSummary);

        mockMvc.perform(get("/api/v1/cabins/{id}", cabinId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cabaña A"))
                .andExpect(jsonPath("$.averageScore").value(4.5))
                .andExpect(jsonPath("$.totalRatings").value(50));
    }

    /*
    @Test
    void testCreateCabin() throws Exception {
        CabinDTO cabinDTO = new CabinDTO(null, "Cabaña B", "Ubicación B", 6, "Descripción B", 150.0, CabinCategory.STANDARD, null, null, null);
        CabinDTO createdCabinDTO = new CabinDTO(1L, "Cabaña B", "Ubicación B", 6, "Descripción B", 150.0, CabinCategory.STANDARD, null, null, null);

        when(cabinService.save(any(CabinDTO.class))).thenReturn(createdCabinDTO);

        mockMvc.perform(post("/api/v1/cabins")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(asJsonString(cabinDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cabaña B"));
    }

    @Test
    void testUpdateCabin() throws Exception {
        Long cabinId = 1L;
        CabinDTO cabinDTO = new CabinDTO(cabinId, "Cabaña B", "Ubicación B", 6, "Descripción B", 150.0, CabinCategory.STANDARD, null, null, null);
        CabinDTO updatedCabinDTO = new CabinDTO(cabinId, "Cabaña B", "Ubicación B", 6, "Descripción B", 150.0, CabinCategory.STANDARD, null, null, null);

        when(cabinService.save(any(CabinDTO.class))).thenReturn(updatedCabinDTO);

        mockMvc.perform(put("/api/v1/cabins/{id}", cabinId)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(asJsonString(cabinDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cabaña B"));
    }

    @Test
    void testDeleteCabin() throws Exception {
        Long cabinId = 1L;

        mockMvc.perform(delete("/api/v1/cabins/{id}", cabinId))
                .andExpect(status().isNoContent());
    }

    private static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

     */
}
