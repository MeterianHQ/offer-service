package com.ovoenergy.offer.task;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.db.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
    private OfferRepository offerCustomRepository;

    @Override
    protected void runTask() {
        LOGGER.info("Task started for offers status update process");
        Date now = jdbcHelper.lookupCurrentDbTime();
        List<OfferDBEntity> offersToExpire = offerCustomRepository.findAllByStatusAndExpiryDateLessThan(StatusType.ACTIVE, getDateTimeMidnightMilliseconds(now));
        Set<Long> idsToExpire = offersToExpire.stream().map(OfferDBEntity::getId).collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(offersToExpire)) {
            LOGGER.debug("Started offers status update to process");
            LOGGER.info("Updating offers with IDs = {} with Expired status. Count {}. Execution time {}", idsToExpire, idsToExpire.size(), now);
            int updated = offerCustomRepository.updateExpiredOffersStatus(now.getTime(), getDateTimeMidnightMilliseconds(now));
            LOGGER.info("Updated offers with Expired status. Count {}. Execution time {}", updated, now);
            LOGGER.info("Task completed for offers status update process");
        } else {
            LOGGER.info("Skipped offers update with Expired status");
        }
    }

    private Long getDateTimeMidnightMilliseconds(Date date) {
        return LocalDateTime.of(date.toInstant().atZone(ZoneId.of("UTC")).toLocalDate(), LocalTime.MIDNIGHT).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
    }

}