package de.bund.bva.isyfact.security.example.rest;

import java.security.Principal;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleRestController {

    @RequestMapping("/ping")
    @Secured("ROLE_test")
    public boolean ping(Principal principal, Authentication authentication) {
        return true;
    }

}
