package com.musicapi.service;

import com.musicapi.model.Banner;
import com.musicapi.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    // Update KHÔNG làm mất ảnh cũ
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
        Banner banner = getById(id); // đảm bảo tồn tại
        bannerRepository.delete(banner);
    }
}
