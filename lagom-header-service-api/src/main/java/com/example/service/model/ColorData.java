package com.example.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class ColorData {
    @JsonProperty("color")
    private String color;
    @JsonProperty("colorCode")
    private String colorCode;
    @JsonProperty("rgbValues")
    private String rgbValues;

    @JsonCreator
    public ColorData() {
    }

    public String getRgbValues() {
        return rgbValues;
    }

    public void setRgbValues(String rgbValues) {
        this.rgbValues = rgbValues;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

}

