package de.bund.bva.isyfact.security.test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import de.bund.bva.isyfact.security.example.IsySpringBootApplication;

@SpringBootTest(classes = IsySpringBootApplication.class)
@AutoConfigureMockMvc
public class RolesFromJwtTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldAllowPingFromUserWithRole() throws Exception {
        this.mockMvc.perform(get("/ping")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_test"))))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void shouldDenyPingFromUserWithoutRole() throws Exception {
        this.mockMvc.perform(get("/ping")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_wrong"))))
                .andExpect(status().isForbidden());
    }

}
