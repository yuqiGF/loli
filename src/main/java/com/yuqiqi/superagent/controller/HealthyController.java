package com.yuqiqi.superagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthyController {

    @GetMapping
    public String healthCheck(){
        return "ok";
    }
}
