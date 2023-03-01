package de.bund.bva.isyfact.security.example.rest;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleRestController {

    @RequestMapping("/ping")
    @Secured("PRIV_Recht_A")
    public boolean ping(Authentication authentication) {
        return true;
    }

}
