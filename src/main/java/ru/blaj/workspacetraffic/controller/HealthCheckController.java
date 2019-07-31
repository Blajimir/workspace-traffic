package ru.blaj.workspacetraffic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping(path="/api/health", produces = "application/json;charset=utf-8")
public class HealthCheckController {
    @GetMapping()
    public ResponseEntity<?> getHealth(){
        return ResponseEntity.ok(Collections.singletonMap("health","up"));
    }
}
