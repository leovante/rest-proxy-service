package ru.vtb.stub.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.stub.filter.RequestWrapper;
import ru.vtb.stub.service.ResponseService;

import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
public class ResponseController {

    @Autowired
    private ResponseService service;

    @Operation(hidden = true)
    @RequestMapping(path = "${path.response}", method = {GET, POST, PUT, PATCH, DELETE},
            produces = {APPLICATION_JSON_VALUE, TEXT_PLAIN_VALUE, "application/vnd.schemaregistry.v1+json",
                    APPLICATION_OCTET_STREAM_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> response(
            @RequestParam String rpsRequest,
            @RequestParam String rpsKey,
            RequestWrapper servletRequest
    ) {
        return service.sendResponse(rpsRequest, rpsKey, servletRequest);
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e,
                                                                            HttpServletRequest request) {
        log.error("No acceptable representation found for [{}] | supported {}", request.getHeader("Accept"),
                e.getSupportedMediaTypes());
        return ResponseEntity.badRequest().build();
    }
}
