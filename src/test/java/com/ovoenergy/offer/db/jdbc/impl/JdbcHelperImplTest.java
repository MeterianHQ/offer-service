package com.ovoenergy.offer.db.jdbc.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JdbcHelperImplTest {

    private static final Date TEST_NOW_DATE = new Date(System.currentTimeMillis());

    @Mock
    private JdbcTemplate mockJdbc;

    @InjectMocks
    private JdbcHelperImpl unit;

    @Test
    public void lookupCurrentDbTimeSuccess() {
        when(mockJdbc.queryForObject(any(String.class), any(RowMapper.class))).thenReturn(TEST_NOW_DATE);

        Date now = unit.lookupCurrentDbTime();

        assertEquals(TEST_NOW_DATE, now);
        verify(mockJdbc).queryForObject(any(String.class), any(RowMapper.class));
    }
}
