package com.rustik.rustik.test.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rustik.rustik.controller.CabinController;
import com.rustik.rustik.dto.CabinDTO;
import com.rustik.rustik.mapper.CabinMapper;
import com.rustik.rustik.model.Cabin;
import com.rustik.rustik.model.CabinCategory;
import com.rustik.rustik.model.Detail;
import com.rustik.rustik.model.Image;
import com.rustik.rustik.service.CabinService;
import com.rustik.rustik.service.RatingService;
import io.vavr.control.Either;
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
import static org.mockito.Mockito.*;
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

    @Test
    void testCreateCabin() throws Exception {

        CabinDTO cabinDTO = new CabinDTO(1L, "Cabaña A", "Ubicación A", 4, "Descripción", 100.0, "MODERNA");

        Cabin cabin = new Cabin(1L, cabinDTO.getName(), cabinDTO.getLocation(), cabinDTO.getCapacity(),
                cabinDTO.getDescription(), cabinDTO.getPrice(), CabinCategory.valueOf(cabinDTO.getCategory()),
                new ArrayList<>(), new ArrayList<>());

        CabinDTO savedCabinDTO = CabinMapper.toDTO(cabin);

        when(cabinService.save(any(CabinDTO.class))).thenReturn(Either.right(savedCabinDTO));

        mockMvc.perform(post("/api/v1/cabins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cabinDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cabaña A"))
                .andExpect(jsonPath("$.location").value("Ubicación A"))
                .andExpect(jsonPath("$.capacity").value(4))
                .andExpect(jsonPath("$.description").value("Descripción"))
                .andExpect(jsonPath("$.price").value(100.0))
                .andExpect(jsonPath("$.category").value("MODERNA"))
                .andExpect(jsonPath("$.cabinFeatures").isEmpty())
                .andExpect(jsonPath("$.images").isEmpty());
    }

    @Test
    void testUpdateCabin() throws Exception {
        Long cabinId = 1L;
        CabinDTO cabinDTO = new CabinDTO(cabinId, "Cabaña B", "Ubicación B", 6, "Descripción B", 150.0, "MODERNA");

        // Simulando la respuesta de la llamada al servicio con una entidad actualizada
        Cabin updatedCabin = new Cabin(cabinId, "Cabaña B", "Ubicación B", 6, "Descripción B", 150.0, CabinCategory.MODERNA, new ArrayList<>(), new ArrayList<>());
        CabinDTO updatedCabinDTO = CabinMapper.toDTO(updatedCabin); // Asegúrate de convertir la entidad a DTO

        when(cabinService.save(any(CabinDTO.class))).thenReturn(Either.right(updatedCabinDTO));

        mockMvc.perform(put("/api/v1/cabins/{id}", cabinId)
                        .contentType(MediaType.APPLICATION_JSON) // El tipo de contenido debe ser JSON
                        .content(new ObjectMapper().writeValueAsString(cabinDTO))) // Serialización de la solicitud
                .andExpect(status().isOk()) // Verifica que la respuesta sea 200 OK
                .andExpect(jsonPath("$.name").value("Cabaña B"))
                .andExpect(jsonPath("$.location").value("Ubicación B"))
                .andExpect(jsonPath("$.capacity").value(6))
                .andExpect(jsonPath("$.description").value("Descripción B"))
                .andExpect(jsonPath("$.price").value(150.0))
                .andExpect(jsonPath("$.category").value("MODERNA"));
    }

    @Test
    void testDeleteCabin() throws Exception {
        Long cabinId = 1L;

        doNothing().when(cabinService).delete(cabinId);

        mockMvc.perform(delete("/api/v1/cabins/{id}", cabinId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCabinNotFound() throws Exception {
        Long cabinId = 1L;

        doThrow(new RuntimeException("Cabaña no encontrada")).when(cabinService).delete(cabinId);

        mockMvc.perform(delete("/api/v1/cabins/{id}", cabinId))
                .andExpect(status().isNotFound());
    }
}
