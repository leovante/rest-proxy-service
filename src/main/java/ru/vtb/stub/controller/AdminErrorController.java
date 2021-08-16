package ru.vtb.stub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.vtb.stub.service.AdminErrorService;
import ru.vtb.stub.validate.ResponseKey;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequestMapping("${path.admin.error}")
public class AdminErrorController {

    @Autowired
    private AdminErrorService service;

    @GetMapping
    public ResponseEntity<Object> getErrorData(@RequestParam @ResponseKey String key) {
        log.info("Admin error controller. Get error data for key: {}", key);
        return ResponseEntity.ok(service.getErrorData(key));
    }

    @PostMapping
    public ResponseEntity<Void> putErrorData(
            @RequestParam @ResponseKey String key,
            @RequestParam(required = false) @Digits(integer = 3, fraction = 0) @Min(value = 400) Integer status,
            @RequestParam(required = false) String message
    ) {
        log.info("Admin error controller. Put error data for key: {}", key);
        service.putErrorData(key, message, status);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeErrorData(@RequestParam @ResponseKey String key) {
        log.info("Admin error controller. Delete error data for key: {}", key);
        return service.removeErrorData(key) != null ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
