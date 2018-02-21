package com.ovoenergy.offer.db.jdbc.impl;

import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Component
public class JdbcHelperImpl implements JdbcHelper {

    private static final String SELECT_CURRENT_TIME = "SELECT NOW() as current_date_time";

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    @Transactional(propagation = REQUIRED)
    public Date lookupCurrentDbTime() {
        return jdbc.queryForObject(SELECT_CURRENT_TIME, (rs, rowNum) -> new Date(rs.getTimestamp("current_date_time").getTime()));
    }
}
