package com.cnsia.polarbookshop.catalogservice.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

import com.cnsia.polarbookshop.catalogservice.config.PolarProperties;

@RestController
public class HomeController {

    private final PolarProperties polarProperties;
    private static final Logger log = Logger.getLogger(HomeController.class.getName());

    public HomeController(PolarProperties polarProperties) {
        this.polarProperties = polarProperties;
    }

    @GetMapping("/")
    public String getGreeting() {
        log.info("GET: getGreeting()");
        return polarProperties.getGreeting();
    }
}
