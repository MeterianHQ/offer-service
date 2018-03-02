package com.ovoenergy.offer.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public abstract class AbstractMailCommsProperties {

    @NotBlank
    private String type;
    @NotBlank
    private String name;
    @NotBlank
    private String version;
    @NotBlank
    private String subject;
}
