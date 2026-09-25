package com.nazjara.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nazjara.model.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenAIServiceImplTest {

    @Autowired
    OpenAIServiceImpl openAIService;

    @Test
    void testGetAnswer() {
        var answer = openAIService.getAnswer(new Question("Give me a dad joke"));
        assertThat(answer.answer()).isNotBlank();
    }
}
