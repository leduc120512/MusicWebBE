package com.musicapi.service;

import com.musicapi.model.Banner;
import com.musicapi.repository.BannerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<Banner> getAll() {
        return bannerRepository.findAll();
    }

    public Banner getById(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Banner not found"
                        )
                );
    }

    public Banner create(Banner banner) {
        return bannerRepository.save(banner);
    }

    // Update must not drop the existing image
    public Banner update(Long id, Banner newBanner) {
        Banner banner = getById(id);

        if (newBanner.getNote() != null) {
            banner.setNote(newBanner.getNote());
        }

        if (newBanner.getImage() != null && !newBanner.getImage().isBlank()) {
            banner.setImage(newBanner.getImage());
        }

        return bannerRepository.save(banner);
    }

    public void delete(Long id) {
        Banner banner = getById(id); // fails with 404 when it does not exist
        bannerRepository.delete(banner);
    }
}
