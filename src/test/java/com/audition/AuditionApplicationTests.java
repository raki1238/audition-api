package com.audition;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false"
})
class AuditionApplicationTests {

    // TODO implement unit test. Note that an applicant should create additional unit tests as required.

//    @Test
//    void contextLoads() {
//    }

}
