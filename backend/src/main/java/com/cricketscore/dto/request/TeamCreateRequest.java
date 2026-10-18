package com.cricketscore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TeamCreateRequest {

    @NotBlank(message = "Team name cannot be blank")
    @Size(max = 100, message = "Team name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Short name cannot be blank")
    @Size(min = 2, max = 10, message = "Short name must be between 2 and 10 characters")
    private String shortName;

    @Size(max = 100, message = "Country name cannot exceed 100 characters")
    private String country;

    @Size(max = 500, message = "Logo URL cannot exceed 500 characters")
    private String logoUrl;

    public TeamCreateRequest() {
    }

    public TeamCreateRequest(String name, String shortName, String country, String logoUrl) {
        this.name = name;
        this.shortName = shortName;
        this.country = country;
        this.logoUrl = logoUrl;
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
}
