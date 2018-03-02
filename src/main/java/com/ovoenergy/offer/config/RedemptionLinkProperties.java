package com.ovoenergy.offer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.temporal.ChronoUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "redemption.link.expire.time")
public class RedemptionLinkProperties {

    @Min(0)
    @NotNull
    private Long period;
    @NotNull
    private ChronoUnit unit;

    public long getMilliseconds() {
        return unit.getDuration().toMillis() * period;
    }
}
