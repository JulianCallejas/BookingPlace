package com.rustik.rustik.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rustik.rustik.model.Cabin;
import com.rustik.rustik.model.Image;
import com.rustik.rustik.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final CabinService cabinService; // Para asociar imágenes con cabañas
    private final Cloudinary cloudinary;


    @Autowired
    public ImageService(ImageRepository imageRepository, CabinService cabinService, Cloudinary cloudinary) {
        this.imageRepository = imageRepository;
        this.cabinService = cabinService;
        this.cloudinary = cloudinary; // Usa el bean Cloudinary configurado
    }

    // Método para subir imagen y asociarla con una cabaña
    public Image uploadImage(Long cabinId, MultipartFile file) {
        try {
            // Verifica que el archivo no esté vacío
            if (file.isEmpty()) {
                throw new IllegalArgumentException("El archivo está vacío");
            }

            Cabin cabin = cabinService.findById(cabinId);
            if (cabin == null) {
                throw new IllegalArgumentException("Cabaña no encontrada");
            }

            // Subir el archivo a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("url");

            // Crear y guardar la entidad Image
            Image image = new Image();
            image.setUrl(imageUrl);
            image.setCabin(cabin);
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Error al subir la imagen", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }


    public Image findById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }


    public boolean deleteImage(Long imageId) {
        if (!imageRepository.existsById(imageId)) {
            return false;
        }
        imageRepository.deleteById(imageId);
        return true;
    }
}