package com.rustik.rustik.controller;


import com.rustik.rustik.dto.CabinDTO;
import com.rustik.rustik.exception.BadRequestException;
import com.rustik.rustik.mapper.CabinMapper;
import com.rustik.rustik.model.Cabin;
import com.rustik.rustik.model.CabinCategory;
import com.rustik.rustik.security.CustomUserDetails;
import com.rustik.rustik.service.CabinService;
import com.rustik.rustik.service.ImageService;
import com.rustik.rustik.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cabins")
public class CabinController {

    private final CabinService cabinService;
    private final RatingService ratingService;

    @Autowired
    public CabinController(CabinService cabinService, ImageService imageService, RatingService ratingService) {
        this.cabinService = cabinService;
        this.ratingService = ratingService;
    }


    @Operation(summary = "Get all cabins", description = "Devuelve una lista de todas las cabañas disponibles.")
    @ApiResponse(responseCode = "200", description = "Lista de cabañas obtenida exitosamente.")
    @GetMapping
    public List<CabinDTO> getAllCabins() {
        return cabinService.findAll().stream()
                .map(CabinMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Operation(summary = "Obtener cabaña por ID", description = "Devuelve los detalles de una cabaña específica usando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabaña encontrada."),
            @ApiResponse(responseCode = "404", description = "Cabaña no encontrada.")
    })

    @GetMapping("/{id}")
    public ResponseEntity<CabinDTO> getCabinById(@PathVariable Long id) {
        Cabin cabin = cabinService.findById(id);
        if (cabin == null) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el resumen de la puntuación (promedio y total de valoraciones)
        Map<String, Object> ratingSummary = ratingService.getRatingSummaryForCabin(id);
        Double averageScore = (Double) ratingSummary.get("averageScore");
        Long totalRatings = (Long) ratingSummary.get("totalRatings");

        // Convertir la cabaña a DTO
        CabinDTO cabinDTO = CabinMapper.toDTO(cabin);

        // Agregar el promedio de puntuaciones y el total de valoraciones al DTO
        cabinDTO.setAverageScore(averageScore);
        cabinDTO.setTotalRatings(totalRatings);

        return ResponseEntity.ok(cabinDTO);
    }

    @Operation(summary = "Obtener cabañas aleatorias", description = "Devuelve un número de cabañas aleatorias con su primera imagen.")
    @ApiResponse(responseCode = "200", description = "Cabañas aleatorias obtenidas exitosamente.")
    @GetMapping("/random")
    //Llama 10 cabañas random con su primera imagen
    public ResponseEntity<List<CabinDTO>> getRandomCabins(@RequestParam(defaultValue = "10") int count) {
        List<CabinDTO> randomCabins = cabinService.getRandomCabins(count);
        return ResponseEntity.ok(randomCabins);
    }


    @Operation(summary = "Crear una nueva cabaña", description = "Permite crear una nueva cabaña.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cabaña creada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o datos incorrectos.")
    })
    @SecurityRequirement(name = "bearer")
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> createCabin(@ModelAttribute CabinDTO cabinDTO) {

            Either<List<String>, CabinDTO> result = cabinService.save(cabinDTO);

            return result.fold(
                    errors -> {
                        return ResponseEntity.badRequest().body(errors);
                    },
                    cabin -> {
                        return ResponseEntity.status(HttpStatus.CREATED).body(cabin);
                    }
            );


    }


    @Operation(summary = "Actualizar cabaña", description = "Permite actualizar una cabaña existente .")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabaña actualizada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o datos incorrectos."),
            @ApiResponse(responseCode = "404", description = "Cabaña no encontrada.")
    })
    @SecurityRequirement(name = "bearer")
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> updateCabin(@PathVariable Long id, @ModelAttribute CabinDTO cabinDTO) {

            cabinDTO.setId(id);

            Either<List<String>, CabinDTO> result = cabinService.save(cabinDTO);

        return result.fold(
                errors -> ResponseEntity.badRequest().body(errors),
                cabin -> ResponseEntity.ok().body(cabin)
        );
    }

    @Operation(summary = "Eliminar cabaña", description = "Permite eliminar una cabaña.")
    @ApiResponse(responseCode = "204", description = "Cabaña eliminada exitosamente.")
    @SecurityRequirement(name = "bearer")
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteCabin(@PathVariable Long id) {

            cabinService.delete(id);
            return ResponseEntity.noContent().build();

    }


    @Operation(
            summary = "Filtrar por categoria", description = "Permite filtrar las cabañas disponibles según las categorías proporcionadas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cabañas filtradas por categorías"),
            @ApiResponse(responseCode = "204", description = "No se encontraron cabañas para las categorías proporcionadas")
    })
    @GetMapping("/filter")
    public ResponseEntity<List<CabinDTO>> getCabinsByCategories(@RequestParam List<CabinCategory> categories) {
        List<CabinDTO> cabins = cabinService.getCabinsByCategories(categories);
        if (cabins.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cabins);
    }


    @Operation(
            summary = "Filtrar cabañas por nombre", description = "Permite filtrar las cabañas disponibles según el nombre."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cabañas filtradas por nombre"),
            @ApiResponse(responseCode = "204", description = "No se encontraron cabañas con el nombre proporcionado")
    })
    @GetMapping("/filterByName")
    public ResponseEntity<List<CabinDTO>> getCabinsByName(@RequestParam String name) {
        List<CabinDTO> cabins = cabinService.getCabinsByName(name);
        if (cabins.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cabins);
    }

}