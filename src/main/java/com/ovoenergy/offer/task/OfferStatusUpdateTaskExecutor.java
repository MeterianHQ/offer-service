package com.ovoenergy.offer.task;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.db.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("offerStatusUpdateTaskExecutor")
public class OfferStatusUpdateTaskExecutor extends BaseTaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferStatusUpdateTaskExecutor.class);

    @Autowired
    protected JdbcHelper jdbcHelper;

    @Autowired
    private OfferRepository offerRepository;

    @Override
    protected void runTask() {
        LOGGER.info("Task started for offers status update process");
        Date now = jdbcHelper.lookupCurrentDbTime();
        List<OfferDBEntity> offersToExpire = offerRepository.findAllByStatusAndExpiryDateLessThan(StatusType.ACTIVE, getDateTimeMidnightMilliseconds(now));
        Set<Long> idsToExpire = offersToExpire.stream().map(OfferDBEntity::getId).collect(Collectors.toSet());

        LOGGER.debug("Started offers status update to process");
        LOGGER.info("Updating offers with IDs = {} with Expired status. Execution time {}", idsToExpire, now);
        offersToExpire.stream().forEach(off -> {
            off.setStatus(StatusType.EXPIRED);
            off.setUpdatedOn(now.getTime());
        });
        offerRepository.save(offersToExpire);
        LOGGER.info("Entities with IDs = {} were successfully updated with Expired status. Execution time {}", idsToExpire, now);
        LOGGER.info("Task completed for offers status update process");
    }

    private Long getDateTimeMidnightMilliseconds(Date date) {
        return LocalDateTime.of(date.toInstant().atZone(ZoneId.of("UTC")).toLocalDate(), LocalTime.MIDNIGHT).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
    }

}