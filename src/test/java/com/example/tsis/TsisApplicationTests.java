package com.example.tsis;

import com.example.tsis.support.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TsisApplicationTests extends PostgresTestContainer {

    @Test
    void contextLoads() {
    }
}
