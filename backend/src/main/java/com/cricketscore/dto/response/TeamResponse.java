package com.cricketscore.dto.response;

import java.time.Instant;

public class TeamResponse {
    private Long id;
    private String name;
    private String shortName;
    private String country;
    private String logoUrl;
    private Instant createdAt;

    public TeamResponse() {
    }

    public TeamResponse(Long id, String name, String shortName, String country, String logoUrl, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.country = country;
        this.logoUrl = logoUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
