package com.ovoenergy.offer.task;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.db.repository.OfferRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OfferStatusUpdateTaskExecutorTest {

    @InjectMocks
    private OfferStatusUpdateTaskExecutor offerStatusUpdateTaskExecutor;
    @Mock
    private JdbcHelper jdbcHelper;
    @Mock
    private OfferRepository offerRepository;

    @Test
    public void testRunTaskZeroUpdates() {
        when(jdbcHelper.lookupCurrentDbTime()).thenReturn(new Date());
        when(offerRepository.findAllByStatusAndExpiryDateLessThan(eq(StatusType.ACTIVE), anyLong())).thenReturn(Collections.emptyList());

        offerStatusUpdateTaskExecutor.runTask();

        verify(jdbcHelper, only()).lookupCurrentDbTime();
        verify(offerRepository, only()).findAllByStatusAndExpiryDateLessThan(eq(StatusType.ACTIVE), anyLong());
        verifyNoMoreInteractions(jdbcHelper, offerRepository);
    }

    @Test
    public void testRunTaskUpdates() {
        Date now = new Date();
        when(jdbcHelper.lookupCurrentDbTime()).thenReturn(now);
        when(offerRepository.findAllByStatusAndExpiryDateLessThan(eq(StatusType.ACTIVE), anyLong())).thenReturn(Collections.singletonList(new OfferDBEntity()));
        when(offerRepository.updateExpiredOffersStatus(anyLong(), anyLong())).thenReturn(1);

        offerStatusUpdateTaskExecutor.run();

        verify(jdbcHelper, only()).lookupCurrentDbTime();
        verify(offerRepository, times(1)).findAllByStatusAndExpiryDateLessThan(eq(StatusType.ACTIVE), anyLong());
        verify(offerRepository, times(1)).updateExpiredOffersStatus(eq(now.getTime()), anyLong());
        verifyNoMoreInteractions(jdbcHelper, offerRepository);
    }
}