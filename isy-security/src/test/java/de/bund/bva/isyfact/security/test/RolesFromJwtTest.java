package de.bund.bva.isyfact.security.test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import de.bund.bva.isyfact.security.example.IsySpringBootApplication;

@Disabled("currently only runs as an integration test because of the oauth2 test config")
@SpringBootTest(classes = IsySpringBootApplication.class)
@AutoConfigureMockMvc
public class RolesFromJwtTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldAllowPingFromUserWithRole() throws Exception {
        this.mockMvc.perform(get("/ping")
                        .with(jwt().jwt(jwt -> jwt.claim("roles", Collections.singleton("test")))))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void shouldDenyPingFromUserWithoutRole() throws Exception {
        this.mockMvc.perform(get("/ping")
                        .with(jwt().jwt(jwt -> jwt.claim("roles", Collections.singleton("wrong")))))
                .andExpect(status().isForbidden());
    }

}
