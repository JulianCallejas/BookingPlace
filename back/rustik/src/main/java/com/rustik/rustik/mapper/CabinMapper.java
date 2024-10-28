package com.rustik.rustik.mapper;
import com.rustik.rustik.dto.CabinDTO;
import com.rustik.rustik.dto.DetailDTO;
import com.rustik.rustik.dto.ImageDTO; // Asegúrate de importar tu ImageDTO
import com.rustik.rustik.model.Cabin;

import java.util.List;
import java.util.stream.Collectors;

public class CabinMapper {

    // Convertir de Cabin a CabinDTO
    public static CabinDTO toDTO(Cabin cabin) {
        CabinDTO dto = new CabinDTO();
        dto.setId(cabin.getId());
        dto.setName(cabin.getName());
        dto.setLocation(cabin.getLocation());
        dto.setCapacity(cabin.getCapacity());
        dto.setDescription(cabin.getDescription());
        dto.setPrice(cabin.getPrice());

        // Convertir la lista de detalles
        List<DetailDTO> detailDTOs = cabin.getCabinFeatures().stream()
                .map(detail -> {
                    DetailDTO detailDTO = new DetailDTO();
                    detailDTO.setId(detail.getId());
                    detailDTO.setFeatureId(detail.getFeature().getId());
                    detailDTO.setFeatureName(detail.getFeature().getName());
                    detailDTO.setQuantity(detail.getQuantity());
                    return detailDTO;
                })
                .collect(Collectors.toList());

        dto.setCabinFeatures(detailDTOs);

        // Convertir la lista de imágenes
        List<ImageDTO> imageDTOs = cabin.getImages().stream()
                .map(image -> {
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setId(image.getId());
                    imageDTO.setUrl(image.getUrl());
                    return imageDTO;
                })
                .collect(Collectors.toList());

        dto.setImages(imageDTOs);
        return dto;
    }

    // Convertir de CabinDTO a Cabin
    public static Cabin toEntity(CabinDTO dto) {
        Cabin cabin = new Cabin();
        cabin.setId(dto.getId());
        cabin.setName(dto.getName());
        cabin.setLocation(dto.getLocation());
        cabin.setCapacity(dto.getCapacity());
        cabin.setDescription(dto.getDescription());
        cabin.setPrice(dto.getPrice());

        // Aquí podrías agregar la conversión de los detalles y las imágenes si lo necesitas
        return cabin;
    }
}