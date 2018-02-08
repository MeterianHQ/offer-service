package com.ovoenergy.offer.rest;

import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.exception.InternalBaseException;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.validation.key.CodeKeys;
import com.ovoenergy.offer.validation.key.MessageKeys;
import com.ovoenergy.offer.validation.key.ValidationCodeMessageKeyPair;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class InternalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalExceptionHandler.class);

    @Autowired
    private MessageSource msgSource;

    @ExceptionHandler(HttpStatusCodeException.class)
    @ResponseBody
    public ResponseEntity<String> processHttpStatusCodeException(HttpStatusCodeException e) {
        String responseBody = e.getResponseBodyAsString();
        LOGGER.error("Http status code exception occurred: " + responseBody, e);
        return new ResponseEntity<>(responseBody, e.getStatusCode());
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ResponseEntity<ErrorMessageDTO> processIOException(HttpServletRequest req, HttpServletResponse res, IOException ex) {
        LOGGER.error("Unexpected error occurred", ex);
        if (StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(ex), "Broken pipe")) {
            return null;
        } else {
            return new ResponseEntity<>(new ErrorMessageDTO(
                    CodeKeys.GENERIC_SERVER_ERROR,
                    msgSource.getMessage(MessageKeys.Common.GENERIC_SERVER_ERROR, null, LocaleContextHolder.getLocale())),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ErrorMessageDTO> processGenericError(HttpServletRequest req, HttpServletResponse res, Throwable t) throws Throwable {
        LOGGER.error("Unexpected error occurred", t);
        return new ResponseEntity<>(new ErrorMessageDTO(
                CodeKeys.GENERIC_SERVER_ERROR,
                msgSource.getMessage(MessageKeys.Common.GENERIC_SERVER_ERROR, null, LocaleContextHolder.getLocale())),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(VariableNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorMessageDTO> processPathVariableNotValidError(InternalBaseException e) {
        String messageErrorCode = e.getErrorMessageProperty();
        return new ResponseEntity<>(new ErrorMessageDTO(
                messageErrorCode,
                msgSource.getMessage(ValidationCodeMessageKeyPair.getMessageByCode(messageErrorCode), null, LocaleContextHolder.getLocale())), HttpStatus.BAD_REQUEST);
    }

}
