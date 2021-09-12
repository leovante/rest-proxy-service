package ru.vtb.stub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.vtb.stub.service.AdminValidateService;
import ru.vtb.stub.validate.RouteKey;

import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("${path.admin.validate}")
public class AdminValidateController {

    @Autowired
    private AdminValidateService service;

    @GetMapping
    public ResponseEntity<Object> getValidateData(@RequestParam @RouteKey String key) {
        log.debug("Admin validate controller. Get validate data for key: {}", key);
        return ResponseEntity.ok(service.getValidateData(key));
    }

    @PostMapping
    public ResponseEntity<Void> putValidateData(
            @RequestParam @RouteKey String key,
            @RequestBody(required = false) String body,
            @RequestHeader Map<String, String> headers
    ) {
        log.debug("Admin validate controller. Put validate data for key: {}", key);
        service.putValidateData(key, headers, body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeValidateData(@RequestParam @RouteKey String key) {
        log.debug("Admin validate controller. Delete validate data for key: {}", key);
        return service.removeValidateData(key) != null ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
