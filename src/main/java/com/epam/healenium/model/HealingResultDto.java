package com.epam.healenium.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class HealingResultDto {

    private Locator locator;
    private Double score;
    private boolean successHealing;
    private LocalDateTime createDate;

}
