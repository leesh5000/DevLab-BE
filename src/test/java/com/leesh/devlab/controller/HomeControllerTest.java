package com.leesh.devlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leesh.devlab.config.LocaleConfig;
import com.leesh.devlab.config.TimezoneConfig;
import com.leesh.devlab.constant.dto.HealthCheckDto;
import config.WebMvcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@AutoConfigureRestDocs
@Import({WebMvcTestConfig.class, TimezoneConfig.class, LocaleConfig.class})
public class HomeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Test
    void healthCheck_test() throws Exception {

        // given
        HealthCheckDto response = new HealthCheckDto("ok", "ko_KR", "Asia/Seoul", "test", "local", "dev");

        // when
        ResultActions result = mvc.perform(get("/api/health")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(response.status()))
                .andExpect(jsonPath("$.locale").value(response.locale()))
                .andExpect(jsonPath("$.timezone").value(response.timezone()))
                .andExpect(jsonPath("$.active_profiles").isArray())
                .andDo(print());

        // API Docs
        result.andDo(document("health-check",
                responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                        fieldWithPath("locale").type(JsonFieldType.STRING).description("로케일"),
                        fieldWithPath("timezone").type(JsonFieldType.STRING).description("타임존"),
                        fieldWithPath("active_profiles").type(JsonFieldType.ARRAY).description("현재 서버에 적용된 프로파일")
                )
        ));

    }

}
