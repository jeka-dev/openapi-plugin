package your.basepackage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ControllerIT {

   @Autowired
   private MockMvc mvc;

    @Test
    void getPet1Test() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/pet/1"))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

}