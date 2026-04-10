package com.musicapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ViolationType {
    COPYRIGHT,
    PLAGIARISM,
    OTHER;

    @JsonCreator
    public static ViolationType fromText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        if ("COMMUNITY".equals(normalized)) {
            return OTHER;
        }
        try {
            return ViolationType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Violation type must be one of: COPYRIGHT, PLAGIARISM, OTHER (COMMUNITY is accepted as OTHER)");
        }
    }
}
