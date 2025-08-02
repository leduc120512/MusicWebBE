package com.musicapi.service;

import com.musicapi.model.Banner;
import com.musicapi.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    public List<Banner> getAll() {
        return bannerRepository.findAll();
    }

    public Banner getById(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));
    }

    public Banner create(Banner banner) {
        return bannerRepository.save(banner);
    }

    public Banner update(Long id, Banner newBanner) {
        Banner banner = getById(id);
        banner.setImage(newBanner.getImage());
        banner.setNote(newBanner.getNote());
        return bannerRepository.save(banner);
    }

    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }
}
