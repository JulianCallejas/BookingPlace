package com.rustik.rustik.service;


import com.rustik.rustik.model.Feature;
import com.rustik.rustik.repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;

    @Autowired
    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public List<Feature> findAll() {
        return featureRepository.findAll();
    }

    public Feature findById(Long id) {
        return featureRepository.findById(id).orElse(null);
    }

    public Feature save(Feature feature) {
        return featureRepository.save(feature);
    }

    public void delete(Long id) {
        featureRepository.deleteById(id);
    }
}