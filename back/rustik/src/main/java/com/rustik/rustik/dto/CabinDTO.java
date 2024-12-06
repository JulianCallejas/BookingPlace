package com.rustik.rustik.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CabinDTO {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private String description;
    private Double price;
    private String category;
    private List<DetailDTO> cabinFeatures; // Lista de detalles
    private List<ImageDTO> images = new ArrayList<>(); // Lista de url
    private List<RatingDTO> ratings;
    private Double averageScore;
    private Long totalRatings;

    @JsonIgnore
    private List<MultipartFile> imagesToUpload = new ArrayList<>(); // Lista de imagenes

    public CabinDTO(Long id, String name, String location, Integer capacity, String description, Double price, String category) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
        this.price = price;
        this.category = category;
    }
}
