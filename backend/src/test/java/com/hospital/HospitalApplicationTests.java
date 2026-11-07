package com.hospital;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class HospitalApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context, JPA mapping, and beans initialize correctly
    }
}
