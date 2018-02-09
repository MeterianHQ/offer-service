package com.ovoenergy.offer.db.jdbc.impl;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.util.Date;

import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JdbcHelperImpl implements JdbcHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcHelperImpl.class);

    private static final String SELECT_CURRENT_TIME = "SELECT NOW() as current_date_time";

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    @Transactional(propagation = REQUIRED)
    public Date lookupCurrentDbTime() {
        return jdbc.queryForObject(SELECT_CURRENT_TIME, (rs, rowNum) -> new Date(rs.getTimestamp("current_date_time").getTime()));
    }
}
