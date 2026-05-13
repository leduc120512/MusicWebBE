package com.musicapi.service;

import com.musicapi.model.PopupAd;
import com.musicapi.repository.PopupAdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PopupAdService {
    @Autowired
    private PopupAdRepository popupAdRepository;

    public List<PopupAd> getAll() {
        return popupAdRepository.findAll();
    }

    public Optional<PopupAd> getActive() {
        return popupAdRepository.findActivePopup(LocalDateTime.now(), PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }

    public PopupAd getById(Long id) {
        return popupAdRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Popup ad not found"));
    }

    public PopupAd create(PopupAd popupAd) {
        return popupAdRepository.save(popupAd);
    }

    public PopupAd update(Long id, PopupAd request) {
        PopupAd popupAd = getById(id);
        if (request.getTitle() != null) popupAd.setTitle(request.getTitle());
        if (request.getContent() != null) popupAd.setContent(request.getContent());
        if (request.getImage() != null) popupAd.setImage(request.getImage());
        if (request.getTargetUrl() != null) popupAd.setTargetUrl(request.getTargetUrl());
        popupAd.setActive(request.isActive());
        popupAd.setStartAt(request.getStartAt());
        popupAd.setEndAt(request.getEndAt());
        return popupAdRepository.save(popupAd);
    }

    public void delete(Long id) {
        popupAdRepository.delete(getById(id));
    }
}
