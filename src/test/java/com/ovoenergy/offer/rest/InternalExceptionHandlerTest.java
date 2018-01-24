package com.ovoenergy.offer.rest;

import com.ovoenergy.offer.dto.ErrorMessageDTO;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ovoenergy.offer.test.utils.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocaleContextHolder.class})
@UnitTest
public class InternalExceptionHandlerTest {

    private static final Locale LOCALE = Locale.ENGLISH;

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @InjectMocks
    InternalExceptionHandler handler = new InternalExceptionHandler();

    @Mock
    private HttpServletRequest mockReq;

    @Mock
    private HttpServletResponse mockRes;

    @Mock
    private MessageSource msgSource;

    @Before
    public void setUp() {
        mockStatic(LocaleContextHolder.class);
        PowerMockito.when(LocaleContextHolder.getLocale()).thenReturn(LOCALE);
        when(msgSource.getMessage(any(), any(), eq(LOCALE))).thenReturn(ERROR_MESSAGE);
    }

    @Test
    public void testProcessHttpStatusCodeException() {
        HttpStatusCodeException ex = mock(HttpStatusCodeException.class);
        when(ex.getResponseBodyAsString()).thenReturn(ERROR_MESSAGE);
        when(ex.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        ResponseEntity<String> response = handler.processHttpStatusCodeException(ex);
        assertEquals(ERROR_MESSAGE, response.getBody());
    }

    @Test
    public void testProcessGenericError() throws Throwable {
        Exception ex = new RuntimeException("smth went wrong");
        ResponseEntity<ErrorMessageDTO> response = handler.processGenericError(mockReq, mockRes, ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testProcessIOExceptionBrokenPipe() {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);
        IOException ex = new IOException("Broken pipe");


        ResponseEntity<ErrorMessageDTO> result = handler.processIOException(mockReq, mockRes, ex);
        assertNull(result);
    }

    @Test
    public void testProcessIOException() {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);
        IOException ex = new IOException("");
        when(msgSource.getMessage(any(), any(), eq(LOCALE))).thenReturn(ERROR_MESSAGE);

        ResponseEntity<ErrorMessageDTO> result = handler.processIOException(mockReq, mockRes, ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(ERROR_MESSAGE, result.getBody().getMessage());
    }
}
