package com.ovoenergy.offer.rest;

import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.validation.key.ValidationCodeMessageKeyPair;
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

import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocaleContextHolder.class, ValidationCodeMessageKeyPair.class})
public class InternalExceptionHandlerTest {

    private static final Locale LOCALE = Locale.ENGLISH;

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @InjectMocks
    private InternalExceptionHandler handler;

    @Mock
    private MessageSource msgSource;

    @Before
    public void setUp() {
        mockStatic(LocaleContextHolder.class);
        PowerMockito.when(LocaleContextHolder.getLocale()).thenReturn(LOCALE);
        when(msgSource.getMessage(any(), any(), eq(LOCALE))).thenReturn(ERROR_MESSAGE);

        mockStatic(ValidationCodeMessageKeyPair.class);
        PowerMockito.when(ValidationCodeMessageKeyPair.getMessageByCode(eq(ERROR_MESSAGE))).thenReturn(ERROR_MESSAGE);
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
    public void testProcessGenericError() {
        Exception ex = new RuntimeException("smth went wrong");
        ResponseEntity<ErrorMessageDTO> response = handler.processGenericError(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testProcessIOExceptionBrokenPipe() {
        IOException ex = new IOException("Broken pipe");


        ResponseEntity<ErrorMessageDTO> result = handler.processIOException(ex);
        assertNull(result);
    }

    @Test
    public void testProcessIOException() {
        IOException ex = new IOException("");
        when(msgSource.getMessage(any(), any(), eq(LOCALE))).thenReturn(ERROR_MESSAGE);

        ResponseEntity<ErrorMessageDTO> result = handler.processIOException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(ERROR_MESSAGE, result.getBody().getMessage());
    }

    @Test
    public void processVariableNotValidError() {
        VariableNotValidException ex = new VariableNotValidException("input interface validation exception");

        ResponseEntity<ErrorMessageDTO> response = handler.processVariableNotValidError(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ERROR_MESSAGE, response.getBody().getMessage());
    }

}
