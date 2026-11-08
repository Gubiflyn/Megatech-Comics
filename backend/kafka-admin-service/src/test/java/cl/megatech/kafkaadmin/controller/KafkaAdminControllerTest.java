package cl.megatech.kafkaadmin.controller;

import cl.megatech.kafkaadmin.config.SecurityConfig;
import cl.megatech.kafkaadmin.service.KafkaAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KafkaAdminController.class)
@Import(SecurityConfig.class)
class KafkaAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaAdminService kafkaAdminService;

    @Test
    void crearTopico_conNombreVacio_retorna400() throws Exception {

        String body = """
                {
                  "nombre": "",
                  "particiones": 3,
                  "factorReplicacion": 2
                }
                """;

        mockMvc.perform(post("/api/kafka/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarTopico_sinAutenticacion_retorna401() throws Exception {

        mockMvc.perform(delete("/api/kafka/topics/demo.topico"))
                .andExpect(status().isUnauthorized());
    }

}
