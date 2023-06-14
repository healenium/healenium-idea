package com.epam.healenium.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

public class HealingResultDto {

    private Locator locator;
    private Double score;
    private boolean successHealing;
    private LocalDateTime createDate;

    public Locator getLocator() {
        return locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean isSuccessHealing() {
        return successHealing;
    }

    public void setSuccessHealing(boolean successHealing) {
        this.successHealing = successHealing;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
